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
public class Heir {
    private String contactNo;
    private String address;
    private List<Lot> bookLot;
    private List<Corpse> bookCorpse;
    
    public Heir(String contactNo, String address, List<Lot> bookLot, List<Corpse> bookCorpse) {
        this.contactNo = contactNo;
        this.address = address;
        this.bookLot = bookLot != null ? bookLot : new ArrayList<>();
        this.bookCorpse = bookCorpse != null ? bookCorpse : new ArrayList<>();
    }
    
    public String getContactNo() {
        return contactNo;
    }
    
    public String getAddress() {
        return address;
    }
    
    public List<Lot> getBookLot() {
        return bookLot;
    }
    
    public boolean bookLot(int lotId, boolean booking) {
        // Implementation for booking a lot
        return booking;
    }
    
    public Corpse findCorpse(Corpse corpse) {
        return bookCorpse.stream()
                        .filter(c -> c.equals(corpse))
                        .findFirst()
                        .orElse(null);
    }
    
    public void addCorpse(Corpse corpse) {
        bookCorpse.add(corpse);
    }
    
    public boolean cancelBookLot(int lotId) {
        return bookLot.removeIf(lot -> lot.getLotId() == lotId);
    }
    
    public boolean updateBookLot(int lotId) {
        // Implementation for updating book lot
        return true;
    }
}
