package database;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
import models.Content;
import models.ContentType;
import models.Genre;
import models.User;
import models.Artist;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    /**
     * Loads users from into the database.
     * Expected format: ID;Name;Region;RegistrationDate(YYYY-MM-DD)
     *
     * @param filepath path to the source text file
     * @param db       the active database instance to populate
     */
    public static void loadUsers(String filepath, StreamingDatabase db) {
        In in = new In(filepath);
        if (!in.exists()) {
            System.out.println("File not found: " + filepath);
            return;
        }

        while (!in.isEmpty()) {
            String line = in.readLine().replace("\uFEFF", "").trim();
            if (line.isEmpty() || line.startsWith("#"))
                continue;

            String[] parts = line.split(";");
            if (parts.length == 4) {
                try {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String region = parts[2].trim();
                    LocalDate date = LocalDate.parse(parts[3].trim());

                    User user = new User(id, name, region, date);
                    db.insertUser(user);
                } catch (DateTimeParseException e) {
                    System.out.println("Date format error (Skipping line): " + line);
                }
            }
        }
        in.close();
        System.out.println("System: Users loaded successfully.");
    }

    /**
     * Loads media content,linking artists and generating genres.
     * Expected format:
     * ID;Name;Type;Genre;ReleaseDate(YYYY-MM-DD);Duration;ArtistIDs
     *
     * @param filepath path to the content data file
     * @param db       the active database instance to populate
     */
    public static void loadContents(String filepath, StreamingDatabase db) {
        In in = new In(filepath);
        if (!in.exists()) {
            System.out.println("File not found: " + filepath);
            return;
        }

        while (!in.isEmpty()) {
            String line = in.readLine().replace("\uFEFF", "").trim();
            if (line.isEmpty() || line.startsWith("#"))
                continue;

            String[] parts = line.split(";");
            if (parts.length >= 6) {
                try {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    ContentType type = ContentType.valueOf(parts[2].trim().toUpperCase());

                    String genreName = parts[3].trim().toUpperCase();
                    String genreId = "G_" + genreName;
                    Genre genre = db.getGenre(genreId);

                    if (genre == null) {
                        genre = new Genre(genreId, genreName);
                        db.insertGenre(genre);
                    }

                    LocalDate date = LocalDate.parse(parts[4].trim());
                    int duration = Integer.parseInt(parts[5].trim());

                    Content content = new Content(id, name, type, genre, date, duration);
                    db.insertContent(content);

                    if (parts.length == 7) {
                        String[] artistIds = parts[6].split(",");
                        for (String artistId : artistIds) {
                            db.addArtistToContent(id, artistId.trim());
                        }
                    }

                } catch (Exception e) {
                    System.out.println("Content format error (Skipping line): " + line);
                }
            }
        }
        in.close();
        System.out.println("System: Contents loaded successfully.");
    }

    /**
     * Loads genre definitions into the system.
     * Expected format: ID;Name
     *
     * @param filepath path to the genre data file
     * @param db       the active database instance to populate
     */
    public static void loadGenres(String filepath, StreamingDatabase db) {
        In in = new In(filepath);
        if (!in.exists()) {
            System.out.println("Error: File not found at " + filepath);
            return;
        }

        while (!in.isEmpty()) {
            String line = in.readLine().replace("\uFEFF", "").trim();
            if (line.isEmpty() || line.startsWith("#"))
                continue;

            String[] parts = line.split(";");
            if (parts.length == 2) {
                String id = parts[0].trim();
                String name = parts[1].trim();

                Genre genre = new Genre(id, name);
                db.insertGenre(genre);
            }
        }
        in.close();
        System.out.println("System: Genres loaded successfully.");
    }

    /**
     * Loads artists from a text file.
     * Expected format: ID;Name;Nationality;Gender;BirthDate(YYYY-MM-DD)
     *
     * @param filepath path to the artist records file
     * @param db       the active database instance to populate
     */
    public static void loadArtists(String filepath, StreamingDatabase db) {
        In in = new In(filepath);
        if (!in.exists()) {
            System.out.println("Error: File not found at " + filepath);
            return;
        }

        while (!in.isEmpty()) {
            String line = in.readLine().replace("\uFEFF", "").trim();
            if (line.isEmpty() || line.startsWith("#"))
                continue;

            String[] parts = line.split(";");
            if (parts.length == 5) {
                try {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String nationality = parts[2].trim();
                    String gender = parts[3].trim();
                    LocalDate birthDate = LocalDate.parse(parts[4].trim());

                    Artist artist = new Artist(id, name, nationality, gender, birthDate);
                    db.insertArtist(artist);
                } catch (DateTimeParseException e) {
                    System.out.println("Warning: Invalid date format. Skipping line: " + line);
                }
            }
        }
        in.close();
        System.out.println("System: Artists loaded successfully.");
    }

    /**
     * Captures a runtime state snapshot by compiling entity collections
     * and writing them into a single serialized binary object stream.
     *
     * @param db       source database engine containing data states
     * @param filepath destination path for the binary state file
     */
    public static void saveSystem(StreamingDatabase db, String filepath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filepath))) {
            List<User> userList = new ArrayList<>();
            for (String id : db.getAllUserIds())
                userList.add(db.getUser(id));

            List<Genre> genreList = new ArrayList<>();
            for (String id : db.getAllGenreIds())
                genreList.add(db.getGenre(id));

            List<Artist> artistList = new ArrayList<>();
            for (String id : db.getAllArtistIds())
                artistList.add(db.getArtist(id));

            List<Content> contentList = new ArrayList<>();
            for (String id : db.getAllContentIds())
                contentList.add(db.getContent(id));

            oos.writeObject(userList);
            oos.writeObject(genreList);
            oos.writeObject(artistList);
            oos.writeObject(contentList);

            System.out.println("System successfully saved to: " + filepath);
        } catch (IOException e) {
            System.out.println("Error saving system: " + e.getMessage());
        }
    }

    /**
     * Restores application states from a binary file, resolving and re-linking
     * object
     * reference paths to secure data topology across internal collections.
     *
     * @param db       target database instance where tables will be restored
     * @param filepath source binary file containing serialized snapshots
     */
    @SuppressWarnings("unchecked")
    public static void loadSystem(StreamingDatabase db, String filepath) {
        File file = new File(filepath);
        if (!file.exists()) {
            System.out.println("No previous save found at: " + filepath);
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filepath))) {
            List<User> userList = (List<User>) ois.readObject();
            List<Genre> genreList = (List<Genre>) ois.readObject();
            List<Artist> artistList = (List<Artist>) ois.readObject();
            List<Content> contentList = (List<Content>) ois.readObject();

            for (User u : userList)
                db.insertUser(u);
            for (Artist a : artistList)
                db.insertArtist(a);
            for (Genre g : genreList)
                db.insertGenre(g);

            for (Content c : contentList) {
                if (c.getGenre() != null) {
                    Genre originalGenre = db.getGenre(c.getGenre().getId());
                    c.setGenre(originalGenre);
                }

                if (c.getCast() != null) {
                    edu.princeton.cs.algs4.Queue<Artist> tempQueue = new edu.princeton.cs.algs4.Queue<>();
                    for (Artist fakeArtist : c.getCast())
                        tempQueue.enqueue(fakeArtist);

                    while (!c.getCast().isEmpty())
                        c.getCast().delete(c.getCast().max());

                    for (Artist fakeArtist : tempQueue) {
                        Artist originalArtist = db.getArtist(fakeArtist.getId());
                        if (originalArtist != null)
                            c.addArtist(originalArtist);
                    }
                }
                db.insertContent(c);
            }
            System.out.println("System successfully loaded from: " + filepath);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading system: " + e.getMessage());
        }
    }

    /**
     * Saves all archived (deleted) entities to a text file.
     *
     * @param archive  the archive instance containing deleted entities
     * @param filepath path to the destination text file
     */
    public static void saveArchive(Archive archive, String filepath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            writer.println("# ARCHIVED ENTITIES");

            writer.println("#--- USERS ---");
            for (User u : archive.getDeletedUsers()) {
                writer.println(u.getId() + ";" + u.getName() + ";" + u.getRegion() + ";" + u.getRegistrationDate());
            }

            writer.println("#--- ARTISTS ---");
            for (Artist a : archive.getDeletedArtists()) {
                writer.println(a.getId() + ";" + a.getName() + ";" + a.getNationality() + ";" + a.getGender() + ";"
                        + a.getBirthDate());
            }

            writer.println("#--- GENRES ---");
            for (Genre g : archive.getDeletedGenres()) {
                writer.println(g.getId() + ";" + g.getName());
            }

            writer.println("#--- CONTENTS ---");
            for (Content c : archive.getDeletedContents()) {
                StringBuilder artistsSb = new StringBuilder();
                if (c.getCast() != null) {
                    boolean first = true;
                    for (Artist a : c.getCast()) {
                        if (!first)
                            artistsSb.append(",");
                        artistsSb.append(a.getId());
                        first = false;
                    }
                }
                String genreName = c.getGenre() != null ? c.getGenre().getName() : "";
                writer.println(c.getId() + ";" + c.getName() + ";" + c.getType() + ";" + genreName + ";"
                        + c.getReleaseDate() + ";" + c.getDurationMinutes() + ";" + artistsSb.toString());
            }

            System.out.println("Archive successfully saved to text file: " + filepath);
        } catch (IOException e) {
            System.out.println("Error saving archive: " + e.getMessage());
        }
    }
}