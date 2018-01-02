package fr.epsi.i4.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

    /**
     * Matrice des distances
     */
    private int[][] distances;

    /**
     * Liste de données utilisé pour remplir les clusters
     */
    private List<Entry> data;

    public Master() {
        data = new ArrayList<>();
        clusters = new ArrayList<>();
    }

    /**
     * Génère la matrice des distances
     */
    public void generateDistancesMatrix() {
        distances = new int[data.size()][data.size()];
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.size(); j++) {
                distances[i][j] = data.get(i).calculateDistance(data.get(j));
            }
        }
    }

    /**
     * Génère les premier cluster qui seront ensuite fusionné. Chaque cluster
     * est composé d'un élément et de tout les éléments qui sont à une distance
     * inferieure à 2
     */
    public void generateClusterTemp() {
        for (int i = 0; i < data.size(); i++) {
            clusters.add(new Cluster());
            for (int j = 0; j < data.size(); j++) {
                if (distances[i][j] < 2) {
                    clusters.get(clusters.size() - 1).addEntry(data.get(j));
                }
            }
        }
    }

    /**
     * Affiche les cluster
     *
     * @return clusters
     */
    public String print() {
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

    /**
     * Effectue un nettoyage du cluster. Si celui ci est entierement compris
     * dans un autre alors il est supprimé
     *
     * @param cluster
     * @return boolean supprimé
     */
    public boolean preselect(Cluster cluster) {
        int count;
        boolean res = false;
        int i = 0;
        Cluster clusterTemp;
        while (i < clusters.size()) {
            clusterTemp = clusters.get(i);
            if (clusterTemp != cluster) {
                count = 0;
                for (Entry entry : cluster.getData()) {
                    if (clusterTemp.getData().contains(entry)) {
                        count++;
                    }
                }
                if (count == cluster.getData().size()) {
                    clusters.remove(cluster);
                    res = true;
                    i = clusters.size();
                }
            }
            i++;
        }
        return res;
    }

    /**
     * Affiche la matrice de distances
     */
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
     * Récupère le cluster qui possède la distance maximale la plus proche du
     * cluster passé en paramètre
     *
     * @param cluster
     * @return cluster et distance max
     */
    public ClusterDistance getMinofMax(Cluster cluster) {
        int distance;
        int max = 1000000000;
        Cluster clusterToMerge = null;
        for (Cluster clusterTemp : clusters) {
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

    /**
     * Merge les deux clusters les plus proche grâce à la fonction getMinOfMax.
     * Effectue le merge uniquement pour les deux plus proche. Puis supprime le
     * cluster merger
     */
    public void merge() {
        ClusterDistance clusterDistance = new ClusterDistance(null, 10000);
        ClusterDistance clusterDistanceTemp = null;
        Cluster clusterToMerge = null;
        Cluster clusterMerge = null;
        for (Cluster cluster : clusters) {
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
        clusters.remove(clusterToMerge);
    }

    /**
     * Supprime les entry en doublons dans les cluster restant en verifiant de
     * garder l'entry dans le cluster ou sa distance maximale est la plus petite
     * possible
     */
    public void cleanCluster() {
        List<Cluster> clusterList;
        int max = 100000;
        Cluster goodCluster;
        int distance;
        for (Entry entry : data) {
            max = 100000;
            clusterList = new ArrayList<>();
            goodCluster = null;
            for (Cluster cluster : clusters) {
                if (cluster.getData().contains(entry)) {
                    clusterList.add(cluster);
                }
            }
            if (clusterList.size() > 1) {
                for (Cluster clusterOfList : clusterList) {
                    distance = entry.getMaximumDistanceWithCluster(clusterOfList).getDistance();
                    if (max >= distance) {
                        if (goodCluster != null) {
                            goodCluster.getData().remove(entry);
                        }
                        max = distance;
                        goodCluster = clusterOfList;
                    } else {
                        clusterOfList.getData().remove(entry);
                    }
                }
            }
        }
    }

    /**
     * Range les éléments dans les cluster. Génère n clusters
     */
    public long dispatch() {
        Scanner input = new Scanner(System.in);
        System.out.println("Combien de cluster souhaitez vous ?");
        String choice = input.next();
        long time = System.currentTimeMillis();
        try {
            int n = Integer.valueOf(choice);

            generateClusterTemp();
//            int i = 0;
//            while (i < clusters.size()) {
//                if (!preselect(clusters.get(i))) {
//                    i++;
//                }
//            }
            while (clusters.size() > n) {
                merge();
            }
            cleanCluster();
        } catch (NumberFormatException e) {
            System.out.println("Saisir un nombre !");
            dispatch();
        }
        return System.currentTimeMillis() - time;
    }

    public void readFile() {
        List<Entry> dataTemp = data;
        try {
            File f = new File("data.txt");
            BufferedReader b = new BufferedReader(new FileReader(f));
            String readLine = "";
            data = new ArrayList<>();
            while ((readLine = b.readLine()) != null) {
                Entry entry = new Entry();
                entry.StringToObject(readLine);
                this.data.add(entry);
            }

        } catch (IOException e) {
            data = dataTemp;
            System.out.println("Aucune données");
        }
    }
}
