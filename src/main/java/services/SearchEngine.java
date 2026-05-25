package services;

import database.StreamingDatabase;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.SeparateChainingHashST;
import edu.princeton.cs.algs4.SET;
import models.Artist;
import models.Content;
import models.ContentType;
import models.User;

public class SearchEngine {

    private RedBlackBST<String, SET<User>> usersByDate;
    private RedBlackBST<String, SET<Artist>> artistsByBirthDate;
    private SeparateChainingHashST<String, SET<Artist>> artistsByNationality;
    private RedBlackBST<String, SET<Content>> contentsByName;
    private RedBlackBST<String, SET<Content>> contentsByReleaseDate;

    public SearchEngine(StreamingDatabase db) {
        this.usersByDate = new RedBlackBST<>();
        this.artistsByBirthDate = new RedBlackBST<>();
        this.artistsByNationality = new SeparateChainingHashST<>();
        this.contentsByName = new RedBlackBST<>();
        this.contentsByReleaseDate = new RedBlackBST<>();

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
        buildContentReleaseDateIndex(db);
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
     * Indexes content by release date for range searches.
     *
     * @param db active database instance
     */
    private void buildContentReleaseDateIndex(StreamingDatabase db) {
        for (String id : db.getAllContentIds()) {
            Content content = db.getContent(id);
            if (content != null && content.getReleaseDate() != null) {
                String dateStr = content.getReleaseDate().toString();
                if (!contentsByReleaseDate.contains(dateStr)) {
                    contentsByReleaseDate.put(dateStr, new SET<Content>());
                }
                contentsByReleaseDate.get(dateStr).add(content);
            }
        }
    }

    /**
     * Finds users registered within a specific date range.
     *
     * @param  startDate beginning of registration period (YYYY-MM-DD)
     * @param  endDate end of registration period (YYYY-MM-DD)
     * @return users registered in this range
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
     * @param  startDate beginning of birth period (YYYY-MM-DD)
     * @param  endDate end of birth period (YYYY-MM-DD)
     * @return artists born in this range
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
     * Finds artists by their nationality using Hash Table.
     *
     * @param  nationality country name to filter by
     * @return artists from the specified nation
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
     * Performs a prefix search on content names using Lexicographical BST bounds.
     *
     * @param  prefix starting characters of content title
     * @return matching content list
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

    /**
     * Finds content released within a specific date range.
     *
     * @param  startDate beginning of release range (YYYY-MM-DD)
     * @param  endDate end of release range (YYYY-MM-DD)
     * @return matching content list
     */
    public Iterable<Content> findContentsByReleaseDateRange(String startDate, String endDate) {
        Queue<Content> resultQueue = new Queue<>();
        if (startDate == null || endDate == null) return resultQueue;

        Iterable<String> datesInRange = contentsByReleaseDate.keys(startDate, endDate);
        for (String date : datesInRange) {
            for (Content c : contentsByReleaseDate.get(date)) resultQueue.enqueue(c);
        }
        return resultQueue;
    }

    /**
     * Finds all users from a given region.
     *
     * @param  region geographical region code or name
     * @return list of users in this region
     */
    public Iterable<User> findUsersByRegion(String region) {
        Queue<User> resultQueue = new Queue<>();
        if (region == null || region.trim().isEmpty()) return resultQueue;

        String searchStr = region.trim().toUpperCase();
        for (String date : usersByDate.keys()) {
            for (User u : usersByDate.get(date)) {
                if (u.getRegion() != null && u.getRegion().toUpperCase().equals(searchStr)) {
                    resultQueue.enqueue(u);
                }
            }
        }
        return resultQueue;
    }

    /**
     * Finds all users from a given region and registration date range.
     *
     * @param  region geographical region code or name
     * @param  startDate starting registration date limit
     * @param  endDate ending registration date limit
     * @return matching users list
     */
    public Iterable<User> findUsersByRegionAndDateRange(String region, String startDate, String endDate) {
        Queue<User> resultQueue = new Queue<>();
        if (region == null || region.trim().isEmpty() || startDate == null || endDate == null) return resultQueue;

        String searchStr = region.trim().toUpperCase();
        Iterable<User> usersInRange = findUsersByRegistrationDateRange(startDate, endDate);
        for (User u : usersInRange) {
            if (u.getRegion() != null && u.getRegion().toUpperCase().equals(searchStr)) {
                resultQueue.enqueue(u);
            }
        }
        return resultQueue;
    }

    /**
     * Finds users by a name substring.
     *
     * @param  substring name fragment to search for
     * @return matching users list
     */
    public Iterable<User> findUsersByNameSubstring(String substring) {
        Queue<User> resultQueue = new Queue<>();
        if (substring == null || substring.trim().isEmpty()) return resultQueue;

        String searchStr = substring.trim().toUpperCase();
        for (String date : usersByDate.keys()) {
            for (User u : usersByDate.get(date)) {
                if (u.getName() != null && u.getName().toUpperCase().contains(searchStr)) {
                    resultQueue.enqueue(u);
                }
            }
        }
        return resultQueue;
    }

    /**
     * Finds users by name substring, region, and registration date range.
     *
     * @param  substring name fragment to filter by
     * @param  region geographical region code or name
     * @param  startDate starting registration date
     * @param  endDate ending registration date
     * @return matching users list
     */
    public Iterable<User> findUsersByNameAndRegionAndDate(String substring, String region, String startDate, String endDate) {
        Queue<User> resultQueue = new Queue<>();
        boolean hasSubstring = (substring != null && !substring.trim().isEmpty());
        boolean hasRegion = (region != null && !region.trim().isEmpty());
        boolean hasDates = (startDate != null && endDate != null);

        if (!hasSubstring && !hasRegion && !hasDates) return resultQueue;

        Iterable<User> baseSet;
        if (hasDates) {
            baseSet = findUsersByRegistrationDateRange(startDate, endDate);
        } else {
            Queue<User> allUsers = new Queue<>();
            for (String date : usersByDate.keys()) {
                for (User u : usersByDate.get(date)) allUsers.enqueue(u);
            }
            baseSet = allUsers;
        }

        String subUpper = hasSubstring ? substring.trim().toUpperCase() : "";
        String regUpper = hasRegion ? region.trim().toUpperCase() : "";

        for (User u : baseSet) {
            if (hasSubstring && (u.getName() == null || !u.getName().toUpperCase().contains(subUpper))) {
                continue;
            }
            if (hasRegion && (u.getRegion() == null || !u.getRegion().toUpperCase().equals(regUpper))) {
                continue;
            }
            resultQueue.enqueue(u);
        }
        return resultQueue;
    }

    /**
     * Finds artists by gender.
     *
     * @param  gender gender string (e.g. Male, Female)
     * @return matching artists list
     */
    public Iterable<Artist> findArtistsByGender(String gender) {
        Queue<Artist> resultQueue = new Queue<>();
        if (gender == null || gender.trim().isEmpty()) return resultQueue;

        String searchStr = gender.trim().toUpperCase();
        for (String date : artistsByBirthDate.keys()) {
            for (Artist a : artistsByBirthDate.get(date)) {
                if (a.getGender() != null && a.getGender().toUpperCase().equals(searchStr)) {
                    resultQueue.enqueue(a);
                }
            }
        }
        return resultQueue;
    }

    /**
     * Finds artists by nationality, gender, and birth date range.
     *
     * @param  nationality artist's country name
     * @param  gender gender keyword
     * @param  startDate starting birth date limit
     * @param  endDate ending birth date limit
     * @return matching artists list
     */
    public Iterable<Artist> findArtistsByNationalityAndGenderAndBirthDateRange(String nationality, String gender, String startDate, String endDate) {
        Queue<Artist> resultQueue = new Queue<>();
        if (nationality == null || nationality.trim().isEmpty() ||
            gender == null || gender.trim().isEmpty() ||
            startDate == null || endDate == null) return resultQueue;

        String natUpper = nationality.trim().toUpperCase();
        String genUpper = gender.trim().toUpperCase();

        Iterable<Artist> artistsInRange = findArtistsByBirthDateRange(startDate, endDate);
        for (Artist a : artistsInRange) {
            if (a.getNationality() != null && a.getNationality().toUpperCase().equals(natUpper) &&
                a.getGender() != null && a.getGender().toUpperCase().equals(genUpper)) {
                resultQueue.enqueue(a);
            }
        }
        return resultQueue;
    }

    /**
     * Finds artists by name substring.
     *
     * @param   substring name fragment to search for
     * @return matching artists list
     */
    public Iterable<Artist> findArtistsByNameSubstring(String substring) {
        Queue<Artist> resultQueue = new Queue<>();
        if (substring == null || substring.trim().isEmpty()) return resultQueue;

        String searchStr = substring.trim().toUpperCase();
        for (String date : artistsByBirthDate.keys()) {
            for (Artist a : artistsByBirthDate.get(date)) {
                if (a.getName() != null && a.getName().toUpperCase().contains(searchStr)) {
                    resultQueue.enqueue(a);
                }
            }
        }
        return resultQueue;
    }

    /**
     * Finds artists by name substring, nationality, gender, and birth date range.
     *
     * @param  substring name fragment to filter by
     * @param  nationality artist's country name
     * @param  gender gender keyword
     * @param  startDate starting birth date
     * @param  endDate ending birth date
     * @return matching artists list
     */
    public Iterable<Artist> findArtistsByNameAndNationalityAndGenderAndBirthDate(String substring, String nationality, String gender, String startDate, String endDate) {
        Queue<Artist> resultQueue = new Queue<>();
        boolean hasSubstring = (substring != null && !substring.trim().isEmpty());
        boolean hasNat = (nationality != null && !nationality.trim().isEmpty());
        boolean hasGen = (gender != null && !gender.trim().isEmpty());
        boolean hasDates = (startDate != null && endDate != null);

        if (!hasSubstring && !hasNat && !hasGen && !hasDates) return resultQueue;

        Iterable<Artist> baseSet;
        if (hasDates) {
            baseSet = findArtistsByBirthDateRange(startDate, endDate);
        } else {
            Queue<Artist> allArtists = new Queue<>();
            for (String date : artistsByBirthDate.keys()) {
                for (Artist a : artistsByBirthDate.get(date)) allArtists.enqueue(a);
            }
            baseSet = allArtists;
        }

        String subUpper = hasSubstring ? substring.trim().toUpperCase() : "";
        String natUpper = hasNat ? nationality.trim().toUpperCase() : "";
        String genUpper = hasGen ? gender.trim().toUpperCase() : "";

        for (Artist a : baseSet) {
            if (hasSubstring && (a.getName() == null || !a.getName().toUpperCase().contains(subUpper))) {
                continue;
            }
            if (hasNat && (a.getNationality() == null || !a.getNationality().toUpperCase().equals(natUpper))) {
                continue;
            }
            if (hasGen && (a.getGender() == null || !a.getGender().toUpperCase().equals(genUpper))) {
                continue;
            }
            resultQueue.enqueue(a);
        }
        return resultQueue;
    }

    /**
     * Finds contents by type.
     *
     * @param  type content type constant (e.g. MOVIE, SERIES)
     * @return matching content list
     */
    public Iterable<Content> findContentsByType(ContentType type) {
        Queue<Content> resultQueue = new Queue<>();
        if (type == null) return resultQueue;

        for (String name : contentsByName.keys()) {
            for (Content c : contentsByName.get(name)) {
                if (c.getType() == type) {
                    resultQueue.enqueue(c);
                }
            }
        }
        return resultQueue;
    }

    /**
     * Finds contents by type, genre, and release date range.
     *
     * @param  type content type constant
     * @param  genreId unique identifier of target genre
     * @param  startDate starting release date limit
     * @param  endDate ending release date limit
     * @return matching content list
     */
    public Iterable<Content> findContentsByTypeAndGenreAndDateRange(ContentType type, String genreId, String startDate, String endDate) {
        Queue<Content> resultQueue = new Queue<>();
        boolean hasType = (type != null);
        boolean hasGenre = (genreId != null && !genreId.trim().isEmpty());
        boolean hasDates = (startDate != null && endDate != null);

        if (!hasType && !hasGenre && !hasDates) return resultQueue;

        Iterable<Content> baseSet;
        if (hasDates) {
            baseSet = findContentsByReleaseDateRange(startDate, endDate);
        } else {
            Queue<Content> allContents = new Queue<>();
            for (String name : contentsByName.keys()) {
                for (Content c : contentsByName.get(name)) allContents.enqueue(c);
            }
            baseSet = allContents;
        }

        String genUpper = hasGenre ? genreId.trim().toUpperCase() : "";

        for (Content c : baseSet) {
            if (hasType && c.getType() != type) {
                continue;
            }
            if (hasGenre && (c.getGenre() == null || c.getGenre().getId() == null ||
                !c.getGenre().getId().toUpperCase().equals(genUpper))) {
                continue;
            }
            resultQueue.enqueue(c);
        }
        return resultQueue;
    }

    /**
     * Finds contents by title substring.
     *
     * @param  substring name fragment of content title
     * @return matching content list
     */
    public Iterable<Content> findContentsByTitleSubstring(String substring) {
        Queue<Content> resultQueue = new Queue<>();
        if (substring == null || substring.trim().isEmpty()) return resultQueue;

        String searchStr = substring.trim().toUpperCase();
        for (String name : contentsByName.keys()) {
            for (Content c : contentsByName.get(name)) {
                if (c.getName() != null && c.getName().toUpperCase().contains(searchStr)) {
                    resultQueue.enqueue(c);
                }
            }
        }
        return resultQueue;
    }

    /**
     * Finds contents by title substring, type, genre, and release date range.
     *
     * @param  substring name fragment of content title
     * @param  type content type constant
     * @param  genreId unique identifier of target genre
     * @param  startDate starting release date range
     * @param  endDate ending release date range
     * @return matching content list
     */
    public Iterable<Content> findContentsByTitleAndTypeAndGenreAndDate(String substring, ContentType type, String genreId, String startDate, String endDate) {
        Queue<Content> resultQueue = new Queue<>();
        boolean hasSubstring = (substring != null && !substring.trim().isEmpty());
        boolean hasType = (type != null);
        boolean hasGenre = (genreId != null && !genreId.trim().isEmpty());
        boolean hasDates = (startDate != null && endDate != null);

        if (!hasSubstring && !hasType && !hasGenre && !hasDates) return resultQueue;

        Iterable<Content> baseSet;
        if (hasDates) {
            baseSet = findContentsByReleaseDateRange(startDate, endDate);
        } else {
            Queue<Content> allContents = new Queue<>();
            for (String name : contentsByName.keys()) {
                for (Content c : contentsByName.get(name)) allContents.enqueue(c);
            }
            baseSet = allContents;
        }

        String subUpper = hasSubstring ? substring.trim().toUpperCase() : "";
        String genUpper = hasGenre ? genreId.trim().toUpperCase() : "";

        for (Content c : baseSet) {
            if (hasSubstring && (c.getName() == null || !c.getName().toUpperCase().contains(subUpper))) {
                continue;
            }
            if (hasType && c.getType() != type) {
                continue;
            }
            if (hasGenre && (c.getGenre() == null || c.getGenre().getId() == null ||
                !c.getGenre().getId().toUpperCase().equals(genUpper))) {
                continue;
            }
            resultQueue.enqueue(c);
        }
        return resultQueue;
    }

    /**
     * Finds contents of a specific genre with duration within a given range.
     *
     * @param  genreId unique identifier of target genre
     * @param  minDuration minimum runtime threshold in minutes
     * @param  maxDuration maximum runtime threshold in minutes
     * @return matching content list
     */
    public Iterable<Content> findContentsByGenreAndDurationRange(String genreId, int minDuration, int maxDuration) {
        Queue<Content> resultQueue = new Queue<>();
        if (genreId == null || genreId.trim().isEmpty()) return resultQueue;

        String genUpper = genreId.trim().toUpperCase();
        for (String name : contentsByName.keys()) {
            for (Content c : contentsByName.get(name)) {
                if (c.getGenre() != null && c.getGenre().getId() != null &&
                    c.getGenre().getId().toUpperCase().equals(genUpper)) {
                    if (c.getDurationMinutes() >= minDuration && c.getDurationMinutes() <= maxDuration) {
                        resultQueue.enqueue(c);
                    }
                }
            }
        }
        return resultQueue;
    }

    /**
     * Finds contents released within a year range.
     *
     * @param  startYear starting year value (inclusive)
     * @param  endYear ending year value (inclusive)
     * @return matching content list
     */
    public Iterable<Content> findContentsByYearRange(int startYear, int endYear) {
        String startDate = startYear + "-01-01";
        String endDate = endYear + "-12-31";
        return findContentsByReleaseDateRange(startDate, endDate);
    }
}