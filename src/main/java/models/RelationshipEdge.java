package models;

import edu.princeton.cs.algs4.DirectedEdge;
import java.time.LocalDate;

/**
 * Represents a relationship edge in the streaming graph.
 * Extends DirectedEdge to be compatible with algs4 pathfinding algorithms.
 */
public class RelationshipEdge extends DirectedEdge implements Comparable<RelationshipEdge> {
    private String relationType;
    private String fromId;
    private String toId;
    private LocalDate timestamp;
    private boolean fullyWatched;
    private String details;

    public RelationshipEdge(int v, int w, double weight, String relationType, String fromId, String toId, LocalDate timestamp, boolean fullyWatched, String details) {
        super(v, w, weight);
        this.relationType = relationType;
        this.fromId = fromId;
        this.toId = toId;
        this.timestamp = timestamp;
        this.fullyWatched = fullyWatched;
        this.details = details;
    }

    public String getRelationType() {
        return relationType;
    }

    public String getFromId() {
        return fromId;
    }

    public String getToId() {
        return toId;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public boolean isFullyWatched() {
        return fullyWatched;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public int compareTo(RelationshipEdge other) {
        if (other == null) return 1;

        // comparison: weight
        int cmpWeight = Double.compare(this.weight(), other.weight());
        if (cmpWeight != 0) return cmpWeight;

        // comparison: relationType
        if (this.relationType != null && other.relationType != null) {
            int cmpType = this.relationType.compareTo(other.relationType);
            if (cmpType != 0) return cmpType;
        }

        // comparison: fromId
        if (this.fromId != null && other.fromId != null) {
            int cmpFrom = this.fromId.compareTo(other.fromId);
            if (cmpFrom != 0) return cmpFrom;
        }

        // comparison: toId
        if (this.toId != null && other.toId != null) {
            return this.toId.compareTo(other.toId);
        }

        return 0;
    }
}
