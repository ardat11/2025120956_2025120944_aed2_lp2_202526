package database;

import edu.princeton.cs.algs4.SeparateChainingHashST;
import edu.princeton.cs.algs4.SET;
import models.Artist;
import models.Content;
import models.Genre;
import models.User;

/**
 * Core database manager for the platform.
 */
public class StreamingDatabase {

    private SeparateChainingHashST<String, User> users;
    private SeparateChainingHashST<String, Artist> artists;
    private SeparateChainingHashST<String, Genre> genres;
    private SeparateChainingHashST<String, Content> contents;
    private SeparateChainingHashST<String, SET<Content>> contentsByGenre;
    private StreamingGraph graph;

    public StreamingDatabase() {
        this.users = new SeparateChainingHashST<>();
        this.artists = new SeparateChainingHashST<>();
        this.genres = new SeparateChainingHashST<>();
        this.contents = new SeparateChainingHashST<>();
        this.contentsByGenre = new SeparateChainingHashST<>();
        this.graph = new StreamingGraph();
    }

    /**
     * Gets the streaming graph instance.
     * 
     * @return the active graph
     */
    public StreamingGraph getGraph() {
        return graph;
    }

    /**
     * Inserts a genre into the database.
     * 
     * @param genre The genre to insert
     */
    public void insertGenre(Genre genre) {
        if (genre != null && genre.getId() != null) {
            genres.put(genre.getId(), genre);
        }
    }

    /**
     * Gets a genre from the database.
     * 
     * @param id The ID of the genre to get
     * @return The genre with the specified ID
     */
    public Genre getGenre(String id) {
        return genres.get(id);
    }

    /**
     * Removes a genre from the database.
     * 
     * @param id The ID of the genre to remove
     */
    public void removeGenre(String id) {
        if (genres.contains(id)) {
            if (contentsByGenre.contains(id)) {
                for (Content c : contentsByGenre.get(id)) {
                    c.setGenre(null);
                }
                contentsByGenre.delete(id);
            }
            genres.delete(id);
        }
    }

    /**
     * Inserts a user into the database.
     * 
     * @param user The user to insert
     */
    public void insertUser(User user) {
        if (user != null && user.getId() != null) {
            users.put(user.getId(), user);
        }
    }

    /**
     * Gets a user from the database.
     * 
     * @param id The ID of the user to get
     * @return The user with the specified ID
     */
    public User getUser(String id) {
        return users.get(id);
    }

    /**
     * Removes a user from the database.
     * 
     * @param id The ID of the user to remove
     */
    public void removeUser(String id) {
        if (users.contains(id)) {
            User userToDelete = users.get(id);

            users.delete(id);
            graph.removeVertex(id);
        }
    }

    /**
     * Inserts a content into the database.
     * 
     * @param content The content to insert
     */
    public void insertContent(Content content) {
        if (content != null && content.getId() != null) {
            contents.put(content.getId(), content);

            if (content.getGenre() != null && content.getGenre().getId() != null) {
                String genreId = content.getGenre().getId();

                if (!contentsByGenre.contains(genreId)) {
                    contentsByGenre.put(genreId, new SET<Content>());
                }

                contentsByGenre.get(genreId).add(content);
            }
        }
    }

    /**
     * Gets a content from the database.
     * 
     * @param id The ID of the content to get
     * @return The content with the specified ID
     */
    public Content getContent(String id) {
        return contents.get(id);
    }

    /**
     * Removes a content from the database.
     * 
     * @param id The ID of the content to remove
     */
    public void removeContent(String id) {
        if (contents.contains(id)) {

            Content content = contents.get(id);

            if (content.getGenre() != null && content.getGenre().getId() != null) {
                String genreId = content.getGenre().getId();
                if (contentsByGenre.contains(genreId)) {
                    contentsByGenre.get(genreId).delete(content);
                }
            }

            contents.delete(id);
            graph.removeVertex(id);
        }
    }

    /**
     * Inserts an artist into the database.
     * 
     * @param artist The artist to insert
     */
    public void insertArtist(Artist artist) {
        if (artist != null && artist.getId() != null) {
            artists.put(artist.getId(), artist);
        }
    }

    /**
     * Gets an artist from the database.
     * 
     * @param id The ID of the artist to get
     * @return The artist with the specified ID
     */
    public Artist getArtist(String id) {
        return artists.get(id);
    }

    /**
     * Removes an artist from the database.
     * 
     * @param id The ID of the artist to remove
     */
    public void removeArtist(String id) {
        if (artists.contains(id)) {
            Artist artistToDelete = artists.get(id);

            for (String contentId : contents.keys()) {
                Content c = contents.get(contentId);

                if (c.getCast() != null && c.getCast().contains(artistToDelete)) {
                    c.getCast().delete(artistToDelete);
                }
            }

            artists.delete(id);
            graph.removeVertex(id);
        }
    }

    /**
     * Links an existing Artist to an existing Content.
     * 
     * @param contentId The ID of the content
     * @param artistId  The ID of the artist
     */
    public void addArtistToContent(String contentId, String artistId) {
        Content content = getContent(contentId);
        Artist artist = getArtist(artistId);

        if (content != null && artist != null) {
            content.addArtist(artist);
        }
    }

    /**
     * Returns the contents associated with the specified genre ID.
     * 
     * @param genreId The ID of the genre to retrieve contents for
     * @return An iterable collection of contents associated with the specified
     *         genre ID.
     */
    public Iterable<Content> getContentsByGenreId(String genreId) {
        if (genreId != null && contentsByGenre.contains(genreId)) {
            return contentsByGenre.get(genreId);
        }
        return new SET<Content>();
    }

    /**
     * Edits a user's information.
     * 
     * @param id               The ID of the user to edit
     * @param name             The new name of the user
     * @param region           The new region of the user
     * @param registrationDate The new registration date of the user
     */
    public void editUser(String id, String name, String region, java.time.LocalDate registrationDate) {
        User user = getUser(id);
        if (user != null) {
            if (name != null)
                user.setName(name);
            if (region != null)
                user.setRegion(region);
            if (registrationDate != null)
                user.setRegistrationDate(registrationDate);
        }
    }

    /**
     * Edits an artist's information.
     * 
     * @param id          The ID of the artist to edit
     * @param name        The new name of the artist
     * @param nationality The new nationality of the artist
     * @param gender      The new gender of the artist
     * @param birthDate   The new birth date of the artist
     */
    public void editArtist(String id, String name, String nationality, String gender, java.time.LocalDate birthDate) {
        Artist artist = getArtist(id);
        if (artist != null) {
            if (name != null)
                artist.setName(name);
            if (nationality != null)
                artist.setNationality(nationality);
            if (gender != null)
                artist.setGender(gender);
            if (birthDate != null)
                artist.setBirthDate(birthDate);
        }
    }

    /**
     * Edits a genre's information.
     * 
     * @param id   The ID of the genre to edit
     * @param name The new name of the genre
     */
    public void editGenre(String id, String name) {
        Genre genre = getGenre(id);
        if (genre != null) {
            if (name != null)
                genre.setName(name);
        }
    }

    /**
     * Edits a content's information.
     * 
     * @param id              The ID of the content to edit
     * @param name            The new name of the content
     * @param type            The new type of the content
     * @param genre           The new genre of the content
     * @param releaseDate     The new release date of the content
     * @param durationMinutes The new duration of the content in minutes
     */
    public void editContent(String id, String name, models.ContentType type, Genre genre,
            java.time.LocalDate releaseDate, int durationMinutes) {
        Content content = getContent(id);
        if (content != null) {
            if (name != null)
                content.setName(name);
            if (type != null)
                content.setType(type);

            Genre oldGenre = content.getGenre();
            if (genre != oldGenre) {
                // Remove from old genre secondary index
                if (oldGenre != null && oldGenre.getId() != null) {
                    String oldGenreId = oldGenre.getId();
                    if (contentsByGenre.contains(oldGenreId)) {
                        contentsByGenre.get(oldGenreId).delete(content);
                    }
                }
                // Set the new genre
                content.setGenre(genre);
                // Add to new genre secondary index
                if (genre != null && genre.getId() != null) {
                    String newGenreId = genre.getId();
                    if (!contentsByGenre.contains(newGenreId)) {
                        contentsByGenre.put(newGenreId, new SET<Content>());
                    }
                    contentsByGenre.get(newGenreId).add(content);
                }
            }

            if (releaseDate != null)
                content.setReleaseDate(releaseDate);
            if (durationMinutes > 0)
                content.setDurationMinutes(durationMinutes);
        }
    }

    /**
     * Lists all users in the database.
     * 
     * @return An iterable collection of all users.
     */
    public Iterable<User> listAllUsers() {
        edu.princeton.cs.algs4.Queue<User> queue = new edu.princeton.cs.algs4.Queue<>();
        for (String id : users.keys()) {
            User u = users.get(id);
            if (u != null) {
                queue.enqueue(u);
            }
        }
        return queue;
    }

    /**
     * Lists all artists in the database.
     * 
     * @return An iterable collection of all artists.
     */
    public Iterable<Artist> listAllArtists() {
        edu.princeton.cs.algs4.Queue<Artist> queue = new edu.princeton.cs.algs4.Queue<>();
        for (String id : artists.keys()) {
            Artist a = artists.get(id);
            if (a != null) {
                queue.enqueue(a);
            }
        }
        return queue;
    }

    /**
     * Lists all genres in the database.
     * 
     * @return An iterable collection of all genres.
     */
    public Iterable<Genre> listAllGenres() {
        edu.princeton.cs.algs4.Queue<Genre> queue = new edu.princeton.cs.algs4.Queue<>();
        for (String id : genres.keys()) {
            Genre g = genres.get(id);
            if (g != null) {
                queue.enqueue(g);
            }
        }
        return queue;
    }

    /**
     * Lists all contents in the database.
     * 
     * @return An iterable collection of all contents.
     */
    public Iterable<Content> listAllContents() {
        edu.princeton.cs.algs4.Queue<Content> queue = new edu.princeton.cs.algs4.Queue<>();
        for (String id : contents.keys()) {
            Content c = contents.get(id);
            if (c != null) {
                queue.enqueue(c);
            }
        }
        return queue;
    }

    /**
     * Gets all user IDs in the database.
     * 
     * @return An iterable collection of all user IDs.
     */
    public Iterable<String> getAllUserIds() {
        return users.keys();
    }

    /**
     * Gets all content IDs in the database.
     * 
     * @return An iterable collection of all content IDs.
     */
    public Iterable<String> getAllContentIds() {
        return contents.keys();
    }

    /**
     * Gets all artist IDs in the database.
     * 
     * @return An iterable collection of all artist IDs.
     */
    public Iterable<String> getAllArtistIds() {
        return artists.keys();
    }

    /**
     * Gets all genre IDs in the database.
     * 
     * @return An iterable collection of all genre IDs.
     */
    public Iterable<String> getAllGenreIds() {
        return genres.keys();
    }
}