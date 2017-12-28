/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.epsi.i4.model;

/**
 *
 * @author kbouzan
 */
public class ClusterDistance {
    
    private Cluster cluster;
    
    private int distance;

    public ClusterDistance(Cluster cluster, int distance) {
        this.cluster = cluster;
        this.distance = distance;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
    
    
    
}
