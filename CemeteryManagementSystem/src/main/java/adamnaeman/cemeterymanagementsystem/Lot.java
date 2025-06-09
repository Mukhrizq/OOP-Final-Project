/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adamnaeman.cemeterymanagementsystem;

import java.time.LocalDate;

/**
 *
 * @author User
 */
public class Lot {
    private int lotId;
    private String corpseName;
    private LocalDate corpseDateDeath;
    private boolean isAvailable;
    
    public Lot(int lotId, String corpseName, LocalDate corpseDateDeath, boolean isAvailable) {
        this.lotId = lotId;
        this.corpseName = corpseName;
        this.corpseDateDeath = corpseDateDeath;
        this.isAvailable = isAvailable;
    }
    
    public int getLotId() {
        return lotId;
    }
    
    public void setLotId(int lotId) {
        this.lotId = lotId;
    }
    
    public String getCorpseName() {
        return corpseName;
    }
    
    public void setCorpseName(String corpseName) {
        this.corpseName = corpseName;
    }
    
    public LocalDate getCorpseDateDeath() {
        return corpseDateDeath;
    }
    
    public void setCorpseDateDeath(LocalDate corpseDateDeath) {
        this.corpseDateDeath = corpseDateDeath;
    }
    
    public boolean isAvailable() {
        return isAvailable;
    }
    
    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }
    
    public String toString() {
        return "Lot " + lotId + " - " + (isAvailable ? "Available" : "Occupied");
    }
}
