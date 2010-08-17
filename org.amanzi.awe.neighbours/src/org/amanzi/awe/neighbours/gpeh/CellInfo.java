/*
 * 
 */
package org.amanzi.awe.neighbours.gpeh;

import org.neo4j.graphdb.Node;

/**
 * <p>
 * Information about best cell and interference cell
 * </p>
 * 
 * @author tsinkel_a
 * @since 1.0.0
 */
public class CellInfo {
    private Integer ci;
    private Integer rnc;
    private String psc;
    private Node bestCellInfo;
    private Node interfCellInfo;

    /**
     * Instantiates a new cell info.
     * 
     * @param ci the ci
     * @param rnc the rnc
     * @param psc the psc
     * @param bestCellInfo the best cell info
     * @param interfCellInfo the interf cell info
     */
    public CellInfo(Integer ci, Integer rnc, String psc, Node bestCellInfo, Node interfCellInfo) {
        super();
        this.ci = ci;
        this.rnc = rnc;
        this.psc = psc;
        this.bestCellInfo = bestCellInfo;
        this.interfCellInfo = interfCellInfo;
    }

    /**
     * Gets the best cell info.
     *
     * @return the best cell info
     */
    public Node getBestCellInfo() {
        return bestCellInfo;
    }

    /**
     * Gets the interf cell info.
     *
     * @return the interf cell info
     */
    public Node getInterfCellInfo() {
        return interfCellInfo;
    }

    /**
     * Gets the ci.
     *
     * @return the ci
     */
    public Integer getCi() {
        return ci;
    }

    /**
     * Sets the ci.
     *
     * @param ci the new ci
     */
    public void setCi(Integer ci) {
        this.ci = ci;
    }

    /**
     * Gets the rnc.
     *
     * @return the rnc
     */
    public Integer getRnc() {
        return rnc;
    }

    /**
     * Sets the rnc.
     *
     * @param rnc the new rnc
     */
    public void setRnc(Integer rnc) {
        this.rnc = rnc;
    }

    /**
     * Gets the psc.
     *
     * @return the psc
     */
    public String getPsc() {
        return psc;
    }

    /**
     * Sets the psc.
     *
     * @param psc the new psc
     */
    public void setPsc(String psc) {
        this.psc = psc;
    }

}