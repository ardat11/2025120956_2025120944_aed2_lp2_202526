package models;

import java.time.LocalDate;
import edu.princeton.cs.algs4.SET;

/**
 * Represents a multimedia content (movie, series, etc.) in the platform.
 */
public class Content extends Entity implements Comparable<Content> {

    private ContentType type;
    private Genre genre;
    private LocalDate releaseDate;
    private int durationMinutes;

    private SET<Artist> cast;

    /**
     * Constructs a new Content entity.
     * @param id   the unique identifier for the entity
     * @param name the name of the entity
     * @param type the type of the content (e.g., Movie, Series)
     * @param genre the genre of the content
     * @param releaseDate the release date of the content
     * @param durationMinutes the duration of the content in minutes
     */
    public Content(String id, String name, ContentType type, Genre genre, LocalDate releaseDate, int durationMinutes) {
        super(id, name);
        this.type = type;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.durationMinutes = durationMinutes;
        this.cast = new SET<>();
    }


    // Getters and Setters
    public ContentType getType() { return type; }

    public void setType(ContentType type) { this.type = type; }

    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }

    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }



    public void addArtist(Artist artist)
    {
        if (artist != null)
        {
            this.cast.add(artist);
        }
    }

    public void removeArtist(Artist artist)
    {
        if (artist != null)
        {
            this.cast.delete(artist);
        }
    }

    public SET<Artist> getCast()
    {
        return this.cast;
    }

    @Override
    public int compareTo(Content other) {
        if (other == null || other.getId() == null)
        {
            return 1;
            // Pushes the null objects to the end to prevent Null Pointer Exception

        }
        if (this.id == null)
        {
            return -1;
        }
        return this.id.compareTo(other.getId());
    }

}