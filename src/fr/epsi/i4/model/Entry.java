package fr.epsi.i4.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
//            return distanceMax.getEntry();
            return null;
        }
    }

    public List<Entry> getEntryWithDistanceOne(List<Entry> data) {
        List<Entry> result = new ArrayList<>();
        for (Entry entry : data) {
            if (this.calculateDistance(entry) < 2) {
                result.add(entry);
            }
        }
        return result;
    }

    public Entry checkIfCloseTo(List<Cluster> clusters) {
        Entry result = null;
        DistanceEntry minDistance = null;
        Cluster clusterDataRemove = null;
        int count = 0;
        for (Cluster clusterOfList : clusters) {
            minDistance = getMinimumDistanceWithCluster(clusterOfList);
            if (minDistance.getDistance() < 2) {
                clusterDataRemove = clusterOfList;
                result = minDistance.getEntry();
                if (clusterDataRemove != clusterOfList) {
                    count++;
                }
            }
        }
        if (result != null && count < 2) {
            clusterDataRemove.getData().add(this);
            return this;
        } else {
            return null;
        }
    }

//    public Entry getEntryToSwitch(List<Cluster> clusters) {
//        int min = 0;
//        int minOfMax = 10000;
//        Entry result = null;
//        DistanceEntry resMin;
//        DistanceEntry resMax;
//        Cluster clusterDataRemove = null;
//        for (Cluster clusterOfList : clusters) {
//            resMin = getMinimumDistanceWithCluster(clusterOfList);
//            resMax = getMaximumDistanceWithCluster(clusterOfList);
//            if (Math.abs(resMin.getDistance() - resMax.getDistance()) >= min && resMax.getDistance() < minOfMax) {
//                min = Math.abs(resMin.getDistance() - resMax.getDistance());
//                minOfMax = resMax.getDistance();
//                result = resMax.getEntry();
//                clusterDataRemove = clusterOfList;
//            }
//        }
//        clusterDataRemove.getData().remove(result);
//        return result;
//    }
    public Entry getEntryToSwitch(List<Cluster> clusters) {
        Entry result = null;
        float moy = 0;
        int ecart = 0;
        int ecartTemp = 0;
        DistanceEntry resMin = null;
        DistanceEntry resMax = null;
        Cluster clusterDataRemove = null;
        for (Cluster clusterOfList : clusters) {
            ecartTemp = getMaximumDistanceWithCluster(clusterOfList).getDistance() - getMinimumDistanceWithCluster(clusterOfList).getDistance();
            if (ecartTemp >= ecart) {
                resMin = getMinimumDistanceWithCluster(clusterOfList);
                resMax = getMaximumDistanceWithCluster(clusterOfList);
                clusterDataRemove = clusterOfList;
                ecart = ecartTemp;
                moy = 0;
                for (Entry entry : clusterOfList.getData()) {
                    moy += calculateDistance(entry);
                }
                moy /= clusterOfList.getData().size();
            }
        }
        if((resMax.getDistance() - Math.round(moy)) > (resMin.getDistance() - Math.round(moy))){
            clusterDataRemove.getData().remove(resMax.getEntry());
            return resMax.getEntry();
        } else {
            clusterDataRemove.getData().remove(resMin.getEntry());
            return resMin.getEntry();
        }
    }
}
