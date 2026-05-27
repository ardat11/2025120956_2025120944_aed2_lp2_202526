package models;

/**
 * Represents a content genre (e.g., Action, Comedy) in the system.
 */
public class Genre extends Entity implements Comparable<Genre> {

    /**
     * Constructs a new Genre entity.
     *
     * @param id   the unique identifier for the genre (e.g., "G1")
     * @param name the name of the genre (e.g., "Action")
     */
    public Genre(String id, String name) {
        super(id, name);
    }

    @Override
    public int compareTo(Genre other)
    {
        if (other == null || other.getId() == null) {
            return 1;
            // Pushes the null objects to the end to prevent Null Pointer Exception

        }
        if (this.id == null) {
            return -1;
        }
        return this.id.compareTo(other.getId());
    }

    @Override
    public String toString() {
        return this.getName() != null ? this.getName() : "";
    }
}