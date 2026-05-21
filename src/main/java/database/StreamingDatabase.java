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

    public StreamingDatabase() {
        this.users = new SeparateChainingHashST<>();
        this.artists = new SeparateChainingHashST<>();
        this.genres = new SeparateChainingHashST<>();
        this.contents = new SeparateChainingHashST<>();
        this.contentsByGenre = new SeparateChainingHashST<>();
    }

    public void insertGenre(Genre genre) {
        if (genre != null && genre.getId() != null) {
            genres.put(genre.getId(), genre);
        }
    }

    public Genre getGenre(String id) {
        return genres.get(id);
    }

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

    public void insertUser(User user) {
        if (user != null && user.getId() != null) {
            users.put(user.getId(), user);
        }
    }

    public User getUser(String id) {
        return users.get(id);
    }

    public void removeUser(String id) {
        if (users.contains(id)) {
            User userToDelete = users.get(id);

            users.delete(id);
        }
    }

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

    public Content getContent(String id) {
        return contents.get(id);
    }

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
        }
    }

    public void insertArtist(Artist artist) {
        if (artist != null && artist.getId() != null) {
            artists.put(artist.getId(), artist);
        }
    }

    public Artist getArtist(String id) {
        return artists.get(id);
    }

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
        }
    }

    /**
     * Links an existing Artist to an existing Content.
     *
     * @param contentId the ID of the content
     * @param artistId  the ID of the artist
     */
    public void addArtistToContent(String contentId, String artistId) {
        Content content = getContent(contentId);
        Artist artist = getArtist(artistId);

        if (content != null && artist != null) {
            content.addArtist(artist);
        }
    }

    /**
     * Returns the contents associated with the specified genre ID,
     * @param genreId
     * @return
     */
    public Iterable<Content> getContentsByGenreId(String genreId) {
        if (genreId != null && contentsByGenre.contains(genreId)) {
            return contentsByGenre.get(genreId);
        }
        return new SET<Content>();
    }

    public Iterable<String> getAllUserIds() { return users.keys(); }
    public Iterable<String> getAllContentIds() { return contents.keys(); }
    public Iterable<String> getAllArtistIds() { return artists.keys(); }
    public Iterable<String> getAllGenreIds() { return genres.keys(); }
}