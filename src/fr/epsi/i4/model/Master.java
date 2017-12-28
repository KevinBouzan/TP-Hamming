package fr.epsi.i4.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Classe qui représente l'ensemble des clusters et données
 *
 * @author kbouzan
 */
public class Master {

    /**
     * Liste de cluster
     */
    private List<Cluster> clusters;

    private List<Cluster> clustersTemp;

    private int[][] distances;

    /**
     * Liste de données utilisé pour remplir les clusters
     */
    private List<Entry> data;

    public Master() {
        clusters = new ArrayList<>();
        data = new ArrayList<>();
        clustersTemp = new ArrayList<>();
    }

    public void generateDistancesMatrix() {
        distances = new int[data.size()][data.size()];
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.size(); j++) {
                distances[i][j] = data.get(i).calculateDistance(data.get(j));
            }
        }
        generateClusterTemp();
    }

    public void generateClusterTemp() {
        for (int i = 0; i < data.size(); i++) {
            clustersTemp.add(new Cluster());
            for (int j = 0; j < data.size(); j++) {
                if (distances[i][j] < 2) {
                    clustersTemp.get(clustersTemp.size() - 1).addEntry(data.get(j));
                }
            }
        }
        System.out.println(printTemp());
    }

    public String printTemp() {
        StringBuilder stringBuilder = new StringBuilder("Master: ");
        for (int i = 0; i < clustersTemp.size(); i++) {
            stringBuilder
                    .append("\n---------------------------------")
                    .append("\nCluster ")
                    .append(i + 1);
            for (Entry entry : clustersTemp.get(i).getData()) {
                stringBuilder
                        .append("\n")
                        .append(entry.toString());
            }
        }
        return stringBuilder.toString();
    }

    public boolean preselect(Cluster cluster) {
        int count;
        boolean res = false;
        int i = 0;
        Cluster clusterTemp;
        while (i < clustersTemp.size()) {
            clusterTemp = clustersTemp.get(i);
            if (clusterTemp != cluster) {
                count = 0;
                for (Entry entry : cluster.getData()) {
                    if (clusterTemp.getData().contains(entry)) {
                        count++;
                    }
                }
                if (count == cluster.getData().size()) {
                    clustersTemp.remove(cluster);
                    res = true;
                    i = clustersTemp.size();
                }
            }
            i++;
        }
        return res;
    }

    public void printDistance() {
        for (int i = 0; i < data.size(); i++) {
            if (i == 0) {
                System.out.print("    ");
                for (int j = 0; j < data.size(); j++) {
                    System.out.print(j + 1 + " | ");
                }
            }
            System.out.println("");
            System.out.print(i + 1 + " | ");
            for (int j = 0; j < data.size(); j++) {
                System.out.print(distances[i][j] + " | ");
            }
        }
    }

    public List<Cluster> getClusters() {
        return clusters;
    }

    public List<Cluster> getClustersTemp() {
        return clustersTemp;
    }

    public void setClustersTemp(List<Cluster> clustersTemp) {
        this.clustersTemp = clustersTemp;
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

    /**
     * Retourne le premier cluster vide
     *
     * @return premier cluster vide sinon le premier cluster de la liste
     */
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

    /**
     * Repartition des données au sein des n clusters
     *
     * @param n
     */
    public void dispatch(int n) {
        for (int i = 0; i < n; i++) {
            clusters.add(new Cluster());
        }
        int i;
        boolean trouve;
        Entry entryResult;
        Entry entry;

        // compteur pour éviter les boucle infini
        int count = 0;

        // max que le compteur peut atteindre
        int max = data.size();
        Cluster cluster = null;

        // on boucle tant qu'il y a des données dans la liste data
        while (!data.isEmpty()) {
            i = 0;
            trouve = false;
            entry = getFirstEntry();
            cluster = getFirstEmptyCluster();
            // Si on récupère un cluster vide alors on ajoute la données
            if (cluster.isEmpty()) {
                cluster.addEntry(entry);
                data.remove(entry);
//                for (Entry entryOne : getEntryDistanceOne(entry)) {
//                    cluster.addEntry(entryOne);
//                    data.remove(entryOne);
//                }
            } else {
                // on boucle pour tous les clusters
                while (i < clusters.size() && !trouve) {
                    // On vérifie si la donnée doit être inséré dans le cluster
                    entryResult = entry.checkDistanceWithClusters(clusters.get(i), clusters);
                    // Si le resultat est egal à l'entry alors c'est le bon cluster.
                    // On ajoute l'entry dans le cluster et on le supprime de la liste data
                    if (entryResult == entry) {
                        clusters.get(i).addEntry(entry);
                        data.remove(entry);
                        trouve = true;
                    }
                    i++;
                }
                // Si aucune place n'a été trouvé
                if (!trouve) {
                    // Si c'est le dernier élément de la liste c'est donc qu'il peut être inséré dans n'importe quel cluster. Alors on l'insère dans le premier
                    // Si ce n'est pas le dernier on récupère l'entry qui l'empeche d'être inséreé et on la remet dans la liste des data
                    if (data.size() > 1) {
                        entryResult = entry.getEntryToSwitch(clusters);
                        data.add(entryResult);
                        count++;
                    } else {
                        entry.clusterWithMin(clusters).addEntry(data.get(0));
                        System.out.println("La données " + data.get(0).toString() + " peut s'inserer dans n'importe quel cluster");
//                        getFirstEmptyCluster().addEntry(data.get(0));
                        data.remove(0);
                    }
                }
                // Quand le compteur atteint le max alors on insère la valeur dans le premier cluster. 
                if (count == max) {
                    System.out.println("La données " + data.get(data.size() - 1).toString() + "  ne peut pas être inséré dans un des " + n + " clusters. Elle est donc inséré dans le premier cluster");
                    getFirstEmptyCluster().addEntry(data.get(data.size() - 1));
                    data.remove(data.size() - 1);
                    count = 0;
                }
            }
        }
    }

    /**
     * Repartition des données au sein des clusters. Il crée un nouveau cluster
     * lorsque c'est necessaire
     */
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
        int max = data.size();
        Cluster cluster = null;

        while (!data.isEmpty()) {
            i = 0;
            dist = 0;
            trouve = false;
            entry = getFirstEntry();
            cluster = getFirstEmptyCluster();
            if (cluster.isEmpty()) {
                cluster.addEntry(entry);
                data.remove(entry);
                for (Entry entryOne : getEntryDistanceOne(entry)) {
                    cluster.addEntry(entryOne);
                    data.remove(entryOne);
                }
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
                        data.add(entryMax);
                        count++;
                    } else {
                        clusters.add(new Cluster());
                    }
                }
                // Si le compteur atteint son max on crée un nouveau cluster
                if (count == max) {
                    clusters.add(new Cluster());
                    count = 0;
                }
            }
        }
    }

    /**
     * Retourne le premier élément de la liste de données
     *
     * @return premier Entry de data
     */
    public Entry getFirstEntry() {
        Entry result = null;
        if (!data.isEmpty()) {
            result = data.get(0);
        }
        return result;
    }

    public List<Entry> getEntryDistanceOne(Entry entry) {
        List<Entry> listEntry = new ArrayList<>();
        for (Entry entryOfList : data) {
            if (entry.calculateDistance(entryOfList) < 2) {
                listEntry.add(entryOfList);
            }
        }
        return listEntry;
    }

    public ClusterDistance getMinofMax(Cluster cluster) {
        int maxTemp, distance;
        int max = 1000000000;
        Cluster clusterToMerge = null;
        Cluster clusterToMergeTemp = null;
        for (Cluster clusterTemp : clustersTemp) {
            if (cluster != clusterTemp) {
                for (Entry entry : cluster.getData()) {
                    distance = entry.getMaximumDistanceWithCluster(clusterTemp).getDistance();
                    if (distance < max && entry != entry.getMaximumDistanceWithCluster(clusterTemp).getEntry()) {
                        max = distance;
                        clusterToMerge = clusterTemp;
                    }
                }
            }
        }
        ClusterDistance res = new ClusterDistance(clusterToMerge, max);
        return res;

    }

    public void merge() {
        ClusterDistance clusterDistance = new ClusterDistance(null, 10000);
        ClusterDistance clusterDistanceTemp = null;
        Cluster clusterToMerge = null;
        Cluster clusterMerge = null;
        for (Cluster cluster : clustersTemp) {
            clusterDistanceTemp = getMinofMax(cluster);
            if (clusterDistance.getDistance() > clusterDistanceTemp.getDistance()) {
                clusterToMerge = clusterDistanceTemp.getCluster();
                clusterMerge = cluster;
                clusterDistance.setDistance(clusterDistanceTemp.getDistance());
            }
        }
        for (Entry entry : clusterToMerge.getData()) {
            if (!clusterMerge.getData().contains(entry)) {
                clusterMerge.addEntry(entry);
            }
        }
        clustersTemp.remove(clusterToMerge);

        System.out.println("merge between " + clusterToMerge.toString() + " and " + clusterMerge.toString());
    }

    public void cleanCluster() {
        List<Cluster> clusterList;
        int max = 0;
        Cluster goodCluster;
        for (Entry entry : data) {
            clusterList = new ArrayList<>();
            goodCluster = null;
            for (Cluster cluster : clustersTemp) {
                if (cluster.getData().contains(entry)) {
                    clusterList.add(cluster);
                }
            }
            if (clusterList.size() > 1) {
                for (Cluster clusterOfList : clusterList) {
                    if (max < entry.getMaximumDistanceWithCluster(clusterOfList).getDistance()) {
                        if (goodCluster != null) {
                            goodCluster.getData().remove(entry);
                        }
                        goodCluster = clusterOfList;
                    } else {
                        clusterOfList.getData().remove(entry);
                    }
                }
            }
        }
    }
    
    public void cleanCluster2() {
        List<Cluster> clusterList;
        int max = 100000;
        Cluster goodCluster;
        for (Entry entry : data) {
            max = 100000;
            clusterList = new ArrayList<>();
            goodCluster = null;
            for (Cluster cluster : clustersTemp) {
                if (cluster.getData().contains(entry)) {
                    clusterList.add(cluster);
                }
            }
            if (clusterList.size() > 1) {
                for (Cluster clusterOfList : clusterList) {
                    if (max > entry.getMaximumDistanceWithCluster(clusterOfList).getDistance()) {
                        if (goodCluster != null) {
                            goodCluster.getData().remove(entry);
                        }
                        max = entry.getMaximumDistanceWithCluster(clusterOfList).getDistance();
                        goodCluster = clusterOfList;
                    } else {
                        clusterOfList.getData().remove(entry);
                    }
                }
            }
        }
    }
    
    public void dispatchOpti(int n){
        while(clustersTemp.size() > n){
            merge();
            System.out.println("----------- Merge -----------");
            System.out.println(printTemp());
        }
//        System.out.println(printTemp());
        cleanCluster2();
    }
}
