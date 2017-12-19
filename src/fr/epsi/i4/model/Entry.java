package fr.epsi.i4.model;

import java.lang.reflect.Field;
import java.util.List;

public class Entry {

    private int id;
    private static int nextId = 1;

    public int couleur;
    public int noyaux;
    public int flagelles;
    public int membrane;

    public Entry() {
        id = nextId;
        nextId++;
    }

    public Entry(int couleur, int noyaux, int flagelles, int membrane) {
        this();
        this.couleur = couleur;
        this.noyaux = noyaux;
        this.flagelles = flagelles;
        this.membrane = membrane;
    }

    public int getId() {
        return id;
    }

    public int getCouleur() {
        return couleur;
    }

    public void setCouleur(int couleur) {
        this.couleur = couleur;
    }

    public int getNoyaux() {
        return noyaux;
    }

    public void setNoyaux(int noyaux) {
        this.noyaux = noyaux;
    }

    public int getFlagelles() {
        return flagelles;
    }

    public void setFlagelles(int flagelles) {
        this.flagelles = flagelles;
    }

    public int getMembrane() {
        return membrane;
    }

    public void setMembrane(int membrane) {
        this.membrane = membrane;
    }

    @Override
    public String toString() {
        return "Entry{"
                + "id=" + id
                + ", couleur=" + couleur
                + ", noyaux=" + noyaux
                + ", flagelles=" + flagelles
                + ", membrane=" + membrane
                + '}';
    }

    public int calculateDistance(Entry entry) {
        int distance = 0;

        try {
            for (Field field : getClass().getFields()) {
                if (!field.get(this).equals(field.get(entry))) {
                    distance++;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return distance;
    }

    public DistanceEntry getMaximumDistanceWithCluster(Cluster cluster) {
        int distance = 0;
        int newDistance = 0;
        Entry maxEntry = null;
        for (Entry entry : cluster.getData()) {
            newDistance = Math.max(distance, calculateDistance(entry));
            if (newDistance > distance) {
                distance = newDistance;
                maxEntry = entry;
            }
        }

        return new DistanceEntry(distance, maxEntry);
    }

    public DistanceEntry getMinimumDistanceWithCluster(Cluster cluster) {
        int distance = 1000000;
        int newDistance = 0;
        Entry minEntry = null;
        for (Entry entry : cluster.getData()) {
            newDistance = Math.min(distance, calculateDistance(entry));
            if (newDistance < distance) {
                distance = newDistance;
                minEntry = entry;
            }
        }

        return new DistanceEntry(distance, minEntry);
    }

    public Entry checkDistanceWithClusters(Cluster cluster, List<Cluster> clusters) {
        int distanceMin = 1000000;

        for (Cluster clusterOfList : clusters) {
            if (clusterOfList != cluster) {
                for (Entry entry : clusterOfList.getData()) {
                    distanceMin = Math.min(distanceMin, calculateDistance(entry));
                }
            }
        }

        DistanceEntry distanceMax = getMaximumDistanceWithCluster(cluster);

        if (distanceMin >= distanceMax.getDistance()) {
            return this;
        } else {
            return distanceMax.getEntry();
        }
    }
}
