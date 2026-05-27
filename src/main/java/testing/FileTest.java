package testing;

import database.Archive;
import database.FileManager;
import database.StreamingDatabase;
import services.SearchEngine;
import models.*;

import java.io.File;
import java.time.LocalDate;

public class FileTest {

    public static void runFileTests() {
        System.out.println("--- Starting Consistency, Archiving and Serialization Tests ---");

        StreamingDatabase db = new StreamingDatabase();
        Archive archive = new Archive();

        Genre romance = new Genre("G_ROMANCE", "Romance");
        db.insertGenre(romance);

        User u1 = new User("U1", "Jack", "North America", LocalDate.of(2025, 2, 14));
        db.insertUser(u1);

        Artist a1 = new Artist("A1", "Keira Knightley", "British", "Female", LocalDate.of(1985, 3, 26));
        db.insertArtist(a1);

        Content c1 = new Content("C1", "Pride and Prejudice", ContentType.MOVIE, romance, LocalDate.of(2005, 9, 16),
                129);
        db.insertContent(c1);
        db.addArtistToContent("C1", "A1");

        SearchEngine engine = new SearchEngine(db);

        // Consistency & Archiving on Deletion testing
        System.out.println("Testing user removal and index synchronization...");
        User jack = db.getUser("U1");
        if (jack != null) {
            archive.archiveUser(jack);
            engine.removeUser(jack);
            db.removeUser("U1");
        }
        System.out.println("Is Jack in DB? " + (db.getUser("U1") != null ? "Yes" : "No"));
        boolean foundInRegion = false;
        for (User u : engine.findUsersByRegion("North America")) {
            if (u.getId().equals("U1"))
                foundInRegion = true;
        }
        System.out.println("Is Jack found in search indexes? " + (foundInRegion ? "Yes" : "No"));
        boolean foundInArchive = false;
        for (User u : archive.getDeletedUsers()) {
            if (u.getId().equals("U1"))
                foundInArchive = true;
        }
        System.out.println("Is Jack in Archive? " + (foundInArchive ? "Yes" : "No"));

        // Cascaded deletion for Artist cast testing
        System.out.println("Testing artist removal and cast list cascade...");
        Artist keira = db.getArtist("A1");
        if (keira != null) {
            archive.archiveArtist(keira);
            engine.removeArtist(keira);
            db.removeArtist("A1");
        }
        Content pride = db.getContent("C1");
        boolean artistInCast = false;
        if (pride != null && pride.getCast() != null) {
            for (Artist a : pride.getCast()) {
                if (a.getId().equals("A1"))
                    artistInCast = true;
            }
        }
        System.out.println("Is Keira still in Pride and Prejudice cast? " + (artistInCast ? "Yes" : "No"));

        // Text export testing
        System.out.println("Testing text exports...");
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
        FileManager.saveUsers(db, "data/users_export.txt");
        FileManager.saveArtists(db, "data/artists_export.txt");
        FileManager.saveGenres(db, "data/genres_export.txt");
        FileManager.saveContents(db, "data/contents_export.txt");
        FileManager.saveArchive(archive, "data/archive_export.txt");

        System.out.println("Do export files exist? " + (new File("data/users_export.txt").exists() ? "Yes" : "No"));

        // Binary serialization testing
        System.out.println("Testing binary state serialization save and load...");
        FileManager.saveSystem(db, "data/system_backup.bin");

        StreamingDatabase restoredDb = new StreamingDatabase();
        FileManager.loadSystem(restoredDb, "data/system_backup.bin");

        System.out.println(
                "Is Pride and Prejudice in restored DB? " + (restoredDb.getContent("C1") != null ? "Yes" : "No"));

        System.out.println("--- Consistency, Archiving and Serialization Tests Completed ---\n");
    }
}
