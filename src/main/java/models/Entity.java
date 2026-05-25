package models;

import java.io.Serializable;

/**
 * Base abstract class for all domain entities
 */
public abstract class Entity implements Serializable {

    protected String id;
    protected String name;

    /**
     * Constructs a new Entity.
     *
     * @param id   the unique identifier for the entity
     * @param name the name of the entity
     */
    public Entity(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters-Setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}