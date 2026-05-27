package testing;

import database.StreamingDatabase;
import database.StreamingGraph;
import services.GraphService;
import models.*;

import java.time.LocalDate;

/**
 * R5 test unit to validate StreamingGraph structures and GraphService algorithms.
 * Populates sample entities and edges programmatically and prints results to console.
 */
public class GraphTest {

    public static void runGraphTests() {
        System.out.println("Starting Graph Tests");

        StreamingDatabase db = new StreamingDatabase();
        
        // Populate Symbol Tables (STs)
        Genre action = new Genre("G_ACTION", "Action");
        Genre comedy = new Genre("G_COMEDY", "Comedy");
        db.insertGenre(action);
        db.insertGenre(comedy);

        User u1 = new User("U1", "Alice", "Europe", LocalDate.of(2025, 1, 1));
        User u2 = new User("U2", "Bob", "Europe", LocalDate.of(2025, 1, 2));
        User u3 = new User("U3", "Charlie", "America", LocalDate.of(2025, 1, 3));
        User u4 = new User("U4", "David", "America", LocalDate.of(2025, 1, 4));
        db.insertUser(u1);
        db.insertUser(u2);
        db.insertUser(u3);
        db.insertUser(u4);

        Artist a1 = new Artist("A1", "Actor One", "American", "Male", LocalDate.of(1980, 5, 15));
        Artist a2 = new Artist("A2", "Actor Two", "British", "Female", LocalDate.of(1985, 10, 20));
        db.insertArtist(a1);
        db.insertArtist(a2);

        Content c1 = new Content("C1", "Action Movie 1", ContentType.MOVIE, action, LocalDate.of(2024, 6, 1), 120);
        Content c2 = new Content("C2", "Comedy Series 1", ContentType.SERIES, comedy, LocalDate.of(2024, 7, 15), 30);
        db.insertContent(c1);
        db.insertContent(c2);
        
        // Link Artists to Content (implies PARTICIPATED_IN relationship in graph)
        db.addArtistToContent("C1", "A1");
        db.addArtistToContent("C2", "A2");

        // 2. Add Graph Relationships
        System.out.println("Building graph");
        StreamingGraph graph = db.getGraph();

        // U1 follows U2, U2 follows U3
        graph.addEdge("U1", "U2", 1.0, "FOLLOWS", LocalDate.of(2025, 1, 10), false, "");
        graph.addEdge("U2", "U3", 1.0, "FOLLOWS", LocalDate.of(2025, 1, 11), false, "");

        // WATCHES relationships
        graph.addEdge("U1", "C1", 1.5, "WATCHES", LocalDate.of(2025, 1, 12), true, "");
        graph.addEdge("U2", "C1", 0.5, "WATCHES", LocalDate.of(2025, 1, 13), false, "");
        graph.addEdge("U3", "C2", 0.8, "WATCHES", LocalDate.of(2025, 1, 14), true, "S1E1");

        // RATES relationships
        graph.addEdge("U1", "C1", 9.0, "RATES", LocalDate.of(2025, 1, 12), false, "");
        graph.addEdge("U2", "C1", 6.5, "RATES", LocalDate.of(2025, 1, 13), false, "");

        // 3. Initialize GraphService
        GraphService service = new GraphService(db);

        System.out.println("\nTesting R8.a Shortest Paths");
        
        System.out.println("Shortest path by FOLLOWS between U1 and U3:");
        Iterable<String> followPath = service.shortestPathUsersByFollow("U1", "U3");
        for (String step : followPath) {
            System.out.print(step + " -> ");
        }
        System.out.println("END");

        System.out.println("\nShortest path by Film watches between U1 and U2:");
        Iterable<String> filmPath = service.shortestPathUsersByFilm("U1", "U2", false);
        for (String step : filmPath) {
            System.out.print(step + " -> ");
        }
        System.out.println("END");

        System.out.println("\nTesting Subgraphs");
        
        System.out.println("Extracting Action Genre Subgraph:");
        StreamingGraph actionSubgraph = service.extractGenreSubgraph("G_ACTION");
        System.out.println(" - Subgraph vertices: " + actionSubgraph.V());
        System.out.println(" - Subgraph edges: " + actionSubgraph.E());

        System.out.println("Extracting Rating Subgraph (min rating 7.0):");
        StreamingGraph ratingSubgraph = service.extractRatingSubgraph(7.0);
        System.out.println(" - Rating Subgraph vertices: " + ratingSubgraph.V());
        System.out.println(" - Rating Subgraph edges: " + ratingSubgraph.E());


        System.out.println("\nTesting R8.c Connectivity");
        System.out.println("Is main graph connected? " + service.isConnected(graph));
        System.out.println("Is Action Genre Subgraph connected? " + service.isConnected(actionSubgraph));


        System.out.println("\nTesting Recommendations");
        System.out.println("Content recommendation for U1 (based on followers:");
        Iterable<Content> recommendations = service.recommendContent("U1");
        for (Content rec : recommendations) {
            System.out.println(" - Recommended: " + rec.getName() + " (" + rec.getId() + ")");
        }


        System.out.println("\n Testing Temporal Queries");
        
        System.out.println("Viewing stats for C1 (2025-01-01 to 2025-01-15):");
        String c1Stats = service.getContentViewingStats("C1", "2025-01-01", "2025-01-15");
        System.out.println(c1Stats);

        System.out.println("Users with high watch time in Action genre:");
        Iterable<User> activeUsers = service.findUsersWithHighWatchTime("G_ACTION", "2025-01-01", "2025-01-15");
        for (User u : activeUsers) {
            System.out.println("Active User: " + u.getName());
        }

        System.out.println("Followers of U1 who watched C1 (2025-01-01 to 2025-01-15):");
        Iterable<User> commonWatchers = service.findFollowersWhoWatchedSameFilm("U1", "C1", "2025-01-01", "2025-01-15");
        for (User u : commonWatchers) {
            System.out.println("Follower: " + u.getName());
        }

        System.out.println("\nGraph Structure and Algorithm Tests Completed successfully");
    }
}
