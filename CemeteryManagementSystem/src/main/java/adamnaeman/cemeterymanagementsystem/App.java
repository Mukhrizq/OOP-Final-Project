package adamnaeman.cemeterymanagementsystem;

// Main Application Class
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.*;



public class App extends Application {
    private Cemetery cemetery;
    private ListView<String> lotListView;
    private ListView<String> userListView;
    private TextArea displayArea;

    @Override
    public void start(Stage primaryStage) {
        cemetery = new Cemetery();
        
        primaryStage.setTitle("Cemetery Management System");
        
        // Create main layout
        BorderPane mainPane = new BorderPane();
        
        // Create top menu
        MenuBar menuBar = createMenuBar();
        mainPane.setTop(menuBar);
        
        // Create center content
        TabPane tabPane = new TabPane();
        
        // User Management Tab
        Tab lotTab = new Tab("User Management");
        lotTab.setContent(createUserManagementPane());
        lotTab.setClosable(false);
        
        // Lot Management Tab
        Tab userTab = new Tab("Lot Management");
        userTab.setContent(createLotManagementPane());
        userTab.setClosable(false);
        
        // Display Tab
        Tab displayTab = new Tab("Display");
        displayTab.setContent(createDisplayPane());
        displayTab.setClosable(false);
        
        tabPane.getTabs().addAll(lotTab, userTab, displayTab);
        mainPane.setCenter(tabPane);
        
        Scene scene = new Scene(mainPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Initialize with some sample data
        initializeSampleData();
    }
    /*
        Method createMenuBar will be top header of scene
    */
    
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        
        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().add(exitItem);
        
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());
        helpMenu.getItems().add(aboutItem);
        
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;
    }
    
    
    /*
        Method createLotManagementPane, createUserManagementPane, createDisplayPane will be used 
        for main scene
    */
    // Lot Management
    private VBox createLotManagementPane() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        Label title = new Label("Lot Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Lot creation form
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        
        TextField lotIdField = new TextField();
        TextField corpseNameField = new TextField();
        DatePicker deathDatePicker = new DatePicker();
        CheckBox availableCheckBox = new CheckBox();
        
        form.add(new Label("Lot ID:"), 0, 0);
        form.add(lotIdField, 1, 0);
        form.add(new Label("Corpse Name:"), 0, 1);
        form.add(corpseNameField, 1, 1);
        form.add(new Label("Death Date:"), 0, 2);
        form.add(deathDatePicker, 1, 2);
        form.add(new Label("Available:"), 0, 3);
        form.add(availableCheckBox, 1, 3);
        
        Button createLotBtn = new Button("Create Lot");
        Button updateLotBtn = new Button("Update Selected Lot");
        
        createLotBtn.setOnAction(e -> {
            try {
                int lotId = Integer.parseInt(lotIdField.getText());
                String corpseName = corpseNameField.getText();
                LocalDate deathDate = deathDatePicker.getValue();
                boolean isAvailable = availableCheckBox.isSelected();
                
                // Check if lot ID already exists
                if (cemetery.findLotById(lotId) != null) {
                    showAlert("Error", "Lot ID already exists! Use Update to modify existing lot.");
                    return;
                }
                
                Lot lot = new Lot(lotId, corpseName, deathDate, isAvailable);
                cemetery.addLot(lot);
                updateLotList();
                clearLotForm(lotIdField, corpseNameField, deathDatePicker, availableCheckBox);
                showAlert("Success", "Lot created successfully!");
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid lot ID (number)");
            } catch (Exception ex) {
                showAlert("Error", "Error creating lot: " + ex.getMessage());
            }
        });
        
        updateLotBtn.setOnAction(e -> {
            try {
                String selected = lotListView.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    showAlert("Error", "Please select a lot to update");
                    return;
                }
                
                int selectedLotId = extractLotId(selected);
                Lot lot = cemetery.findLotById(selectedLotId);
                
                if (lot != null) {
                    // Update with form values (only if fields are not empty)
                    if (!lotIdField.getText().isEmpty()) {
                        int newLotId = Integer.parseInt(lotIdField.getText());
                        if (newLotId != selectedLotId && cemetery.findLotById(newLotId) != null) {
                            showAlert("Error", "New Lot ID already exists!");
                            return;
                        }
                        lot.setLotId(newLotId);
                    }
                    
                    if (!corpseNameField.getText().isEmpty()) {
                        lot.setCorpseName(corpseNameField.getText());
                    }
                    
                    if (deathDatePicker.getValue() != null) {
                        lot.setCorpseDateDeath(deathDatePicker.getValue());
                    }
                    
                    lot.setAvailable(availableCheckBox.isSelected());
                    
                    updateLotList();
                    clearLotForm(lotIdField, corpseNameField, deathDatePicker, availableCheckBox);
                    showAlert("Success", "Lot updated successfully!");
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid lot ID (number)");
            } catch (Exception ex) {
                showAlert("Error", "Error updating lot: " + ex.getMessage());
            }
        });
        
        // Load selected lot data into form
        lotListView = new ListView<>();
        lotListView.setPrefHeight(150);
        lotListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadLotIntoForm(newVal, lotIdField, corpseNameField, deathDatePicker, availableCheckBox);
            }
        });
        
        HBox buttonBox = new HBox(10);
        Button toggleAvailabilityBtn = new Button("Toggle Availability");
        Button deleteLotBtn = new Button("Delete Lot");
        
        toggleAvailabilityBtn.setOnAction(e -> {
            String selected = lotListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                int lotId = extractLotId(selected);
                Lot lot = cemetery.findLotById(lotId);
                if (lot != null) {
                    lot.setAvailable(!lot.isAvailable());
                    updateLotList();
                    showAlert("Success", "Lot availability toggled!");
                }
            } else {
                showAlert("Error", "Please select a lot first");
            }
        });
        
        deleteLotBtn.setOnAction(e -> {
            String selected = lotListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                int lotId = extractLotId(selected);
                if (cemetery.removeLot(lotId)) {
                    updateLotList();
                    clearLotForm(lotIdField, corpseNameField, deathDatePicker, availableCheckBox);
                    showAlert("Success", "Lot deleted successfully!");
                } else {
                    showAlert("Error", "Could not delete lot");
                }
            } else {
                showAlert("Error", "Please select a lot first");
            }
        });
        
        buttonBox.getChildren().addAll(createLotBtn, updateLotBtn);
        
        HBox actionButtons = new HBox(10);
        actionButtons.getChildren().addAll(toggleAvailabilityBtn, deleteLotBtn);
        
        vbox.getChildren().addAll(title, form, buttonBox,
                                 new Label("Existing Lots:"), lotListView, actionButtons);
        
        return vbox;
    }
    
    
    
    private VBox createUserManagementPane() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        Label title = new Label("User Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // User creation form
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        
        TextField userIdField = new TextField();
        TextField nameField = new TextField();
        TextField contactField = new TextField();
        TextField addressField = new TextField();
        ComboBox<Integer> lotComboBox = new ComboBox<>();
        
        form.add(new Label("User ID:"), 0, 0);
        form.add(userIdField, 1, 0);
        form.add(new Label("Name:"), 0, 1);
        form.add(nameField, 1, 1);
        form.add(new Label("Contact:"), 0, 2);
        form.add(contactField, 1, 2);
        form.add(new Label("Address:"), 0, 3);
        form.add(addressField, 1, 3);
        form.add(new Label("Book Lot:"), 0, 4);
        form.add(lotComboBox, 1, 4);
        
        HBox loginAndRegister = new HBox(3);
        
        Button createUserBtn = new Button("Register User");
        createUserBtn.setOnAction(e -> {
            try {
                String userId = userIdField.getText();
                String name = nameField.getText();
                String contact = contactField.getText();
                String address = addressField.getText();
                
                Heir heir = new Heir(contact, address, new ArrayList<>(), new ArrayList<>());
                User user = new User(userId, name, new EmailNotificationService(), heir);
                cemetery.addUser(user);
                if (user.registerUser(user)) {
                    showAlert("Success", "Registration successful!");
                    updateUserList();
                        
                } else {
                    showAlert("Error", "Registration failed.");
                }

                // Book lot if selected
                Integer selectedLotId = lotComboBox.getValue();
                if (selectedLotId != null) {
                    bookLotForUser(heir, selectedLotId);
                }

                updateLotComboBox(lotComboBox);
                clearUserForm(userIdField, nameField, contactField, addressField, lotComboBox);
            } catch (Exception ex) {
                showAlert("Error", "Error creating user: " + ex.getMessage());
            }
        });
        
        Button loginUserBtn = new Button("Login User");
        loginUserBtn.setOnAction(e -> {
            try{
                String id = userIdField.getText().trim();
                String name = nameField.getText().trim();
                cemetery.findUserById(id);
                User user = loginUser(id, name);
                if (user != null) {
                    showAlert("Success", "Login successful! Welcome, " + user.getName());
                    
                } else {
                    showAlert("ERROR", "Invalid credentials.");
                }

                
                updateLotComboBox(lotComboBox);
                clearUserForm(userIdField, nameField, contactField, addressField, lotComboBox);
            } catch (Exception ex) {
                showAlert("Error", "Error creating user: " + ex.getMessage());
            }

        });
        
        loginAndRegister.getChildren().addAll(createUserBtn, loginUserBtn);
        
        // User list
        userListView = new ListView<>();
        userListView.setPrefHeight(150);
        
        // Booking management buttons
        HBox bookingButtons = new HBox(10);
        Button viewBookingsBtn = new Button("View User Bookings");
        Button cancelBookingBtn = new Button("Cancel Booking");
        Button addBookingBtn = new Button("Add Booking");
        
        viewBookingsBtn.setOnAction(e -> viewUserBookings());
        cancelBookingBtn.setOnAction(e -> cancelUserBooking());
        addBookingBtn.setOnAction(e -> addBookingToUser());
        
        bookingButtons.getChildren().addAll(viewBookingsBtn, cancelBookingBtn, addBookingBtn);
        
        vbox.getChildren().addAll(title, form, loginAndRegister, 
                                 new Label("Existing Users:"), userListView, bookingButtons);
        
        // Update lot combo box when tab is shown
        updateLotComboBox(lotComboBox);
        
        return vbox;
    }
    
    private VBox createDisplayPane() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        Label title = new Label("System Display");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        HBox buttonBox = new HBox(10);
        Button displayLotsBtn = new Button("Display Available Lots");
        Button displayUsersBtn = new Button("Display Users");
        Button displayAllBtn = new Button("Display All Data");
        
        displayLotsBtn.setOnAction(e -> {
            cemetery.displayAvailableLots();
            updateDisplayArea();
        });
        
        displayUsersBtn.setOnAction(e -> {
            cemetery.displayUsers();
            updateDisplayArea();
        });
        
        displayAllBtn.setOnAction(e -> {
            cemetery.displayAvailableLots();
            cemetery.displayUsers();
            updateDisplayArea();
        });
        
        buttonBox.getChildren().addAll(displayLotsBtn, displayUsersBtn, displayAllBtn);
        
        displayArea = new TextArea();
        displayArea.setEditable(false);
        displayArea.setPrefHeight(400);
        
        vbox.getChildren().addAll(title, buttonBox, new Label("Output:"), displayArea);
        
        return vbox;
    }
    
    private void initializeSampleData() {
        // Add sample lots
        cemetery.addLot(new Lot(1, "", LocalDate.of(1, 1, 1), true));
        cemetery.addLot(new Lot(2, "", LocalDate.of(1, 1, 1), true));
        cemetery.addLot(new Lot(3, "", LocalDate.of(1, 1, 1), true));
        cemetery.addLot(new Lot(4, "", LocalDate.of(1, 1, 1), true));
        cemetery.addLot(new Lot(5, "", LocalDate.of(1, 1, 1), true));
        cemetery.addLot(new Lot(6, "", LocalDate.of(1, 1, 1), true));
        cemetery.addLot(new Lot(7, "", LocalDate.of(1, 1, 1), true));
        cemetery.addLot(new Lot(8, "", LocalDate.of(1, 1, 1), true));
        cemetery.addLot(new Lot(9, "", LocalDate.of(1, 1, 1), true));
        cemetery.addLot(new Lot(10, "", LocalDate.of(1, 1, 1), true));

        updateLotList();
        updateUserList();
    }
    
    private void updateLotList() {
        lotListView.getItems().clear();
        for (Lot lot : cemetery.getLots()) {
            String status = lot.isAvailable() ? "Available" : "Occupied";
            String corpseInfo = lot.getCorpseName().isEmpty() ? "Empty" : lot.getCorpseName();
            lotListView.getItems().add(String.format("Lot %d - %s - %s (%s)", 
                lot.getLotId(), corpseInfo, lot.getCorpseDateDeath(), status));
        }
    }
    
    private void updateUserList() {
        userListView.getItems().clear();
        for (User user : cemetery.getUsers()) {
            userListView.getItems().add(String.format("%s - %s (Contact: %s)", 
                user.getUserId(), user.getName(), user.getHeir().getContactNo()));
        }
    }
    
    private void updateLotComboBox(ComboBox<Integer> comboBox) {
        comboBox.getItems().clear();
        for (Lot lot : cemetery.getLots()) {
            if (lot.isAvailable()) {
                comboBox.getItems().add(lot.getLotId());
            }
        }
    }
    
    private void updateDisplayArea() {
        // This would be updated by the cemetery display methods
        // For now, we'll show a summary
        StringBuilder sb = new StringBuilder();
        sb.append("=== CEMETERY MANAGEMENT SYSTEM ===\n\n");
        sb.append("Available Lots:\n");
        for (Lot lot : cemetery.getAvailableLots()) {
            sb.append(String.format("- Lot %d (Available)\n", lot.getLotId()));
        }
        sb.append("\nUsers:\n");
        for (User user : cemetery.getUsers()) {
            sb.append(String.format("- %s (%s)\n", user.getName(), user.getUserId()));
        }
        displayArea.setText(sb.toString());
    }
    
    private int extractLotId(String lotString) {
        return Integer.parseInt(lotString.split(" ")[1]);
    }
    
    // Using by Update Lot Button
    private void loadLotIntoForm(String lotString, TextField lotIdField, TextField corpseNameField, 
                                DatePicker deathDatePicker, CheckBox availableCheckBox) {
        int lotId = extractLotId(lotString);
        Lot lot = cemetery.findLotById(lotId);
        
        if (lot != null) {
            lotIdField.setText(String.valueOf(lot.getLotId()));
            corpseNameField.setText(lot.getCorpseName());
            deathDatePicker.setValue(lot.getCorpseDateDeath());
            availableCheckBox.setSelected(lot.isAvailable());
        }
    }
    
    private void clearLotForm(TextField lotId, TextField corpseName, 
                             DatePicker deathDate, CheckBox available) {
        lotId.clear();
        corpseName.clear();
        deathDate.setValue(null);
        available.setSelected(false);
    }
  
    /* 
        Method viewUserBookings, cancelUserBooking, addBookingToUser, bookLotToUser will be use in UserManagementTab Tab
    */
    // Method to clear the textfield on User Management Tab
    private void clearUserForm(TextField userId, TextField name, TextField contact, 
                              TextField address, ComboBox<Integer> lotCombo) {
        userId.clear();
        name.clear();
        contact.clear();
        address.clear();
        lotCombo.setValue(null);
    }
    
    // Method to view user that have make a booking
    private void viewUserBookings() {
        String selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Error", "Please select a user first");
            return;
        }
        
        String userId = selectedUser.split(" - ")[0];
        User user = cemetery.findUserById(userId);
        
        if (user != null) {
            List<Lot> bookings = user.getBookLot();
            if (bookings.isEmpty()) {
                showAlert("Info", "User " + user.getName() + " has no bookings");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Bookings for ").append(user.getName()).append(":\n\n");
                for (Lot lot : bookings) {
                    sb.append("Lot ").append(lot.getLotId())
                      .append(" - ").append(lot.isAvailable() ? "Available" : "Occupied")
                      .append("\n");
                }
                showLongAlert("User Bookings", sb.toString());
            }
        }
    }
    
    // Method to cancel the booking for a user
    private void cancelUserBooking() {
        String selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Error", "Please select a user first");
            return;
        }
        
        String userId = selectedUser.split(" - ")[0];
        User user = cemetery.findUserById(userId);
        
        if (user != null && !user.getBookLot().isEmpty()) {
            // Create dialog to select which booking to cancel
            List<Lot> bookings = user.getBookLot();
            ChoiceDialog<String> dialog = new ChoiceDialog<>();
            dialog.setTitle("Cancel Booking");
            dialog.setHeaderText("Select lot booking to cancel:");
            
            for (Lot lot : bookings) {
                dialog.getItems().add("Lot " + lot.getLotId());
            }
            
            if (!dialog.getItems().isEmpty()) {
                dialog.setSelectedItem(dialog.getItems().get(0));
                Optional<String> result = dialog.showAndWait();
                
                if (result.isPresent()) {
                    int lotId = Integer.parseInt(result.get().split(" ")[1]);
                    if (user.getHeir().cancelBookLot(lotId)) {
                        // Make the lot available again
                        Lot lot = cemetery.findLotById(lotId);
                        if (lot != null) {
                            lot.setAvailable(true);
                        }
                        updateLotList();
                        updateUserList();
                        user.getNotificationService().sendNotification("Booking for Lot " + lotId + " has been cancelled");
                        showAlert("Success", "Booking cancelled successfully!");
                    }
                }
            }
        } else {
            showAlert("Info", "User has no bookings to cancel");
        }
    }
    
    //Method to add booking for user
    private void addBookingToUser() {
        String selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Error", "Please select a user first");
            return;
        }
        
        String userId = selectedUser.split(" - ")[0];
        User user = cemetery.findUserById(userId);
        
        if (user != null) {
            List<Lot> availableLots = cemetery.getAvailableLots();
            if (availableLots.isEmpty()) {
                showAlert("Info", "No available lots to book");
                return;
            }
            
            ChoiceDialog<String> dialog = new ChoiceDialog<>();
            dialog.setTitle("Add Booking");
            dialog.setHeaderText("Select available lot to book:");
            
            for (Lot lot : availableLots) {
                dialog.getItems().add("Lot " + lot.getLotId());
            }
            
            dialog.setSelectedItem(dialog.getItems().get(0));
            Optional<String> result = dialog.showAndWait();
            
            if (result.isPresent()) {
                int lotId = Integer.parseInt(result.get().split(" ")[1]);
                bookLotForUser(user.getHeir(), lotId);
                updateLotList();
                updateUserList();
                user.getNotificationService().sendNotification("New booking confirmed for Lot " + lotId);
                showAlert("Success", "Lot booked successfully!");
            }
        }
    }
    
    // Using by create user button
    private void bookLotForUser(Heir heir, int lotId) {
        Lot lot = cemetery.findLotById(lotId);
        if (lot != null && lot.isAvailable()) {
            heir.bookLot(lotId, true);
            lot.setAvailable(false);
            heir.getBookLot().add(lot);
        }
    }
    
    /*
        Method showLongAlert, showAlert, showAboutDialog will be used in this App class only
    */
    private void showLongAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        
        TextArea textArea = new TextArea(message);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        
        alert.getDialogPane().setExpandableContent(textArea);
        alert.getDialogPane().setExpanded(true);
        alert.showAndWait();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Cemetery Management System");
        alert.setContentText("A JavaFX application for managing cemetery lots and users.\nVersion 1.0");
        alert.showAndWait();
    }

    // Login through App.java file
    private static final String FILENAME = "users.txt";
    public User loginUser(String id, String name) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                User u = User.fromCSV(line);
                if (u != null && u.getUserId().equals(id) && u.getName().equals(name)) {
                    return u;
                }
            }
            updateUserList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}

