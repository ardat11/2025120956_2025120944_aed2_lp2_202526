package services;

import database.StreamingDatabase;
import database.StreamingGraph;
import models.RelationshipEdge;
import models.Content;
import models.User;
import models.Artist;
import models.ContentType;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.SeparateChainingHashST;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.DijkstraSP;
import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.CC;
import edu.princeton.cs.algs4.SET;
import java.time.LocalDate;

/**
 * Service class for graph operations and analysis.
 */
public class GraphService {
    private StreamingDatabase db;
    private StreamingGraph graph;

    public GraphService(StreamingDatabase db) {
        this.db = db;
        this.graph = db.getGraph();
    }

    /**
     * Finds the shortest path between two users using only FOLLOWS relationships.
     *
     * @param fromUserId source user identifier
     * @param toUserId target user identifier
     * @return sequence of user IDs representing the path
     */
    public Iterable<String> shortestPathUsersByFollow(String fromUserId, String toUserId) {
        Queue<String> path = new Queue<>();
        if (fromUserId == null || toUserId == null) return path;

        // Map active vertices (Users) to contiguous indices
        SeparateChainingHashST<String, Integer> tempMap = new SeparateChainingHashST<>();
        SeparateChainingHashST<Integer, String> reverseMap = new SeparateChainingHashST<>();
        int tempIndex = 0;
        for (String id : graph.vertices()) {
            if (db.getUser(id) != null) {
                tempMap.put(id, tempIndex);
                reverseMap.put(tempIndex, id);
                tempIndex++;
            }
        }

        if (!tempMap.contains(fromUserId) || !tempMap.contains(toUserId)) return path;

        EdgeWeightedDigraph digraph = new EdgeWeightedDigraph(tempIndex);
        for (String from : tempMap.keys()) {
            int v = tempMap.get(from);
            for (RelationshipEdge edge : graph.adj(from)) {
                if (edge.getRelationType().equals("FOLLOWS") && tempMap.contains(edge.getToId())) {
                    int w = tempMap.get(edge.getToId());
                    digraph.addEdge(new DirectedEdge(v, w, edge.weight()));
                }
            }
        }

        DijkstraSP sp = new DijkstraSP(digraph, tempMap.get(fromUserId));
        int end = tempMap.get(toUserId);
        if (sp.hasPathTo(end)) {
            for (DirectedEdge edge : sp.pathTo(end)) {
                path.enqueue(reverseMap.get(edge.to()));
            }
            Queue<String> fullPath = new Queue<>();
            fullPath.enqueue(fromUserId);
            for (String node : path) {
                fullPath.enqueue(node);
            }
            return fullPath;
        }
        return path;
    }

    /**
     * Finds the shortest path between two users based on shared movie contents watched.
     * Treats film watch relationships as undirected connections.
     *
     * @param fromUserId source user identifier
     * @param toUserId target user identifier
     * @param fullyWatchedOnly filter to only consider fully watched contents
     * @return sequence of entity IDs representing the path
     */
    public Iterable<String> shortestPathUsersByFilm(String fromUserId, String toUserId, boolean fullyWatchedOnly) {
        Queue<String> path = new Queue<>();
        if (fromUserId == null || toUserId == null) return path;

        // Map active vertices (Users and Movie Content)
        SeparateChainingHashST<String, Integer> tempMap = new SeparateChainingHashST<>();
        SeparateChainingHashST<Integer, String> reverseMap = new SeparateChainingHashST<>();
        int tempIndex = 0;

        for (String id : graph.vertices()) {
            boolean isUser = db.getUser(id) != null;
            Content c = db.getContent(id);
            boolean isMovie = c != null && c.getType() == ContentType.MOVIE;
            if (isUser || isMovie) {
                tempMap.put(id, tempIndex);
                reverseMap.put(tempIndex, id);
                tempIndex++;
            }
        }

        if (!tempMap.contains(fromUserId) || !tempMap.contains(toUserId)) return path;

        EdgeWeightedDigraph digraph = new EdgeWeightedDigraph(tempIndex);
        for (String from : tempMap.keys()) {
            if (db.getUser(from) != null) {
                int v = tempMap.get(from);
                for (RelationshipEdge edge : graph.adj(from)) {
                    if (edge.getRelationType().equals("WATCHES")) {
                        if (fullyWatchedOnly && !edge.isFullyWatched()) continue;
                        if (tempMap.contains(edge.getToId())) {
                            int w = tempMap.get(edge.getToId());
                            // Add bidirectional edges to represent undirected path
                            digraph.addEdge(new DirectedEdge(v, w, edge.weight()));
                            digraph.addEdge(new DirectedEdge(w, v, edge.weight()));
                        }
                    }
                }
            }
        }

        DijkstraSP sp = new DijkstraSP(digraph, tempMap.get(fromUserId));
        int end = tempMap.get(toUserId);
        if (sp.hasPathTo(end)) {
            for (DirectedEdge edge : sp.pathTo(end)) {
                path.enqueue(reverseMap.get(edge.to()));
            }
            Queue<String> fullPath = new Queue<>();
            fullPath.enqueue(fromUserId);
            for (String node : path) {
                fullPath.enqueue(node);
            }
            return fullPath;
        }
        return path;
    }

    /**
     * Finds the shortest path between two users based on shared series contents watched.
     * Treats series watch relationships as undirected connections.
     * Supports filtering by fully watched and specific episode tags.
     *
     * @param fromUserId source user identifier
     * @param toUserId target user identifier
     * @param fullyWatchedOnly filter to only consider fully watched contents
     * @param episodeFilter optional substring filter for episode tags (e.g. "Ep1")
     * @return sequence of entity IDs representing the path
     */
    public Iterable<String> shortestPathUsersBySeries(String fromUserId, String toUserId, boolean fullyWatchedOnly, String episodeFilter) {
        Queue<String> path = new Queue<>();
        if (fromUserId == null || toUserId == null) return path;

        // Map active vertices (Users and Series Content)
        SeparateChainingHashST<String, Integer> tempMap = new SeparateChainingHashST<>();
        SeparateChainingHashST<Integer, String> reverseMap = new SeparateChainingHashST<>();
        int tempIndex = 0;

        for (String id : graph.vertices()) {
            boolean isUser = db.getUser(id) != null;
            Content c = db.getContent(id);
            boolean isSeries = c != null && c.getType() == ContentType.SERIES;
            if (isUser || isSeries) {
                tempMap.put(id, tempIndex);
                reverseMap.put(tempIndex, id);
                tempIndex++;
            }
        }

        if (!tempMap.contains(fromUserId) || !tempMap.contains(toUserId)) return path;

        EdgeWeightedDigraph digraph = new EdgeWeightedDigraph(tempIndex);
        for (String from : tempMap.keys()) {
            if (db.getUser(from) != null) {
                int v = tempMap.get(from);
                for (RelationshipEdge edge : graph.adj(from)) {
                    if (edge.getRelationType().equals("WATCHES")) {
                        if (fullyWatchedOnly && !edge.isFullyWatched()) continue;
                        if (episodeFilter != null && (edge.getDetails() == null || !edge.getDetails().contains(episodeFilter))) continue;
                        if (tempMap.contains(edge.getToId())) {
                            int w = tempMap.get(edge.getToId());
                            // Add bidirectional edges to represent undirected path
                            digraph.addEdge(new DirectedEdge(v, w, edge.weight()));
                            digraph.addEdge(new DirectedEdge(w, v, edge.weight()));
                        }
                    }
                }
            }
        }

        DijkstraSP sp = new DijkstraSP(digraph, tempMap.get(fromUserId));
        int end = tempMap.get(toUserId);
        if (sp.hasPathTo(end)) {
            for (DirectedEdge edge : sp.pathTo(end)) {
                path.enqueue(reverseMap.get(edge.to()));
            }
            Queue<String> fullPath = new Queue<>();
            fullPath.enqueue(fromUserId);
            for (String node : path) {
                fullPath.enqueue(node);
            }
            return fullPath;
        }
        return path;
    }

    /**
     * Extracts a subgraph containing only content of a given genre.
     *
     * @param genreId genre identifier to filter
     * @return the extracted subgraph
     */
    public StreamingGraph extractGenreSubgraph(String genreId) {
        StreamingGraph subgraph = new StreamingGraph();
        if (genreId == null) return subgraph;

        String genreUpper = genreId.toUpperCase();
        for (String vertex : graph.vertices()) {
            for (RelationshipEdge edge : graph.adj(vertex)) {
                Content content = db.getContent(edge.getToId());
                if (content != null && content.getGenre() != null && content.getGenre().getId().toUpperCase().equals(genreUpper)) {
                    subgraph.addEdge(edge.getFromId(), edge.getToId(), edge.weight(), edge.getRelationType(), edge.getTimestamp(), edge.isFullyWatched(), edge.getDetails());
                }
            }
        }
        return subgraph;
    }

    /**
     * Extracts a subgraph containing only users, content of a given genre, and their watch/rate relationships.
     *
     * @param genreId genre identifier to filter
     * @return the extracted subgraph
     */
    public StreamingGraph extractGenreUserSubgraph(String genreId) {
        StreamingGraph subgraph = new StreamingGraph();
        if (genreId == null) return subgraph;

        String genreUpper = genreId.toUpperCase();
        for (String vertex : graph.vertices()) {
            User user = db.getUser(vertex);
            if (user != null) {
                for (RelationshipEdge edge : graph.adj(vertex)) {
                    String relType = edge.getRelationType();
                    if (relType.equals("WATCHES") || relType.equals("RATES")) {
                        Content content = db.getContent(edge.getToId());
                        if (content != null && content.getGenre() != null && content.getGenre().getId().toUpperCase().equals(genreUpper)) {
                            subgraph.addEdge(edge.getFromId(), edge.getToId(), edge.weight(), edge.getRelationType(), edge.getTimestamp(), edge.isFullyWatched(), edge.getDetails());
                        }
                    }
                }
            }
        }
        return subgraph;
    }

    /**
     * Extracts a subgraph containing only users of a given region and their watched/rated content.
     *
     * @param region region identifier (e.g. "US", "Europe")
     * @return the extracted subgraph
     */
    public StreamingGraph extractRegionSubgraph(String region) {
        StreamingGraph subgraph = new StreamingGraph();
        if (region == null) return subgraph;

        String regionUpper = region.toUpperCase();
        for (String vertex : graph.vertices()) {
            User user = db.getUser(vertex);
            if (user != null && user.getRegion() != null && user.getRegion().toUpperCase().equals(regionUpper)) {
                for (RelationshipEdge edge : graph.adj(vertex)) {
                    subgraph.addEdge(edge.getFromId(), edge.getToId(), edge.weight(), edge.getRelationType(), edge.getTimestamp(), edge.isFullyWatched(), edge.getDetails());
                }
            }
        }
        return subgraph;
    }

    /**
     * Extracts a subgraph with rating relationships above a minimum threshold.
     *
     * @param minRating minimum rating score (inclusive)
     * @return the extracted subgraph
     */
    public StreamingGraph extractRatingSubgraph(double minRating) {
        StreamingGraph subgraph = new StreamingGraph();
        for (String vertex : graph.vertices()) {
            for (RelationshipEdge edge : graph.adj(vertex)) {
                if (edge.getRelationType().equals("RATES") && edge.weight() >= minRating) {
                    subgraph.addEdge(edge.getFromId(), edge.getToId(), edge.weight(), edge.getRelationType(), edge.getTimestamp(), edge.isFullyWatched(), edge.getDetails());
                }
            }
        }
        return subgraph;
    }

    /**
     * Extracts a subgraph containing Users, Artists, and their related Content items.
     * Maps user interactions and artist cast participations.
     *
     * @return the extracted subgraph
     */
    public StreamingGraph extractUserArtistSubgraph() {
        StreamingGraph subgraph = new StreamingGraph();
        for (String vertex : graph.vertices()) {
            boolean isUser = db.getUser(vertex) != null;
            boolean isArtist = db.getArtist(vertex) != null;
            boolean isContent = db.getContent(vertex) != null;

            if (isUser || isArtist || isContent) {
                for (RelationshipEdge edge : graph.adj(vertex)) {
                    String toId = edge.getToId();
                    boolean toIsUser = db.getUser(toId) != null;
                    boolean toIsArtist = db.getArtist(toId) != null;
                    boolean toIsContent = db.getContent(toId) != null;

                    if (toIsUser || toIsArtist || toIsContent) {
                        subgraph.addEdge(edge.getFromId(), edge.getToId(), edge.weight(), edge.getRelationType(), edge.getTimestamp(), edge.isFullyWatched(), edge.getDetails());
                    }
                }
            }
        }
        return subgraph;
    }

    /**
     * Extracts a subgraph containing only Users and their follow relationships.
     * Maps user-to-user social connections.
     *
     * @return the extracted subgraph
     */
    public StreamingGraph extractUserUserSubgraph() {
        StreamingGraph subgraph = new StreamingGraph();
        for (String vertex : graph.vertices()) {
            if (db.getUser(vertex) != null) {
                for (RelationshipEdge edge : graph.adj(vertex)) {
                    if (edge.getRelationType().equals("FOLLOWS") && db.getUser(edge.getToId()) != null) {
                         subgraph.addEdge(edge.getFromId(), edge.getToId(), edge.weight(), edge.getRelationType(), edge.getTimestamp(), edge.isFullyWatched(), edge.getDetails());
                    }
                }
            }
        }
        return subgraph;
    }

    /**
     * Extracts a subgraph containing only Artists, Content items, and their participation relationships.
     * Maps artist participation in media contents.
     *
     * @return the extracted subgraph
     */
    public StreamingGraph extractArtistContentSubgraph() {
        StreamingGraph subgraph = new StreamingGraph();
        for (String vertex : graph.vertices()) {
            boolean isArtist = db.getArtist(vertex) != null;
            boolean isContent = db.getContent(vertex) != null;
            if (isArtist || isContent) {
                for (RelationshipEdge edge : graph.adj(vertex)) {
                    String toId = edge.getToId();
                    boolean toIsArtist = db.getArtist(toId) != null;
                    boolean toIsContent = db.getContent(toId) != null;
                    if (toIsArtist || toIsContent) {
                        subgraph.addEdge(edge.getFromId(), edge.getToId(), edge.weight(), edge.getRelationType(), edge.getTimestamp(), edge.isFullyWatched(), edge.getDetails());
                    }
                }
            }
        }
        return subgraph;
    }

    /**
     * Calculates the shortest path between two artists based on content they both participated in.
     * Operates on the provided subgraph (e.g. extractUserArtistSubgraph()) rather than the full graph.
     * Treats participation as undirected connections.
     *
     * @param fromArtistId source artist identifier
     * @param toArtistId target artist identifier
     * @param subgraph the subgraph to operate on (e.g. user-artist subgraph)
     * @return sequence of entity IDs representing the path
     */
    public Iterable<String> shortestPathBetweenArtists(String fromArtistId, String toArtistId, StreamingGraph subgraph) {
        Queue<String> path = new Queue<>();
        if (fromArtistId == null || toArtistId == null || subgraph == null) return path;

        // Map active vertices (Artists and Contents)
        SeparateChainingHashST<String, Integer> tempMap = new SeparateChainingHashST<>();
        SeparateChainingHashST<Integer, String> reverseMap = new SeparateChainingHashST<>();
        int tempIndex = 0;

        for (String id : subgraph.vertices()) {
            boolean isArtist = db.getArtist(id) != null;
            boolean isContent = db.getContent(id) != null;
            if (isArtist || isContent) {
                tempMap.put(id, tempIndex);
                reverseMap.put(tempIndex, id);
                tempIndex++;
            }
        }

        if (!tempMap.contains(fromArtistId) || !tempMap.contains(toArtistId)) return path;

        EdgeWeightedDigraph digraph = new EdgeWeightedDigraph(tempIndex);
        // Add edges between content and its cast members
        for (String id : tempMap.keys()) {
            Content content = db.getContent(id);
            if (content != null && content.getCast() != null) {
                int contentIndex = tempMap.get(id);
                for (Artist artist : content.getCast()) {
                    if (tempMap.contains(artist.getId())) {
                        int artistIndex = tempMap.get(artist.getId());
                        // Bipartite relationship represented as undirected
                        digraph.addEdge(new DirectedEdge(artistIndex, contentIndex, 1.0));
                        digraph.addEdge(new DirectedEdge(contentIndex, artistIndex, 1.0));
                    }
                }
            }
        }

        DijkstraSP sp = new DijkstraSP(digraph, tempMap.get(fromArtistId));
        int end = tempMap.get(toArtistId);
        if (sp.hasPathTo(end)) {
            for (DirectedEdge edge : sp.pathTo(end)) {
                path.enqueue(reverseMap.get(edge.to()));
            }
            Queue<String> fullPath = new Queue<>();
            fullPath.enqueue(fromArtistId);
            for (String node : path) {
                fullPath.enqueue(node);
            }
            return fullPath;
        }
        return path;
    }

    /**
     * Checks if the graph or subgraph is connected (weak connectivity).
     *
     * @param subgraph target graph structure to check
     * @return true if connected, false otherwise
     */
    public boolean isConnected(StreamingGraph subgraph) {
        if (subgraph == null || subgraph.V() <= 1) return true;

        SeparateChainingHashST<String, Integer> tempMap = new SeparateChainingHashST<>();
        int tempIndex = 0;
        for (String id : subgraph.vertices()) {
            tempMap.put(id, tempIndex++);
        }

        Graph undirectedGraph = new Graph(tempIndex);
        for (String from : subgraph.vertices()) {
            int v = tempMap.get(from);
            for (RelationshipEdge edge : subgraph.adj(from)) {
                if (tempMap.contains(edge.getToId())) {
                    int w = tempMap.get(edge.getToId());
                    undirectedGraph.addEdge(v, w);
                }
            }
        }

        CC cc = new CC(undirectedGraph);
        return cc.count() == 1;
    }

    /**
     * Generates simple recommendations based on structural proximity.
     * Finds users followed by the user, collects contents they watched,
     * and filters out contents already watched by the target user.
     *
     * @param userId user identifier to generate recommendations for
     * @return collection of recommended contents
     */
    public Iterable<Content> recommendContent(String userId) {
        Queue<Content> recommendations = new Queue<>();
        if (userId == null || db.getUser(userId) == null) return recommendations;

        // Keep track of contents already watched by the user
        SET<String> watchedContentIds = new SET<>();
        for (RelationshipEdge edge : graph.adj(userId)) {
            if (edge.getRelationType().equals("WATCHES")) {
                watchedContentIds.add(edge.getToId());
            }
        }

        // Keep track of added recommendations to avoid duplicates
        SET<String> recommendedContentIds = new SET<>();

        // Find followed users
        for (RelationshipEdge edge : graph.adj(userId)) {
            if (edge.getRelationType().equals("FOLLOWS")) {
                String followedUserId = edge.getToId();
                // Get followed user's watched contents
                for (RelationshipEdge followedEdge : graph.adj(followedUserId)) {
                    if (followedEdge.getRelationType().equals("WATCHES")) {
                        String contentId = followedEdge.getToId();
                        if (!watchedContentIds.contains(contentId) && !recommendedContentIds.contains(contentId)) {
                            Content content = db.getContent(contentId);
                            if (content != null) {
                                recommendations.enqueue(content);
                                recommendedContentIds.add(contentId);
                            }
                        }
                    }
                }
            }
        }
        return recommendations;
    }

    /**
     * Calculates viewing statistics for a specific content item between two dates.
     *
     * @param contentId content identifier
     * @param startDateStr start date in ISO format (yyyy-MM-dd)
     * @param endDateStr end date in ISO format (yyyy-MM-dd)
     * @return formatted statistics string indicating views and hours
     */
    public String getContentViewingStats(String contentId, String startDateStr, String endDateStr) {
        if (contentId == null || startDateStr == null || endDateStr == null) {
            return "views: 0, hours: 0.0";
        }

        LocalDate startDate;
        LocalDate endDate;
        try {
            startDate = LocalDate.parse(startDateStr);
            endDate = LocalDate.parse(endDateStr);
        } catch (Exception e) {
            return "views: 0, hours: 0.0";
        }

        int views = 0;
        double totalHours = 0.0;

        for (String vertex : graph.vertices()) {
            if (db.getUser(vertex) != null) {
                for (RelationshipEdge edge : graph.adj(vertex)) {
                    if (edge.getRelationType().equals("WATCHES") && edge.getToId().equals(contentId)) {
                        LocalDate edgeDate = edge.getTimestamp();
                        if (edgeDate != null && !edgeDate.isBefore(startDate) && !edgeDate.isAfter(endDate)) {
                            views++;
                            totalHours += edge.weight();
                        }
                    }
                }
            }
        }

        return "views: " + views + ", hours: " + totalHours;
    }

    /**
     * Calculates viewing statistics for all contents of a given genre between two dates.
     *
     * @param genreId genre identifier
     * @param startDateStr start date in ISO format (yyyy-MM-dd)
     * @param endDateStr end date in ISO format (yyyy-MM-dd)
     * @return formatted statistics string indicating views and hours
     */
    public String getGenreViewingStats(String genreId, String startDateStr, String endDateStr) {
        if (genreId == null || startDateStr == null || endDateStr == null) {
            return "views: 0, hours: 0.0";
        }

        LocalDate startDate;
        LocalDate endDate;
        try {
            startDate = LocalDate.parse(startDateStr);
            endDate = LocalDate.parse(endDateStr);
        } catch (Exception e) {
            return "views: 0, hours: 0.0";
        }

        String genreUpper = genreId.toUpperCase();
        int views = 0;
        double totalHours = 0.0;

        for (String vertex : graph.vertices()) {
            if (db.getUser(vertex) != null) {
                for (RelationshipEdge edge : graph.adj(vertex)) {
                    if (edge.getRelationType().equals("WATCHES")) {
                        Content content = db.getContent(edge.getToId());
                        if (content != null && content.getGenre() != null && content.getGenre().getId().toUpperCase().equals(genreUpper)) {
                            LocalDate edgeDate = edge.getTimestamp();
                            if (edgeDate != null && !edgeDate.isBefore(startDate) && !edgeDate.isAfter(endDate)) {
                                views++;
                                totalHours += edge.weight();
                            }
                        }
                    }
                }
            }
        }

        return "views: " + views + ", hours: " + totalHours;
    }

    /**
     * Finds users who watched series of a specific genre within a given time interval.
     * Strictly filters for series content types.
     *
     * @param genreId genre identifier to filter
     * @param startDateStr start date in ISO format (yyyy-MM-dd)
     * @param endDateStr end date in ISO format (yyyy-MM-dd)
     * @return collection of matching users
     */
    public Iterable<User> findUsersWithHighWatchTime(String genreId, String startDateStr, String endDateStr) {
        Queue<User> result = new Queue<>();
        if (genreId == null || startDateStr == null || endDateStr == null) {
            return result;
        }

        LocalDate startDate;
        LocalDate endDate;
        try {
            startDate = LocalDate.parse(startDateStr);
            endDate = LocalDate.parse(endDateStr);
        } catch (Exception e) {
            return result;
        }

        String genreUpper = genreId.toUpperCase();

        for (User user : db.listAllUsers()) {
            double watchTime = 0.0;
            for (RelationshipEdge edge : graph.adj(user.getId())) {
                if (edge.getRelationType().equals("WATCHES")) {
                    LocalDate edgeDate = edge.getTimestamp();
                    if (edgeDate != null && !edgeDate.isBefore(startDate) && !edgeDate.isAfter(endDate)) {
                        Content content = db.getContent(edge.getToId());
                        if (content != null && content.getGenre() != null && content.getGenre().getId().toUpperCase().equals(genreUpper) && content.getType() == ContentType.SERIES) {
                            watchTime += edge.weight();
                        }
                    }
                }
            }
            if (watchTime > 0) {
                result.enqueue(user);
            }
        }

        return result;
    }

    /**
     * Determines which followers of a user watched a given film in a given time interval.
     * Strictly filters for movie content types.
     *
     * @param userId target user identifier
     * @param filmId content (film) identifier
     * @param startDateStr start date in ISO format (yyyy-MM-dd)
     * @param endDateStr end date in ISO format (yyyy-MM-dd)
     * @return collection of matching followers (users)
     */
    public Iterable<User> findFollowersWhoWatchedSameFilm(String userId, String filmId, String startDateStr, String endDateStr) {
        Queue<User> result = new Queue<>();
        if (userId == null || filmId == null || startDateStr == null || endDateStr == null) {
            return result;
        }

        Content content = db.getContent(filmId);
        if (content == null || content.getType() != ContentType.MOVIE) {
            return result;
        }

        LocalDate startDate;
        LocalDate endDate;
        try {
            startDate = LocalDate.parse(startDateStr);
            endDate = LocalDate.parse(endDateStr);
        } catch (Exception e) {
            return result;
        }

        // Find followers (users who follow userId, meaning X -> FOLLOWS -> userId)
        SET<String> followerIds = new SET<>();
        for (String v : graph.vertices()) {
            for (RelationshipEdge edge : graph.adj(v)) {
                if (edge.getRelationType().equals("FOLLOWS") && edge.getToId().equals(userId)) {
                    followerIds.add(edge.getFromId());
                }
            }
        }

        // For each follower, check if they watched the film in the time interval
        for (String followerId : followerIds) {
            for (RelationshipEdge edge : graph.adj(followerId)) {
                if (edge.getRelationType().equals("WATCHES") && edge.getToId().equals(filmId)) {
                    LocalDate edgeDate = edge.getTimestamp();
                    if (edgeDate != null && !edgeDate.isBefore(startDate) && !edgeDate.isAfter(endDate)) {
                        User follower = db.getUser(followerId);
                        if (follower != null) {
                            result.enqueue(follower);
                            break; // Avoid adding the same user multiple times for the same film watch
                        }
                    }
                }
            }
        }

        return result;
    }
}
