package testing;

import database.StreamingDatabase;
import services.SearchEngine;
import models.*;
import java.time.LocalDate;

public class DatabaseTest {

    public static void runDatabaseTests() {
        System.out.println("--- Starting Database CRUD and Listing Tests ---");
        StreamingDatabase db = new StreamingDatabase();

        // Testing inserts
        System.out.println("Inserting test data...");
        Genre action = new Genre("G_ACTION", "Action");
        Genre drama = new Genre("G_DRAMA", "Drama");
        db.insertGenre(action);
        db.insertGenre(drama);

        User u1 = new User("U1", "Alice", "Europe", LocalDate.of(2025, 1, 1));
        User u2 = new User("U2", "Bob", "Asia", LocalDate.of(2025, 2, 1));
        db.insertUser(u1);
        db.insertUser(u2);

        Artist a1 = new Artist("A1", "Leonardo", "Italian", "Male", LocalDate.of(1974, 11, 11));
        Artist a2 = new Artist("A2", "Kate", "British", "Female", LocalDate.of(1975, 10, 5));
        db.insertArtist(a1);
        db.insertArtist(a2);

        Content c1 = new Content("C1", "Titanic", ContentType.MOVIE, drama, LocalDate.of(1997, 12, 19), 194);
        Content c2 = new Content("C2", "Inception", ContentType.MOVIE, action, LocalDate.of(2010, 7, 16), 148);
        db.insertContent(c1);
        db.insertContent(c2);

        db.addArtistToContent("C1", "A1");
        db.addArtistToContent("C1", "A2");

        // Testing edits
        System.out.println("Testing entity edits...");
        db.editUser("U1", "Alice Smith", "Europe", LocalDate.of(2025, 1, 5));
        db.editArtist("A1", "Leonardo DiCaprio", "Italian", "Male", LocalDate.of(1974, 11, 11));
        db.editGenre("G_ACTION", "Action Movies");
        db.editContent("C2", "Inception Edited", ContentType.MOVIE, action, LocalDate.of(2010, 7, 16), 150);

        System.out.println("Edited User name: " + db.getUser("U1").getName());
        System.out.println("Edited Artist name: " + db.getArtist("A1").getName());
        System.out.println("Edited Genre name: " + db.getGenre("G_ACTION").getName());
        System.out.println("Edited Content name: " + db.getContent("C2").getName());

        // Testing listings
        System.out.println("Listing all users:");
        for (User u : db.listAllUsers()) {
            System.out.println(" - " + u.getId() + ": " + u.getName());
        }

        System.out.println("Listing all artists:");
        for (Artist a : db.listAllArtists()) {
            System.out.println(" - " + a.getId() + ": " + a.getName());
        }

        System.out.println("Listing all genres:");
        for (Genre g : db.listAllGenres()) {
            System.out.println(" - " + g.getId() + ": " + g.getName());
        }

        System.out.println("Listing all contents:");
        for (Content c : db.listAllContents()) {
            System.out.println(" - " + c.getId() + ": " + c.getName());
        }

        System.out.println("--- Database CRUD and Listing Tests Completed ---\n");
    }

    public static void runSearchTests() {
        System.out.println("--- Starting Search Query and Filter Tests ---");
        StreamingDatabase db = new StreamingDatabase();
        
        Genre sciFi = new Genre("G_SCIFI", "Sci-Fi");
        db.insertGenre(sciFi);

        User u1 = new User("U1", "Alice Peterson", "US", LocalDate.of(2025, 1, 10));
        User u2 = new User("U2", "Bob Johnson", "UK", LocalDate.of(2025, 3, 15));
        db.insertUser(u1);
        db.insertUser(u2);

        Artist a1 = new Artist("A1", "Christopher Nolan", "British", "Male", LocalDate.of(1970, 7, 30));
        Artist a2 = new Artist("A2", "Emma Thomas", "British", "Female", LocalDate.of(1971, 12, 9));
        db.insertArtist(a1);
        db.insertArtist(a2);

        Content c1 = new Content("C1", "Interstellar", ContentType.MOVIE, sciFi, LocalDate.of(2014, 11, 7), 169);
        db.insertContent(c1);

        SearchEngine engine = new SearchEngine(db);

        // Verification of queries (a-g)
        System.out.println("Searching users by region 'US':");
        for (User u : engine.findUsersByRegion("US")) {
            System.out.println(" - Found: " + u.getName());
        }

        System.out.println("Searching users by region 'US' and date range:");
        for (User u : engine.findUsersByRegionAndDateRange("US", "2025-01-01", "2025-01-20")) {
            System.out.println(" - Found: " + u.getName());
        }

        System.out.println("Searching users by name substring 'Peterson':");
        for (User u : engine.findUsersByNameSubstring("Peterson")) {
            System.out.println(" - Found: " + u.getName());
        }

        System.out.println("Searching users by name, region, and date:");
        for (User u : engine.findUsersByNameAndRegionAndDate("Alice", "US", "2025-01-01", "2025-02-01")) {
            System.out.println(" - Found: " + u.getName());
        }

        System.out.println("Searching artists by gender 'Female':");
        for (Artist a : engine.findArtistsByGender("Female")) {
            System.out.println(" - Found: " + a.getName());
        }

        System.out.println("Searching artists by nationality, gender, and birth date range:");
        for (Artist a : engine.findArtistsByNationalityAndGenderAndBirthDateRange("British", "Female", "1971-01-01", "1972-01-01")) {
            System.out.println(" - Found: " + a.getName());
        }

        System.out.println("Searching contents by type 'MOVIE':");
        for (Content c : engine.findContentsByType(ContentType.MOVIE)) {
            System.out.println(" - Found: " + c.getName());
        }

        System.out.println("Searching contents by title substring 'Inter':");
        for (Content c : engine.findContentsByTitleSubstring("Inter")) {
            System.out.println(" - Found: " + c.getName());
        }

        System.out.println("Searching contents by genre and duration range:");
        for (Content c : engine.findContentsByGenreAndDurationRange("G_SCIFI", 150, 180)) {
            System.out.println(" - Found: " + c.getName());
        }

        System.out.println("Searching contents by year range 2010 to 2015:");
        for (Content c : engine.findContentsByYearRange(2010, 2015)) {
            System.out.println(" - Found: " + c.getName());
        }

        System.out.println("--- Search Query and Filter Tests Completed ---\n");
    }
}
