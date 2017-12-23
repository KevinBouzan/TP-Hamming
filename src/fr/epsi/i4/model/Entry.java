package fr.epsi.i4.model;

import java.lang.reflect.Field;
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

    /**
     * Calcule la distance entre l'entry actuelle et l'entry passé en paramètre
     * @param entry
     * @return distance
     */
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
        int ecart = 0;
        int ecartTemp = 0;
        DistanceEntry resMin = null;
        DistanceEntry resMax = null;
        Cluster clusterDataRemove = null;
        for (Cluster clusterOfList : clusters) {
            
            // Calcul de l'ecart type
            // S'il est plus grand que celui enregistré on calcule sa moyenne
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
        
        // afin de savoir si c'est le maximum ou le minimum qui pose problème on soustrait la moyenne à ces deux valeur. La plus grande valeur est donc celle la plus eloigné de la moyenne et donc celle su'il faut retirer
        // la moyenne est arrondi puisqu'il ne peut pas y avoir un demi attribut de difference.
        if((resMax.getDistance() - Math.round(moy)) > (resMin.getDistance() - Math.round(moy))){
            clusterDataRemove.getData().remove(resMax.getEntry());
            return resMax.getEntry();
        } else {
            clusterDataRemove.getData().remove(resMin.getEntry());
            return resMin.getEntry();
        }
    }
}
