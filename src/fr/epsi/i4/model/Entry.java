package fr.epsi.i4.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe Entry qui represente une ligne de données
 * @author kbouzan
 */
public class Entry {

    /**
     * id de la ligne de données
     */
    private int id;
    
    /**
     * nextId static afin d'auto-increment l'id
     */
    private static int nextId = 1;

    private List<Integer> params;
    
    public Entry() {
        id = nextId;
        nextId++;
        this.params = new ArrayList<>();
    }
    
    public Entry(int... values){
        this();
        this.params = new ArrayList<>();
        for (int v : values) {
            this.params.add(v);
        }
    }

    public int getId() {
        return id;
    }

    public List<Integer> getParams() {
        return params;
    }

    public void setParams(List<Integer> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "Entry{" + "id=" + id + ", params=" + params.toString() + '}';
    }

    

    /**
     * Calcule la distance entre l'entry actuelle et l'entry passé en paramètre
     * @param entry
     * @return distance
     */
    public int calculateDistance(Entry entry) {
        int distance = 0;
        int i = 0;

        while(i < entry.getParams().size()){
            if(this.getParams().get(i) != entry.getParams().get(i)){
                distance++;
            }
            i++;
        }

        return distance;
    }

    /**
     * Calcule la distace maximale avec le cluster passer en paramètre
     * @param cluster
     * @return la distance maximale et l'entry qui correspond à cette distance 
     */
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

    /**
     * Calcule la distace minimale avec le cluster passer en paramètre
     * @param cluster
     * @return la distance minimale et l'entry qui correspond à cette distance 
     */
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

    /**
     * Verifie si l'entry doit être inseré dans le cluster passé en paramètre
     * @param cluster
     * @param clusters
     * @return l'entry si ok sinon null
     */
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
            return null;
        }
    }

    /**
     * recupère l'entry qui gêne pour l'insertion de la données dans un cluster.
     * Pour cela on calcule la moyenne ainsi que l'ecart entre le max et le min afin de choisir le bon élément à retirer du cluster
     * @param clusters
     * @return 
     */
    public Entry getEntryToSwitch(List<Cluster> clusters) {
        float moy = 0;
        float ecart = 0;
        DistanceEntry resMin = null;
        DistanceEntry resMax = null;
        Entry res = null;
        Cluster clusterDataRemove = null;
        for (Cluster clusterOfList : clusters) {
            resMin = getMinimumDistanceWithCluster(clusterOfList);
            resMax = getMaximumDistanceWithCluster(clusterOfList);
            moy = 0;
            //Calcul de la moyenne des distance dans le cluster
            for (Entry entry : clusterOfList.getData()) {
                moy += calculateDistance(entry);
            }
            moy /= clusterOfList.getData().size();
            
            // Avec la moyenne on peut alors savoir si c'est le min ou le max qui pose problème
            // Enfini il faut verifier que l'ecart calculer est supérieur au precedent afin de choisir l'entry qui pose le plus de problème à l'insertion de la donnée
            if ((resMax.getDistance() - moy) > Math.abs((resMin.getDistance() - moy))) {
                if ((resMax.getDistance() - moy) > ecart){
                    res = resMax.getEntry();
                    clusterDataRemove = clusterOfList;
                    ecart = resMax.getDistance() - moy;
                } 
            } else {
                if (Math.abs((resMin.getDistance() - moy)) > ecart){
                    res = resMin.getEntry();
                    clusterDataRemove = clusterOfList;
                    ecart = Math.abs((resMin.getDistance() - moy));
                }
            }
        }
        
        // On supprime l'entry génante du cluster puis on la retourne pour qu'elle soit traiter à nouveau
        clusterDataRemove.getData().remove(res);
        return res;
    }
    
    /**
     * Retourne le cluster qui a une distance minimum avec l'entry
     * @param clusters
     * @return cluster minimum distance
     */
    public Cluster clusterWithMin(List<Cluster> clusters){
        Cluster res = clusters.get(0);
        int min = 1000;
        int distance = 0;
        for (Cluster cluster : clusters){
            distance = getMinimumDistanceWithCluster(cluster).getDistance();
            if (distance < min){
                res = cluster;
                min = distance;
            }
        }
        return res;
    }
}
