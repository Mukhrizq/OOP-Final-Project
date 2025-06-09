/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adamnaeman.cemeterymanagementsystem;

/**
 *
 * @author User
 */
public class EmailNotificationService implements NotificationService {
    @Override
    public void sendNotification(String message) {
        System.out.println("Email notification sent: " + message);
        // In a real implementation, this would send an actual email
    }
}
