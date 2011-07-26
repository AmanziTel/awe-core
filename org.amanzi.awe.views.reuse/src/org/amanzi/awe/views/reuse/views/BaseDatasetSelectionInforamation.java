package org.amanzi.awe.views.reuse.views;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.amanzi.awe.views.reuse.views.BaseNetworkSelectionInformation.BaseNetworkInformationImpl;
import org.amanzi.neo.services.DatasetService;
import org.amanzi.neo.services.NeoServiceFactory;
import org.amanzi.neo.services.enums.GeoNeoRelationshipTypes;
import org.amanzi.neo.services.enums.NodeTypes;
import org.amanzi.neo.services.statistic.IPropertyInformation;
import org.amanzi.neo.services.statistic.ISelectionInformation;
import org.amanzi.neo.services.statistic.ISinglePropertyStat;
import org.amanzi.neo.services.statistic.ISource;
import org.amanzi.neo.services.statistic.IStatistic;
import org.amanzi.neo.services.utils.AggregateRules;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;

public class BaseDatasetSelectionInforamation implements ISelectionInformation {

	private final String name;
	private final String nodeType;
	private Map<String, IPropertyInformation> propertyMap = new HashMap<String, IPropertyInformation>();
	private boolean isAggregated = false;
	private final Node root;

	/**
	 * @param statistic
	 * @param name
	 * @param nodeType
	 */
	public BaseDatasetSelectionInforamation(Node root, IStatistic statistic,
			String name, String nodeType) {
		this.root = root;
		this.name = name;
		this.nodeType = nodeType;
		Collection<String> col = statistic.getPropertyNameCollection(name,
				nodeType, new Comparable<Class>() {

					@Override
					public int compareTo(Class o) {
						return Comparable.class.isAssignableFrom(o) ? 0 : -1;
					}
				});
		for (String propName : col) {
			ISinglePropertyStat stat = statistic.findPropertyStatistic(name,
					nodeType, propName);
			if (stat != null) {
				IPropertyInformation propInf = new BaseDatasetinforamation(
						stat, propName, root, nodeType);
				propertyMap.put(propName, propInf);
			}
		}
	}

	@Override
	public String getDescription() {
		return String.format("Dataset:  "+name);
	}

	@Override
	public Set<String> getPropertySet() {
		return Collections.unmodifiableSet(propertyMap.keySet());
	}

	@Override
	public IPropertyInformation getPropertyInformation(String propertyName) {
		return propertyMap.get(propertyName);
	}

	@Override
	public boolean isAggregated() {
		return isAggregated;
	}

	@Override
	public Node getRootNode() {
		return root;
	}

	public static class BaseDatasetinforamation implements
			IPropertyInformation {

		private ISinglePropertyStat statistic;
		private String name;
		private final Node root;
		private final String nodeType;
		private DatasetService ds;

		public BaseDatasetinforamation(ISinglePropertyStat statistic,
				String name, Node root, String nodeType) {
			super();
			this.statistic = statistic;
			this.name = name;
			this.root = root;
			this.nodeType = nodeType;
			ds = NeoServiceFactory.getInstance().getDatasetService();
		}

		@Override
		public ISinglePropertyStat getStatistic() {
			return statistic;
		}

		@Override
		public String getPropertyName() {
			return name;
		}

		@Override
		public Iterable<ISource> getValueIterable(AggregateRules rules) {
			// ignore selection rules in network
			Traverser td = Traversal
					.description()
					.depthFirst()
					.relationships(GeoNeoRelationshipTypes.CHILD,
							Direction.OUTGOING)
					.relationships(GeoNeoRelationshipTypes.NEXT,
							Direction.OUTGOING).evaluator(new Evaluator() {

						@Override
						public Evaluation evaluate(Path arg0) {
							Node node = arg0.endNode();
							String typeId = ds.getTypeId(node);
							boolean continues = true;
							boolean includes = node.hasProperty(name)&&typeId.equals(nodeType);
							return Evaluation.of(includes, continues);
						}
					}).traverse(root);
			return new SourceExistIterable(td, name, formSourceFinder(nodeType));
		}

		public ISourceFinder formSourceFinder(String nodeType) {

			if (NodeTypes.M.getId().equals(nodeType)) {
				return new ISourceFinder() {

					@Override
					public Node getSource(Node node) {
						return node;
					}

					@Override
					public Iterable<Node> getMultySource(Node node) {
						HashSet<Node> res = new HashSet<Node>();
						res.add(root);
						return res;
					}
				};
			
			}
			// TODO use network structure instead fixed types
			return null;
		}

	}

	@Override
	public String getFullDescription() {
		return getDescription();
	}
}
