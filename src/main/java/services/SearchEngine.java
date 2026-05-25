package services;

import database.StreamingDatabase;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.SeparateChainingHashST;
import edu.princeton.cs.algs4.SET;
import models.Artist;
import models.Content;
import models.User;

public class SearchEngine {

    private RedBlackBST<String, SET<User>> usersByDate;
    private RedBlackBST<String, SET<Artist>> artistsByBirthDate;

    private SeparateChainingHashST<String, SET<Artist>> artistsByNationality;

    private RedBlackBST<String, SET<Content>> contentsByName;

    public SearchEngine(StreamingDatabase db) {

        this.usersByDate = new RedBlackBST<>();
        this.artistsByBirthDate = new RedBlackBST<>();
        this.artistsByNationality = new SeparateChainingHashST<>();
        this.contentsByName = new RedBlackBST<>();


        buildIndexes(db);
    }
    /**
     * Helper method to initialize all lookup indexes.
     *
     * @param db the active database instance used as the data source
     */
    private void buildIndexes(StreamingDatabase db) {
        buildUserDateIndex(db);
        buildArtistBirthDateIndex(db);
        buildArtistNationalityIndex(db);
        buildContentNameIndex(db);
    }
    /**
     * Maps registration dates to users for  query lookups.
     *
     * @param db active database instance
     */
    private void buildUserDateIndex(StreamingDatabase db) {
        for (String id : db.getAllUserIds()) {
            User user = db.getUser(id);
            if (user != null && user.getRegistrationDate() != null) {
                String dateStr = user.getRegistrationDate().toString();
                if (!usersByDate.contains(dateStr)) {
                    usersByDate.put(dateStr, new SET<User>());
                }
                usersByDate.get(dateStr).add(user);
            }
        }
    }
    /**
     * Indexes artists by their birth dates.
     *
     * @param db active database instance
     */
    private void buildArtistBirthDateIndex(StreamingDatabase db) {
        for (String id : db.getAllArtistIds()) {
            Artist artist = db.getArtist(id);
            if (artist != null && artist.getBirthDate() != null) {
                String birthDateStr = artist.getBirthDate().toString();
                if (!artistsByBirthDate.contains(birthDateStr)) {
                    artistsByBirthDate.put(birthDateStr, new SET<Artist>());
                }
                artistsByBirthDate.get(birthDateStr).add(artist);
            }
        }
    }
    /**
     * Groups artists by nationality using uppercase.
     *
     * @param db active database instance
     */
    private void buildArtistNationalityIndex(StreamingDatabase db) {
        for (String id : db.getAllArtistIds()) {
            Artist artist = db.getArtist(id);
            if (artist != null && artist.getNationality() != null) {
                String nationality = artist.getNationality().toUpperCase();
                if (!artistsByNationality.contains(nationality)) {
                    artistsByNationality.put(nationality, new SET<Artist>());
                }
                artistsByNationality.get(nationality).add(artist);
            }
        }
    }
    /**
     * Indexes content by title for name searches.
     *
     * @param db active database instance
     */
    private void buildContentNameIndex(StreamingDatabase db) {
        for (String id : db.getAllContentIds()) {
            Content content = db.getContent(id);
            if (content != null && content.getName() != null) {
                String normalizedName = content.getName().toUpperCase();
                if (!contentsByName.contains(normalizedName)) {
                    contentsByName.put(normalizedName, new SET<Content>());
                }
                contentsByName.get(normalizedName).add(content);
            }
        }
    }

    /**
     *  Finds users registered within a specific date range
     *
     * @param startDate
     * @param endDate
     * @return
     */

    public Iterable<User> findUsersByRegistrationDateRange(String startDate, String endDate) {
        Queue<User> resultQueue = new Queue<>();
        if (startDate == null || endDate == null) return resultQueue;

        Iterable<String> datesInRange = usersByDate.keys(startDate, endDate);
        for (String date : datesInRange) {
            for (User u : usersByDate.get(date)) resultQueue.enqueue(u);
        }
        return resultQueue;
    }

    /**
     * Finds artists born within a specific date range.
     *
     * @param startDate
     * @param endDate
     * @return
     */

    public Iterable<Artist> findArtistsByBirthDateRange(String startDate, String endDate) {
        Queue<Artist> resultQueue = new Queue<>();
        if (startDate == null || endDate == null) return resultQueue;

        Iterable<String> datesInRange = artistsByBirthDate.keys(startDate, endDate);
        for (String date : datesInRange) {
            for (Artist a : artistsByBirthDate.get(date)) resultQueue.enqueue(a);
        }
        return resultQueue;
    }

    /**
     * Finds artists by their  nationality using Hash Table
     *
     * @param nationality
     * @return
     */

    public Iterable<Artist> findArtistsByNationality(String nationality) {
        Queue<Artist> resultQueue = new Queue<>();
        if (nationality == null || nationality.trim().isEmpty()) return resultQueue;

        String searchStr = nationality.toUpperCase();
        if (artistsByNationality.contains(searchStr)) {
            for (Artist a : artistsByNationality.get(searchStr)) resultQueue.enqueue(a);
        }
        return resultQueue;
    }

    /**
     * Performs a prefix search on content names using Lexicographical BST bounds
     *
     * @param prefix
     * @return
     */

    public Iterable<Content> findContentsByNamePrefix(String prefix) {
        Queue<Content> resultQueue = new Queue<>();
        if (prefix == null || prefix.trim().isEmpty()) return resultQueue;

        String searchStr = prefix.toUpperCase();
        String limitStr = searchStr + '\uFFFF';

        Iterable<String> matchingNames = contentsByName.keys(searchStr, limitStr);
        for (String matchedName : matchingNames) {
            for (Content c : contentsByName.get(matchedName)) resultQueue.enqueue(c);
        }
        return resultQueue;
    }
}