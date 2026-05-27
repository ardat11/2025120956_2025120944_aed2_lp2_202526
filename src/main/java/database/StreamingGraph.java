package database;

import edu.princeton.cs.algs4.SeparateChainingHashST;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import models.RelationshipEdge;
import java.time.LocalDate;

/**
 * Weighted directed graph that dynamically maps String entity IDs to integer vertices.
 * Provides APIs to manage vertices, relationships and exports to standard algs4 graph.
 */
public class StreamingGraph {
    private SeparateChainingHashST<String, Integer> st;
    private SeparateChainingHashST<Integer, String> keys;
    private SeparateChainingHashST<String, SET<RelationshipEdge>> adj;
    private int vertexCount;

    public StreamingGraph() {
        this.st = new SeparateChainingHashST<>();
        this.keys = new SeparateChainingHashST<>();
        this.adj = new SeparateChainingHashST<>();
        this.vertexCount = 0;
    }

    /**
     * Adds a new vertex mapped to the entityId.
     *
     * @param entityId unique identifier of the entity
     */
    public void addVertex(String entityId) {
        if (entityId == null || entityId.trim().isEmpty()) return;
        if (!adj.contains(entityId)) {
            st.put(entityId, vertexCount);
            keys.put(vertexCount, entityId);
            vertexCount++;
            adj.put(entityId, new SET<RelationshipEdge>());
        }
    }

    /**
     * Removes a vertex and all its connected incoming and outgoing edges.
     *
     * @param entityId unique identifier of the entity to remove
     */
    public void removeVertex(String entityId) {
        if (entityId == null || !adj.contains(entityId)) return;

        // Remove outgoing edges and mappings
        adj.delete(entityId);
        if (st.contains(entityId)) {
            int index = st.get(entityId);
            st.delete(entityId);
            keys.delete(index);
        }

        // Remove incoming edges pointing to this vertex from other vertices
        for (String otherId : adj.keys()) {
            SET<RelationshipEdge> edges = adj.get(otherId);
            Queue<RelationshipEdge> toRemove = new Queue<>();
            for (RelationshipEdge edge : edges) {
                if (edge.getToId().equals(entityId)) {
                    toRemove.enqueue(edge);
                }
            }
            for (RelationshipEdge edge : toRemove) {
                edges.delete(edge);
            }
        }
    }

    /**
     * Adds or updates a directed weighted edge between two entities.
     *
     * @param fromId source entity ID
     * @param toId target entity ID
     * @param weight edge weight (e.g. watch time or rating)
     * @param relationType type of relationship (e.g. WATCHES, RATES)
     * @param timestamp date of the interaction
     * @param fullyWatched whether the content was fully watched
     * @param details additional relationship information
     */
    public void addEdge(String fromId, String toId, double weight, String relationType, LocalDate timestamp, boolean fullyWatched, String details) {
        if (fromId == null || toId == null || relationType == null) return;

        // Ensure both vertices exist in the graph
        addVertex(fromId);
        addVertex(toId);

        int v = st.get(fromId);
        int w = st.get(toId);

        // If the same relationship already exists, remove it first to update
        RelationshipEdge existingEdge = getEdge(fromId, toId, relationType);
        if (existingEdge != null) {
            adj.get(fromId).delete(existingEdge);
        }

        RelationshipEdge newEdge = new RelationshipEdge(v, w, weight, relationType, fromId, toId, timestamp, fullyWatched, details);
        adj.get(fromId).add(newEdge);
    }

    /**
     * Removes a specific relationship between two vertices.
     *
     * @param fromId source entity ID
     * @param toId target entity ID
     * @param relationType type of relationship to remove
     */
    public void removeEdge(String fromId, String toId, String relationType) {
        if (fromId == null || toId == null || relationType == null) return;
        if (!adj.contains(fromId)) return;

        RelationshipEdge edgeToRemove = getEdge(fromId, toId, relationType);
        if (edgeToRemove != null) {
            adj.get(fromId).delete(edgeToRemove);
        }
    }

    /**
     * Finds a specific relationship edge.
     *
     * @param fromId source entity ID
     * @param toId target entity ID
     * @param relationType type of relationship
     * @return the edge if found, null otherwise
     */
    public RelationshipEdge getEdge(String fromId, String toId, String relationType) {
        if (fromId == null || toId == null || relationType == null) return null;
        if (!adj.contains(fromId)) return null;

        for (RelationshipEdge edge : adj.get(fromId)) {
            if (edge.getToId().equals(toId) && edge.getRelationType().equals(relationType)) {
                return edge;
            }
        }
        return null;
    }

    /**
     * Returns the outgoing relationships of a vertex.
     *
     * @param entityId unique identifier of the entity
     * @return iterable collection of relationship edges
     */
    public Iterable<RelationshipEdge> adj(String entityId) {
        if (entityId == null || !adj.contains(entityId)) return new SET<>();
        return adj.get(entityId);
    }

    /**
     * Returns all active vertex IDs in the graph.
     *
     * @return iterable of entity IDs
     */
    public Iterable<String> vertices() {
        return adj.keys();
    }

    /**
     * Returns the number of active vertices in the graph.
     *
     * @return number of vertices
     */
    public int V() {
        return adj.size();
    }

    /**
     * Returns the total number of relationships/edges in the graph.
     *
     * @return number of edges
     */
    public int E() {
        int count = 0;
        for (String id : adj.keys()) {
            count += adj.get(id).size();
        }
        return count;
    }

    /**
     * Exports the dynamic graph to a standard algs4 EdgeWeightedDigraph.
     * Maps active string IDs to contiguous integer indices dynamically.
     *
     * @return EdgeWeightedDigraph instance
     */
    public EdgeWeightedDigraph toEdgeWeightedDigraph() {
        int activeV = V();
        EdgeWeightedDigraph digraph = new EdgeWeightedDigraph(activeV);

        // Map active String IDs to contiguous integers 0..activeV-1
        SeparateChainingHashST<String, Integer> tempMap = new SeparateChainingHashST<>();
        int tempIndex = 0;
        for (String id : adj.keys()) {
            tempMap.put(id, tempIndex++);
        }

        // Add all active edges to the digraph
        for (String fromId : adj.keys()) {
            int v = tempMap.get(fromId);
            for (RelationshipEdge edge : adj.get(fromId)) {
                if (tempMap.contains(edge.getToId())) {
                    int w = tempMap.get(edge.getToId());
                    digraph.addEdge(new RelationshipEdge(v, w, edge.weight(), edge.getRelationType(), edge.getFromId(), edge.getToId(), edge.getTimestamp(), edge.isFullyWatched(), edge.getDetails()));
                }
            }
        }
        return digraph;
    }
}
