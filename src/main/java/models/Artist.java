package models;

import java.time.LocalDate;

/**
 * Represents an artist such as an actor or a director.
 */
public class Artist extends Entity implements Comparable<Artist> {
    private String nationality;
    private String gender;
    private LocalDate birthDate;

    /**
     * Constructs a new Artist.
     *
     * @param id          the unique identifier
     * @param name        the name of the artist
     * @param nationality the nationality of the artist
     * @param gender      the gender of the artist
     * @param birthDate   the birth date of the artist
     */
    public Artist(String id, String name, String nationality, String gender, LocalDate birthDate)
    {
        super(id, name);
        this.nationality = nationality;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    // Getters
    public String getNationality() { return nationality; }
    public String getGender() { return gender; }
    public LocalDate getBirthDate() { return birthDate; }

    @Override
    public int compareTo(Artist other) {
        // Pushes the null objects to the end to prevent Null Pointer Exception
        if (other == null || other.getId() == null)
        {
            return 1;
        }
        if (this.id == null)
        {
            return -1;
        }
        return this.id.compareTo(other.getId());
    }
}