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

import java.util.ArrayList;

import org.amanzi.awe.views.reuse.views.FreqPlanSelectionInformation.TRXTYPE;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.NetworkService;
import org.amanzi.neo.services.enums.DatasetRelationshipTypes;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.network.FrequencyPlanModel;
import org.amanzi.neo.services.statistic.IPropertyInformation;
import org.amanzi.neo.services.statistic.ISinglePropertyStat;
import org.amanzi.neo.services.statistic.ISource;
import org.amanzi.neo.services.statistic.IStatistic;
import org.amanzi.neo.services.utils.AggregateRules;
import org.apache.commons.lang.ObjectUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class FrPlanPropertyInf implements IPropertyInformation {

    private final IStatistic statistic;
    private final Node networkNode;
    private final FrequencyPlanModel model;

    private ISinglePropertyStat propStat;
    private final String propertyName;
    private final String sector;
    private final String ncc;
    private final String bcc;
    private final TRXTYPE trxtype;

    public FrPlanPropertyInf(IStatistic statistic, Node networkNode, FrequencyPlanModel model, String propertyName, String sector, String ncc, String bcc, TRXTYPE trxtype) {
        this.statistic = statistic;
        this.networkNode = networkNode;
        this.model = model;
        this.propertyName = propertyName;
        this.sector = sector;
        this.ncc = ncc;
        this.bcc = bcc;
        this.trxtype = trxtype;

        propStat = statistic.findPropertyStatistic(model.getName(), NodeTypes.FREQUENCY_PLAN.getId(), propertyName);
    }

    @Override
    public ISinglePropertyStat getStatistic() {
        return propStat;
    }

    @Override
    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public Iterable<ISource> getValueIterable(AggregateRules rules) {
        final NetworkService ns = NeoServiceFactory.getInstance().getNetworkService();
        final DatasetService ds = NeoServiceFactory.getInstance().getDatasetService();
        Node rootNode = networkNode;
        if (sector != null) {
            rootNode = ns.findSector(rootNode, sector, true);
        }
        if (rootNode == null) {
            return new ArrayList<ISource>();
        }
        Traverser traverser = Traversal.description().depthFirst().uniqueness(Uniqueness.NONE).relationships(GeoNeoRelationshipTypes.CHILD, Direction.OUTGOING)
                .relationships(DatasetRelationshipTypes.PLAN_ENTRY, Direction.OUTGOING).evaluator(new Evaluator() {

                    @Override
                    public Evaluation evaluate(Path paramPath) {
                        Node node = paramPath.endNode();
                        if (bcc != null || ncc != null) {
                            if (NodeTypes.SECTOR.checkNode(node)) {
                                String bcc1 = (String)node.getProperty("bcc", null);
                                String ncc1 = (String)node.getProperty("ncc", null);
                                return Evaluation.of(false, ObjectUtils.equals(bcc, bcc1) && ObjectUtils.equals(ncc, ncc1));
                            }
                        }
                        if (trxtype != null && trxtype != TRXTYPE.ALL) {
                            if (NodeTypes.TRX.checkNode(node)) {
                                TRXTYPE type = (Boolean)node.getProperty("bcch", false) ? TRXTYPE.BCCH : TRXTYPE.TCH;
                                return Evaluation.of(false, ObjectUtils.equals(trxtype, type));
                            }
                        }
                        if (NodeTypes.FREQUENCY_PLAN.checkNode(node)) {
                            boolean incl = node.hasProperty(propertyName);
                            if (incl) {
                                String modelName;
                                if (node.hasRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING)) {
                                    modelName = ds.getNodeName(node.getSingleRelationship(GeoNeoRelationshipTypes.CHILD, Direction.INCOMING).getOtherNode(node));
                                } else {
                                    modelName = ds.getNodeName(node);
                                }
                                return Evaluation.of(ObjectUtils.equals(modelName, model.getName()), false);
                            } else {
                                return Evaluation.EXCLUDE_AND_PRUNE;
                            }
                        }
                        return Evaluation.EXCLUDE_AND_CONTINUE;
                    }
                }).traverse(rootNode);
        return new SourceExistIterable(traverser, propertyName);
    }

}
