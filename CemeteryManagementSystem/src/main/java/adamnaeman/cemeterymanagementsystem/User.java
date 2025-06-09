/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adamnaeman.cemeterymanagementsystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author User
 */
public class User {
    private String userId;
    private String name;
    private NotificationService notificationService;
    private Heir heir;
    
    public User(String userId, String name, NotificationService notificationService, Heir heir) {
        this.userId = userId;
        this.name = name;
        this.notificationService = notificationService;
        this.heir = heir;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getName() {
        return name;
    }
    
    public List<Lot> getBookLot() {
        return heir.getBookLot();
    }
    
    public Heir getHeir() {
        return heir;
    }
    
    public NotificationService getNotificationService() {
        return notificationService;
    }
    
    private static final String FILENAME = "users.txt";
    
    public String toCSV() {
    return String.join(",",
        userId,
        name,
        notificationService.getClass().getSimpleName(), // or a meaningful string
        heir.getContactNo(),                            // or serialize more fields if needed
        heir.getAddress()
    );
}

    public static User fromCSV(String csvLine) {
    String[] parts = csvLine.split(",");
    if (parts.length < 5) return null;

    String userId = parts[0];
    String name = parts[1];
    NotificationService ns = new EmailNotificationService(); // assuming one type for now
    Heir heir = new Heir(parts[3], parts[4], null, null);

    return new User(userId, name, ns, heir);
}

    public boolean registerUser(User user) {
        //check for duplicate id
        if (isUserIdExist(user.getUserId())) {
            return false; //if id already exists
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME, true))) {
            bw.write(user.toCSV());
            bw.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isUserIdExist(String userId) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                User u = User.fromCSV(line);
                if (u != null && u.getUserId().equals(userId)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
