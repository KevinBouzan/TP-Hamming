package fr.epsi.i4.model;

import java.lang.reflect.Field;
import java.util.*;

public class Cluster {

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

    public Entry removeEntry(int id) {
        return data.remove(id);
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public int getMaximumDistance() {
        int distance = 0;

        for (int i = 0; i < data.size(); i++) {
            for (int j = i + 1; j < data.size(); j++) {
                distance = Math.max(distance, data.get(i).calculateDistance(data.get(j)));
            }
        }

        return distance;
    }
}
