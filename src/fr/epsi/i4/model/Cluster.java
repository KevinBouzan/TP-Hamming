package fr.epsi.i4.model;

import java.util.*;

/**
 * Classe Cluster qui represente un cluster de données
 * @author kbouzan
 */
public class Cluster {

    /**
     * Liste des données du cluster
     */
    private List<Entry> data;

    public Cluster() {
        data = new ArrayList<>();
    }

    public List<Entry> getData() {
        return data;
    }

    public void setData(List<Entry> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "data=" + data +
                '}';
    }

    public Entry addEntry(Entry entry) {
        if (data.add(entry)) {
            return entry;
        }
        return null;
    }
    
    public boolean isEmpty() {
        return data.isEmpty();
    }
}
