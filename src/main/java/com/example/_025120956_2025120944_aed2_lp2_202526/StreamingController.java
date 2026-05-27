package com.example._025120956_2025120944_aed2_lp2_202526;

import database.StreamingDatabase;
import database.Archive;
import database.FileManager;
import services.SearchEngine;
import models.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class StreamingController {

    // Database and search engine instances
    private StreamingDatabase db = new StreamingDatabase();
    private SearchEngine searchEngine = new SearchEngine(db);
    private Archive archive = new Archive();

    // User tab fields
    @FXML private TextField txtUserId;
    @FXML private TextField txtUserName;
    @FXML private TextField txtUserRegion;
    @FXML private TextField txtUserDate;
    @FXML private TableView<User> tblUsers;
    @FXML private TableColumn<User, String> colUserId;
    @FXML private TableColumn<User, String> colUserName;
    @FXML private TableColumn<User, String> colUserRegion;
    @FXML private TableColumn<User, LocalDate> colUserDate;

    // Artist tab fields
    @FXML private TextField txtArtistId;
    @FXML private TextField txtArtistName;
    @FXML private TextField txtArtistNat;
    @FXML private TextField txtArtistGender;
    @FXML private TextField txtArtistBirth;
    @FXML private TableView<Artist> tblArtists;
    @FXML private TableColumn<Artist, String> colArtistId;
    @FXML private TableColumn<Artist, String> colArtistName;
    @FXML private TableColumn<Artist, String> colArtistNat;
    @FXML private TableColumn<Artist, String> colArtistGender;
    @FXML private TableColumn<Artist, LocalDate> colArtistBirth;

    // Content tab fields
    @FXML private TextField txtContentId;
    @FXML private TextField txtContentTitle;
    @FXML private TextField txtContentType;
    @FXML private TextField txtContentGenre;
    @FXML private TextField txtContentDate;
    @FXML private TextField txtContentDuration;
    @FXML private TableView<Content> tblContents;
    @FXML private TableColumn<Content, String> colContentId;
    @FXML private TableColumn<Content, String> colContentTitle;
    @FXML private TableColumn<Content, ContentType> colContentType;
    @FXML private TableColumn<Content, Genre> colContentGenre;
    @FXML private TableColumn<Content, LocalDate> colContentDate;
    @FXML private TableColumn<Content, Integer> colContentDuration;

    // Genre tab fields
    @FXML private TextField txtGenreId;
    @FXML private TextField txtGenreName;
    @FXML private TableView<Genre> tblGenres;
    @FXML private TableColumn<Genre, String> colGenreId;
    @FXML private TableColumn<Genre, String> colGenreName;

    // Search tab fields
    @FXML private TextField txtSearchUserName;
    @FXML private TextField txtSearchUserRegion;
    @FXML private TextField txtSearchUserStart;
    @FXML private TextField txtSearchUserEnd;
    
    @FXML private TextField txtSearchArtistName;
    @FXML private TextField txtSearchArtistNat;
    @FXML private TextField txtSearchArtistGender;
    @FXML private TextField txtSearchArtistStart;
    @FXML private TextField txtSearchArtistEnd;
    
    @FXML private TextField txtSearchContentTitle;
    @FXML private TextField txtSearchContentType;
    @FXML private TextField txtSearchContentGenre;
    @FXML private TextField txtSearchContentStart;
    @FXML private TextField txtSearchContentEnd;
    @FXML private TextField txtSearchContentMinDur;
    @FXML private TextField txtSearchContentMaxDur;
    
    @FXML private TextArea txtSearchResults;
    @FXML private TextArea txtAnalyticsResults;

    // Run automatically when the UI starts
    @FXML
    public void initialize() {
        // Set up User table columns
        colUserId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUserName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colUserRegion.setCellValueFactory(new PropertyValueFactory<>("region"));
        colUserDate.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));

        // Set up Artist table columns
        colArtistId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colArtistName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colArtistNat.setCellValueFactory(new PropertyValueFactory<>("nationality"));
        colArtistGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colArtistBirth.setCellValueFactory(new PropertyValueFactory<>("birthDate"));

        // Set up Content table columns
        colContentId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colContentTitle.setCellValueFactory(new PropertyValueFactory<>("name"));
        colContentType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colContentGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colContentDate.setCellValueFactory(new PropertyValueFactory<>("releaseDate"));
        colContentDuration.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));

        // Set up Genre table columns
        colGenreId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colGenreName.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Row selection listener on tblUsers to auto-populate fields
        tblUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtUserId.setText(newSelection.getId());
                txtUserName.setText(newSelection.getName());
                txtUserRegion.setText(newSelection.getRegion());
                txtUserDate.setText(newSelection.getRegistrationDate() != null ? newSelection.getRegistrationDate().toString() : "");
            }
        });

        // Row selection listener on tblArtists to auto-populate fields
        tblArtists.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtArtistId.setText(newSelection.getId());
                txtArtistName.setText(newSelection.getName());
                txtArtistNat.setText(newSelection.getNationality());
                txtArtistGender.setText(newSelection.getGender());
                txtArtistBirth.setText(newSelection.getBirthDate() != null ? newSelection.getBirthDate().toString() : "");
            }
        });

        // Row selection listener on tblContents to auto-populate fields
        tblContents.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtContentId.setText(newSelection.getId());
                txtContentTitle.setText(newSelection.getName());
                txtContentType.setText(newSelection.getType() != null ? newSelection.getType().name() : "");
                txtContentGenre.setText(newSelection.getGenre() != null ? newSelection.getGenre().getId() : "");
                txtContentDate.setText(newSelection.getReleaseDate() != null ? newSelection.getReleaseDate().toString() : "");
                txtContentDuration.setText(String.valueOf(newSelection.getDurationMinutes()));
            }
        });

        // Row selection listener on tblGenres to auto-populate fields
        tblGenres.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtGenreId.setText(newSelection.getId());
                txtGenreName.setText(newSelection.getName());
            }
        });

        // Load dummy starting data
        loadDummyData();

        // Refresh all lists
        showUsers();
        showArtists();
        showContents();
        showGenres();
    }

    // Loads some random example data for testing
    private void loadDummyData() {
        Genre g1 = new Genre("G_ACTION", "Action");
        Genre g2 = new Genre("G_COMEDY", "Comedy");
        db.insertGenre(g1);
        db.insertGenre(g2);

        User u1 = new User("U1", "Alice Smith", "Europe", LocalDate.of(2025, 1, 10));
        User u2 = new User("U2", "Bob Jones", "North America", LocalDate.of(2025, 2, 20));
        db.insertUser(u1);
        db.insertUser(u2);

        Artist a1 = new Artist("A1", "David Bowie", "British", "Male", LocalDate.of(1947, 1, 8));
        Artist a2 = new Artist("A2", "Meryl Streep", "American", "Female", LocalDate.of(1949, 6, 22));
        db.insertArtist(a1);
        db.insertArtist(a2);

        Content c1 = new Content("C1", "Inception", ContentType.MOVIE, g1, LocalDate.of(2010, 7, 16), 148);
        Content c2 = new Content("C2", "Friends", ContentType.SERIES, g2, LocalDate.of(1994, 9, 22), 22);
        db.insertContent(c1);
        db.insertContent(c2);
    }

    // Refresh Users Table View
    private void showUsers() {
        ObservableList<User> list = FXCollections.observableArrayList();
        for (User u : db.listAllUsers()) {
            list.add(u);
        }
        tblUsers.setItems(list);
        searchEngine.rebuildIndexes(db);
    }

    // Refresh Artists Table View
    private void showArtists() {
        ObservableList<Artist> list = FXCollections.observableArrayList();
        for (Artist a : db.listAllArtists()) {
            list.add(a);
        }
        tblArtists.setItems(list);
        searchEngine.rebuildIndexes(db);
    }

    // Refresh Contents Table View
    private void showContents() {
        ObservableList<Content> list = FXCollections.observableArrayList();
        for (Content c : db.listAllContents()) {
            list.add(c);
        }
        tblContents.setItems(list);
        searchEngine.rebuildIndexes(db);
    }

    // Refresh Genres Table View
    private void showGenres() {
        ObservableList<Genre> list = FXCollections.observableArrayList();
        for (Genre g : db.listAllGenres()) {
            list.add(g);
        }
        tblGenres.setItems(list);
    }

    // --- User Buttons ---
    @FXML
    public void onAddUserClick() {
        try {
            String id = txtUserId.getText();
            String name = txtUserName.getText();
            String region = txtUserRegion.getText();
            LocalDate date = LocalDate.parse(txtUserDate.getText());

            User user = new User(id, name, region, date);
            db.insertUser(user);
            showUsers();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @FXML
    public void onEditUserClick() {
        try {
            String id = txtUserId.getText().trim();
            if (id.isEmpty()) return;

            String name = txtUserName.getText().trim();
            if (name.isEmpty()) name = null;

            String region = txtUserRegion.getText().trim();
            if (region.isEmpty()) region = null;

            LocalDate date = null;
            String dateText = txtUserDate.getText().trim();
            if (!dateText.isEmpty()) {
                date = LocalDate.parse(dateText);
            }

            db.editUser(id, name, region, date);
            showUsers();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @FXML
    public void onDeleteUserClick() {
        try {
            String id = txtUserId.getText();
            User u = db.getUser(id);
            if (u != null) {
                archive.archiveUser(u);
                searchEngine.removeUser(u);
                db.removeUser(id);
                showUsers();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // --- Artist Buttons ---
    @FXML
    public void onAddArtistClick() {
        try {
            String id = txtArtistId.getText();
            String name = txtArtistName.getText();
            String nat = txtArtistNat.getText();
            String gender = txtArtistGender.getText();
            LocalDate birth = LocalDate.parse(txtArtistBirth.getText());

            Artist artist = new Artist(id, name, nat, gender, birth);
            db.insertArtist(artist);
            showArtists();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @FXML
    public void onEditArtistClick() {
        try {
            String id = txtArtistId.getText().trim();
            if (id.isEmpty()) return;

            String name = txtArtistName.getText().trim();
            if (name.isEmpty()) name = null;

            String nat = txtArtistNat.getText().trim();
            if (nat.isEmpty()) nat = null;

            String gender = txtArtistGender.getText().trim();
            if (gender.isEmpty()) gender = null;

            LocalDate birth = null;
            String birthText = txtArtistBirth.getText().trim();
            if (!birthText.isEmpty()) {
                birth = LocalDate.parse(birthText);
            }

            db.editArtist(id, name, nat, gender, birth);
            showArtists();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @FXML
    public void onDeleteArtistClick() {
        try {
            String id = txtArtistId.getText();
            Artist a = db.getArtist(id);
            if (a != null) {
                archive.archiveArtist(a);
                searchEngine.removeArtist(a);
                db.removeArtist(id);
                showArtists();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // --- Content Buttons ---
    @FXML
    public void onAddContentClick() {
        try {
            String id = txtContentId.getText().trim();
            String title = txtContentTitle.getText().trim();
            ContentType type = ContentType.valueOf(txtContentType.getText().trim().toUpperCase());
            
            String genreText = txtContentGenre.getText().trim();
            Genre genre = db.getGenre(genreText);
            if (genre == null) {
                for (Genre g : db.listAllGenres()) {
                    if (g.getName() != null && g.getName().equalsIgnoreCase(genreText)) {
                        genre = g;
                        break;
                    }
                }
            }
            
            LocalDate date = LocalDate.parse(txtContentDate.getText().trim());
            int duration = Integer.parseInt(txtContentDuration.getText().trim());

            Content content = new Content(id, title, type, genre, date, duration);
            db.insertContent(content);
            showContents();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @FXML
    public void onEditContentClick() {
        try {
            String id = txtContentId.getText().trim();
            if (id.isEmpty()) return;

            Content content = db.getContent(id);
            if (content == null) return;

            String title = txtContentTitle.getText().trim();
            if (title.isEmpty()) title = null;

            ContentType type = null;
            String typeText = txtContentType.getText().trim();
            if (!typeText.isEmpty()) {
                type = ContentType.valueOf(typeText.toUpperCase());
            }

            Genre genre = null;
            String genreText = txtContentGenre.getText().trim();
            if (!genreText.isEmpty()) {
                genre = db.getGenre(genreText);
                if (genre == null) {
                    for (Genre g : db.listAllGenres()) {
                        if (g.getName() != null && g.getName().equalsIgnoreCase(genreText)) {
                            genre = g;
                            break;
                        }
                    }
                }
            } else {
                genre = content.getGenre();
            }

            LocalDate date = null;
            String dateText = txtContentDate.getText().trim();
            if (!dateText.isEmpty()) {
                date = LocalDate.parse(dateText);
            }

            int duration = 0;
            String durationText = txtContentDuration.getText().trim();
            if (!durationText.isEmpty()) {
                duration = Integer.parseInt(durationText);
            }

            db.editContent(id, title, type, genre, date, duration);
            showContents();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @FXML
    public void onDeleteContentClick() {
        try {
            String id = txtContentId.getText();
            Content c = db.getContent(id);
            if (c != null) {
                archive.archiveContent(c);
                searchEngine.removeContent(c);
                db.removeContent(id);
                showContents();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // --- Genre Buttons ---
    @FXML
    public void onAddGenreClick() {
        try {
            String id = txtGenreId.getText();
            String name = txtGenreName.getText();

            Genre genre = new Genre(id, name);
            db.insertGenre(genre);
            showGenres();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @FXML
    public void onEditGenreClick() {
        try {
            String id = txtGenreId.getText().trim();
            if (id.isEmpty()) return;
            String name = txtGenreName.getText().trim();
            if (name.isEmpty()) name = null;

            db.editGenre(id, name);
            showGenres();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @FXML
    public void onDeleteGenreClick() {
        try {
            String id = txtGenreId.getText();
            Genre g = db.getGenre(id);
            if (g != null) {
                archive.archiveGenre(g);
                db.removeGenre(id);
                showGenres();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // --- Search Tab Buttons ---
    @FXML
    public void onSearchUsersClick() {
        try {
            String name = txtSearchUserName.getText();
            String region = txtSearchUserRegion.getText();
            String start = txtSearchUserStart.getText().trim();
            String end = txtSearchUserEnd.getText().trim();

            String startDate = start.isEmpty() ? null : start;
            String endDate = end.isEmpty() ? null : end;

            String res = "--- User Search Results ---\n";
            Iterable<User> users = searchEngine.findUsersByNameAndRegionAndDate(name, region, startDate, endDate);
            int count = 0;
            for (User u : users) {
                res = res + "ID: " + u.getId() + " | Name: " + u.getName() + " | Region: " + u.getRegion() + " | Reg: " + u.getRegistrationDate() + "\n";
                count++;
            }
            res = res + "\nTotal found: " + count + "\n";
            txtSearchResults.setText(res);
        } catch (Exception e) {
            txtSearchResults.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    public void onSearchArtistsClick() {
        try {
            String name = txtSearchArtistName.getText();
            String nation = txtSearchArtistNat.getText();
            String gender = txtSearchArtistGender.getText();
            String start = txtSearchArtistStart.getText().trim();
            String end = txtSearchArtistEnd.getText().trim();

            String startDate = start.isEmpty() ? null : start;
            String endDate = end.isEmpty() ? null : end;

            String res = "--- Artist Search Results ---\n";
            Iterable<Artist> artists = searchEngine.findArtistsByNameAndNationalityAndGenderAndBirthDate(name, nation, gender, startDate, endDate);
            int count = 0;
            for (Artist a : artists) {
                res = res + "ID: " + a.getId() + " | Name: " + a.getName() + " | Nation: " + a.getNationality() + " | Gender: " + a.getGender() + " | Birth: " + a.getBirthDate() + "\n";
                count++;
            }
            res = res + "\nTotal found: " + count + "\n";
            txtSearchResults.setText(res);
        } catch (Exception e) {
            txtSearchResults.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    public void onSearchContentsClick() {
        try {
            String title = txtSearchContentTitle.getText();
            String typeStr = txtSearchContentType.getText().trim().toUpperCase();
            String genreId = txtSearchContentGenre.getText().trim();
            String start = txtSearchContentStart.getText().trim();
            String end = txtSearchContentEnd.getText().trim();
            String minDurStr = txtSearchContentMinDur.getText().trim();
            String maxDurStr = txtSearchContentMaxDur.getText().trim();

            ContentType type = typeStr.isEmpty() ? null : ContentType.valueOf(typeStr);
            String startDate = start.isEmpty() ? null : start;
            String endDate = end.isEmpty() ? null : end;

            String res = "--- Content Search Results ---\n";
            Iterable<Content> contents;

            // Check if duration range query is requested
            if (!minDurStr.isEmpty() || !maxDurStr.isEmpty()) {
                int minDur = minDurStr.isEmpty() ? 0 : Integer.parseInt(minDurStr);
                int maxDur = maxDurStr.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(maxDurStr);
                contents = searchEngine.findContentsByGenreAndDurationRange(genreId, minDur, maxDur);
            } else {
                contents = searchEngine.findContentsByTitleAndTypeAndGenreAndDate(title, type, genreId, startDate, endDate);
            }

            int count = 0;
            for (Content c : contents) {
                res = res + "ID: " + c.getId() + " | Title: " + c.getName() + " | Type: " + c.getType() + " | Genre: " + (c.getGenre() != null ? c.getGenre().getName() : "None") + " | Release: " + c.getReleaseDate() + " | Duration: " + c.getDurationMinutes() + " mins\n";
                count++;
            }
            res = res + "\nTotal found: " + count + "\n";
            txtSearchResults.setText(res);
        } catch (Exception e) {
            txtSearchResults.setText("Error: " + e.getMessage());
        }
    }

    // --- Import / Export Handlers ---
    @FXML
    public void onImportTextClick() {
        try {
            FileManager.loadUsers("data/users_export.txt", db);
            FileManager.loadArtists("data/artists_export.txt", db);
            FileManager.loadGenres("data/genres_export.txt", db);
            FileManager.loadContents("data/contents_export.txt", db);

            showUsers();
            showArtists();
            showContents();
            showGenres();

            txtAnalyticsResults.setText("System: CSV Text Data successfully imported from 'data/' directory.");
        } catch (Exception e) {
            txtAnalyticsResults.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    public void onExportTextClick() {
        try {
            java.io.File dataDir = new java.io.File("data");
            if (!dataDir.exists()) {
                dataDir.mkdir();
            }
            FileManager.saveUsers(db, "data/users_export.txt");
            FileManager.saveArtists(db, "data/artists_export.txt");
            FileManager.saveGenres(db, "data/genres_export.txt");
            FileManager.saveContents(db, "data/contents_export.txt");
            FileManager.saveArchive(archive, "data/archive_export.txt");
            txtAnalyticsResults.setText("System: All database tables successfully backed up as flat semicolon-separated files under 'data/' directory.");
        } catch (Exception e) {
            txtAnalyticsResults.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    public void onSaveBinaryClick() {
        try {
            FileManager.saveSystem(db, "data/system_backup.bin");
            txtAnalyticsResults.setText("System: Complete database snapshot successfully captured and serialized to: 'data/system_backup.bin'\n" + "Users, artists, contents, and genres are fully preserved!");
        } catch (Exception e) {
            txtAnalyticsResults.setText("Error saving state: " + e.getMessage());
        }
    }

    @FXML
    public void onLoadBinaryClick() {
        try {
            StreamingDatabase restored = new StreamingDatabase();
            FileManager.loadSystem(restored, "data/system_backup.bin");
            db = restored;
            searchEngine = new SearchEngine(db);

            showUsers();
            showArtists();
            showContents();
            showGenres();

            txtAnalyticsResults.setText("System: Complete database snapshot successfully loaded and reconstructed from: 'data/system_backup.bin'\n" + "All records and binary tables are fully restored and online.");
        } catch (Exception e) {
            txtAnalyticsResults.setText("Error loading state: " + e.getMessage());
        }
    }
}
