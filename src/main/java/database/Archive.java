package database;

import edu.princeton.cs.algs4.SeparateChainingHashST;
import edu.princeton.cs.algs4.Queue;
import models.Artist;
import models.Content;
import models.Genre;
import models.User;

import java.io.Serializable;

/**
 * Maintains an in-memory record of all deleted entities for audit and recovery
 * purposes.
 */
public class Archive implements Serializable {

    private SeparateChainingHashST<String, User> deletedUsers;
    private SeparateChainingHashST<String, Artist> deletedArtists;
    private SeparateChainingHashST<String, Genre> deletedGenres;
    private SeparateChainingHashST<String, Content> deletedContents;

    public Archive() {
        this.deletedUsers = new SeparateChainingHashST<>();
        this.deletedArtists = new SeparateChainingHashST<>();
        this.deletedGenres = new SeparateChainingHashST<>();
        this.deletedContents = new SeparateChainingHashST<>();
    }

    public void archiveUser(User u) {
        if (u != null && u.getId() != null) {
            deletedUsers.put(u.getId(), u);
        }
    }

    public void archiveArtist(Artist a) {
        if (a != null && a.getId() != null) {
            deletedArtists.put(a.getId(), a);
        }
    }

    public void archiveGenre(Genre g) {
        if (g != null && g.getId() != null) {
            deletedGenres.put(g.getId(), g);
        }
    }

    public void archiveContent(Content c) {
        if (c != null && c.getId() != null) {
            deletedContents.put(c.getId(), c);
        }
    }

    public Iterable<User> getDeletedUsers() {
        Queue<User> queue = new Queue<>();
        for (String id : deletedUsers.keys()) {
            User u = deletedUsers.get(id);
            if (u != null) {
                queue.enqueue(u);
            }
        }
        return queue;
    }

    public Iterable<Artist> getDeletedArtists() {
        Queue<Artist> queue = new Queue<>();
        for (String id : deletedArtists.keys()) {
            Artist a = deletedArtists.get(id);
            if (a != null) {
                queue.enqueue(a);
            }
        }
        return queue;
    }

    public Iterable<Genre> getDeletedGenres() {
        Queue<Genre> queue = new Queue<>();
        for (String id : deletedGenres.keys()) {
            Genre g = deletedGenres.get(id);
            if (g != null) {
                queue.enqueue(g);
            }
        }
        return queue;
    }

    public Iterable<Content> getDeletedContents() {
        Queue<Content> queue = new Queue<>();
        for (String id : deletedContents.keys()) {
            Content c = deletedContents.get(id);
            if (c != null) {
                queue.enqueue(c);
            }
        }
        return queue;
    }
}
