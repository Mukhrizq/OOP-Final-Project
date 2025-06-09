/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adamnaeman.cemeterymanagementsystem;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author User
 */
public class Cemetery {
    private List<Lot> lots;
    private List<User> users;
    
    public Cemetery() {
        this.lots = new ArrayList<>();
        this.users = new ArrayList<>();
    }
    
    public void addUser(User user) {
        users.add(user);
    }
    
    public void addLot(Lot lot) {
        lots.add(lot);
    }
    
    
    public List<Lot> getAvailableLots() {
        return lots.stream()
                  .filter(Lot::isAvailable)
                  .collect(java.util.stream.Collectors.toList());
    }
    
    public void displayAvailableLots() {
        System.out.println("Available Lots:");
        for (Lot lot : getAvailableLots()) {
            System.out.println("Lot ID: " + lot.getLotId());
        }
    }
    
    public void displayUsers() {
        System.out.println("Users:");
        for (User user : users) {
            System.out.println("User: " + user.getName() + " (ID: " + user.getUserId() + ")");
        }
    }
    
    public List<Lot> getLots() {
        return lots;
    }
    
    public List<User> getUsers() {
        return users;
    }
    
    public Lot findLotById(int lotId) {
        return lots.stream()
                  .filter(lot -> lot.getLotId() == lotId)
                  .findFirst()
                  .orElse(null);
    }
    
    public User findUserById(String userId) {
        return users.stream()
                   .filter(user -> user.getUserId().equals(userId))
                   .findFirst()
                   .orElse(null);
    }
    
    public boolean removeLot(int lotId) {
        return lots.removeIf(lot -> lot.getLotId() == lotId);
    }
}
