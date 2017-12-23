package fr.epsi.i4.model;

import java.util.ArrayList;
import java.util.List;

public class Master {

    private List<Cluster> clusters;
    private List<Entry> data;

    public Master() {
        clusters = new ArrayList<>();
        data = new ArrayList<>();
    }

    public List<Cluster> getClusters() {
        return clusters;
    }

    public List<Entry> getData() {
        return data;
    }

    public void setData(List<Entry> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Master: ");
        for (int i = 0; i < clusters.size(); i++) {
            stringBuilder
                    .append("\n---------------------------------")
                    .append("\nCluster ")
                    .append(i + 1);
            for (Entry entry : clusters.get(i).getData()) {
                stringBuilder
                        .append("\n")
                        .append(entry.toString());
            }
        }
        return stringBuilder.toString();
    }

    public Entry addEntry(Entry entry) {
        if (data.add(entry)) {
            return entry;
        }
        return null;
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

    public Cluster getFirstEmptyCluster() {
        for (Cluster c : clusters) {
            if (c.isEmpty()) {
                return c;
            }
        }

        return clusters.get(0);
    }

    public void displayDistances() {
        for (int i = 0; i < data.size(); i++) {
            for (int j = i + 1; j < data.size(); j++) {
                System.out.println("Distance between " + data.get(i).getId() + " and " + data.get(j).getId() + " : " + data.get(i).calculateDistance(data.get(j)));
            }
        }
    }

    public void dispatch(int n) {
        for (int i = 0; i < n; i++) {
            clusters.add(new Cluster());
        }
        int i;
        boolean trouve;
        Entry entryResult;
        Entry entryMax = null;
        Entry entry;
        Cluster clusterMax = null;
        int dist;
        int count = 0;
        int max = data.size();
        List<Entry> listEntryOneDistance;
        Cluster cluster = null;

        while (!data.isEmpty()) {
            listEntryOneDistance = null;
            i = 0;
            dist = 0;
            trouve = false;
            entry = getFirstEntry();
            cluster = getFirstEmptyCluster();
            if (cluster.isEmpty()) {
//                if (entry.checkIfCloseTo(clusters) == null || data.size() < 2) {
                    cluster.addEntry(entry);
                    data.remove(entry);
//                    listEntryOneDistance = entry.getEntryWithDistanceOne(data);
//                    for (Entry entryOfList : listEntryOneDistance) {
//                        cluster.addEntry(entryOfList);
//                        data.remove(entryOfList);
//                    }
//                } else {
//                    data.remove(entry);
//                }
            } else {
                while (i < clusters.size() && !trouve) {
                    entryResult = entry.checkDistanceWithClusters(clusters.get(i), clusters);
                    if (entryResult == entry) {
                        clusters.get(i).addEntry(entry);
                        data.remove(entry);
                        trouve = true;
                    }
                    i++;
                }
                if (!trouve) {
                    if (data.size() > 1) {

                        entryMax = entry.getEntryToSwitch(clusters);
//                        data.add(entryMax);
//                        data.remove(entry);
//                        data.add(entry);
                        data.add(entryMax);
                        count++;
                    } else {
                        getFirstEmptyCluster().addEntry(data.get(0));
                        data.remove(0);
                    }
                }
                if (count == max) {
                    System.out.println("suppression de la données " + data.get(data.size() - 1).toString() + " car il n'est pas possible de l'inserer dans un des " + n + " clusters");
                    getFirstEmptyCluster().addEntry(data.get(data.size() - 1));
                    data.remove(data.size() - 1);
                    count = 0;
//                    data.add(entryMax);
//                    clusterMax.getData().remove(entryMax);
//                    clusterMax.addEntry(entry);
//                    data.remove(entry);
                }
            }
        }
    }

    public void dispatch() {
        clusters.add(new Cluster());
        clusters.add(new Cluster());
        int i;
        boolean trouve;
        Entry entryResult;
        Entry entryMax = null;
        Entry entry;
        Cluster clusterMax = null;
        int dist;
        int count = 0;
        int max = data.size() * 10;
        while (!data.isEmpty()) {
            i = 0;
            dist = 100000;
            trouve = false;
            entry = getFirstEntry();
            if (getFirstEmptyCluster().isEmpty()) {
                getFirstEmptyCluster().addEntry(entry);
                data.remove(entry);
            } else {
                while (i < clusters.size() && !trouve) {
                    entryResult = entry.checkDistanceWithClusters(clusters.get(i), clusters);
                    if (entryResult == entry) {
                        clusters.get(i).addEntry(entry);
                        data.remove(entry);
                        trouve = true;
                    } else {
                        if (entry.calculateDistance(entryResult) < dist) {
                            dist = entry.calculateDistance(entryResult);
                            entryMax = entryResult;
                            clusterMax = clusters.get(i);
                        }
                    }
                    i++;
                }
                if (!trouve) {
                    data.add(entryMax);
                    clusterMax.getData().remove(entryMax);
                    clusterMax.addEntry(entry);
                    data.remove(entry);
                    count++;
                    if (count == max) {
                        generateNewCluster(entryMax);
                        count = 0;
                    }
                }
//                if (count == max) {
//                    generateNewCluster(entryMax);

//                    clusters.add(new Cluster());
//                    getFirstEmptyCluster().addEntry(entryMax);
//                    data.remove(entryMax);
//                    int newDist = 10000;
//                    for (Cluster cluster : clusters) {
//                        if (entryMax.getMinimumDistanceWithCluster(clusterMax).getDistance() < newDist) {
//                            newDist = entryMax.getMinimumDistanceWithCluster(clusterMax).getDistance();
//                        }
//                    }
//                }
            }
        }
    }

    public Entry getFirstEntry() {
        Entry result = null;
        if (!data.isEmpty()) {
            result = data.get(0);
        }
        return result;
    }

    public void generateNewCluster(Entry entryMax) {
        clusters.add(new Cluster());
        Cluster newCluster = getFirstEmptyCluster();
        newCluster.addEntry(entryMax);
        data.remove(entryMax);
        int newDist = 10000;
        int maxDist = 10000;
        DistanceEntry distEntry;
        for (Cluster cluster : clusters) {
            if (cluster != newCluster) {
                distEntry = entryMax.getMinimumDistanceWithCluster(cluster);
                while (maxDist >= distEntry.getDistance()) {
                    data.add(distEntry.getEntry());
                    cluster.getData().remove(distEntry.getEntry());
                    maxDist = entryMax.calculateDistance(distEntry.getEntry());
                    distEntry = entryMax.getMinimumDistanceWithCluster(cluster);
                }
            }
        }

    }

    public void addManyEntries(Entry entry, Cluster cluster) {
        List<Entry> listEntryOneDistance = entry.getEntryWithDistanceOne(data);
        for (Entry entryOfList : listEntryOneDistance) {
            cluster.addEntry(entryOfList);
            data.remove(entryOfList);
            addManyEntries(entryOfList, cluster);
        }
    }
}
