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
    
    public void StringToObject(String readLine){
        String[] idRead = readLine.split("id=");
        idRead = idRead[1].split(",");
        id = Integer.valueOf(idRead[0]);
        
        String[] paramsRead = readLine.split("params=\\[");
        String[] atts = paramsRead[1].split(", ");
        for (int i = 0; i < atts.length - 1; i++){
            params.add(Integer.valueOf(atts[i]));
        }
        String test = atts[atts.length - 1].split("\\]")[0];
        params.add(Integer.valueOf(test));
    }
}
