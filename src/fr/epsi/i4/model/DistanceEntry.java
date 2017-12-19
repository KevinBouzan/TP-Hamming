/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.epsi.i4.model;

/**
 *
 * @author kbouzan
 */
public class DistanceEntry {
    private int distance;
    private Entry entry;

    public DistanceEntry(int distance, Entry entry) {
        this.distance = distance;
        this.entry = entry;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Entry getEntry() {
        return entry;
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }
    
    
}
