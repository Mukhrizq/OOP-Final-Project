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
public class Corpse {
    private LocalDate corpseDateDeath;
    
    public Corpse(LocalDate corpseDateDeath) {
        this.corpseDateDeath = corpseDateDeath;
    }
    
    public LocalDate getCorpseDateDeath() {
        return corpseDateDeath;
    }
}
