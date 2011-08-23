/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.awe.views.reuse.views;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.network.FrequencyPlanModel;
import org.amanzi.neo.services.statistic.IPropertyInformation;
import org.amanzi.neo.services.statistic.ISelectionInformation;
import org.amanzi.neo.services.statistic.IStatistic;
import org.hsqldb.lib.StringUtil;
import org.neo4j.graphdb.Node;

/**
 * <p>
 * Model for frequency plan analyse
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class FreqPlanSelectionInformation implements ISelectionInformation {

    private Set<String> propertySet;
    private String descr;
    private final Node networkNode;
    private final FrequencyPlanModel model;
    private final IStatistic statistic;
    private String sectorName;
    private String ncc;
    private String bcc;
    private TRXTYPE trxType;


    /**
     * Instantiates a new freq plan selection information.
     *
     * @param statistic the statistic
     * @param networkNode the network node
     * @param model the model
     */
    public FreqPlanSelectionInformation(IStatistic statistic, Node networkNode, FrequencyPlanModel model) {
        this.statistic = statistic;
        this.networkNode = networkNode;
        this.model = model;
        propertySet = new HashSet<String>();
        Collection<String> col = statistic.getPropertyNameCollection(model.getName(), NodeTypes.FREQUENCY_PLAN.getId(),
                new Comparable<Class<?>>() {

                    @Override
                    public int compareTo(Class<?> o) {
                        return Comparable.class.isAssignableFrom(o) ? 0 : -1;
                    }
                });
        propertySet.addAll(col);
        DatasetService ds = NeoServiceFactory.getInstance().getDatasetService();

        descr = String.format("Network %s plan %s", ds.getNodeName(networkNode), model.getName());
    }

    public String getSectorName() {
        return StringUtil.isEmpty(sectorName) ? null : sectorName;
    }

    public IStatistic getStatistic() {
        return statistic;
    }

    public void setSectorName(String sectorName) {
        this.sectorName = sectorName;
    }

    public String getNcc() {
        return StringUtil.isEmpty(ncc) ? null : ncc;
    }

    public void setNcc(String ncc) {
        this.ncc = ncc;
    }

    public String getBcc() {
        return StringUtil.isEmpty(bcc) ? null : bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public TRXTYPE getTrxType() {
        return trxType;
    }

    public void setTrxType(TRXTYPE trxType) {
        this.trxType = trxType;
    }

    @Override
    public String getDescription() {
        return descr;
    }

    @Override
    public Set<String> getPropertySet() {
        return propertySet;
    }

    @Override
    public IPropertyInformation getPropertyInformation(String propertyName) {
        return new FrPlanPropertyInf(statistic, networkNode, model, propertyName, getSectorName(), getNcc(), getBcc(), getTrxType());
    }

    @Override
    public boolean isAggregated() {
        return false;
    }

    @Override
    public Node getRootNode() {
        return networkNode;
    }

    public enum TRXTYPE {
        ALL, BCCH, TCH
    }

    /**
     * @param text
     */
    public void setTrxType(String text) {
        try {
            trxType = TRXTYPE.valueOf(text);
        } catch (Exception e) {
            trxType = null;
        }
    }

    @Override
    public String getFullDescription() {
        return String.format("descr %s sector %s ncc %s bcc %s carrier type %s", getDescription(), getSectorName(), getNcc(), getBcc(), getTrxType());
    }
}
