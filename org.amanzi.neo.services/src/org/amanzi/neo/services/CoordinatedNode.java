package org.amanzi.neo.services;

import org.neo4j.graphdb.Node;

import com.vividsolutions.jts.geom.Coordinate;

public class CoordinatedNode {
private Node node;
private Coordinate coord;
public Node getNode() {
    return node;
}
public void setNode(Node node) {
    this.node = node;
}
public Coordinate getCoord() {
    return coord;
}
public void setCoord(Coordinate coord) {
    this.coord = coord;
}
public CoordinatedNode(){
    super();
}
public CoordinatedNode(Node node, Coordinate coord) {
    this();
    this.node = node;
    this.coord = coord;
}

}
