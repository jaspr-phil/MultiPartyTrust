//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.core;

import de.parsemis.graph.Edge;
import de.parsemis.graph.Graph;
import de.parsemis.graph.HPListGraph;
import de.parsemis.graph.HPMutableGraph;
import de.parsemis.graph.Node;
import de.parsemis.graph.SubGraphComparator;
import jaspr.provfsm.models.NodeNamer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.openprovenance.prov.model.Activity;
import org.openprovenance.prov.model.Agent;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.StatementOrBundle;
import org.openprovenance.prov.model.Used;
import org.openprovenance.prov.model.WasAssociatedWith;
import org.openprovenance.prov.model.WasAttributedTo;
import org.openprovenance.prov.model.WasDerivedFrom;
import org.openprovenance.prov.model.WasGeneratedBy;

public class GraphUtil {
    private static SubGraphComparator<String, String> subgraphComparator = new SubGraphComparator();

    public GraphUtil() {
    }

    public static void init() {
    }

    public static Graph<String, String> makeGraph(Document document, NodeNamer nodeNamer) {
        HPMutableGraph<String, String> graph = new HPListGraph();
        Map<String, Integer> indexMap = new HashMap();
        Iterator var4 = document.getStatementOrBundle().iterator();

        StatementOrBundle sob;
        String toName;
        while(var4.hasNext()) {
            sob = (StatementOrBundle)var4.next();
            int index;
            if (sob instanceof Agent) {
                toName = nodeNamer.name(((Agent)sob).getId());
                index = graph.addNodeIndex(toName);
                indexMap.put(toName, index);
            } else if (sob instanceof Entity) {
                toName = nodeNamer.name(((Entity)sob).getId());
                index = graph.addNodeIndex(toName);
                indexMap.put(toName, index);
            } else if (sob instanceof Activity) {
                toName = nodeNamer.name(((Activity)sob).getId());
                index = graph.addNodeIndex(toName);
                indexMap.put(toName, index);
            }
        }

        var4 = document.getStatementOrBundle().iterator();

        while(var4.hasNext()) {
            sob = (StatementOrBundle)var4.next();
            String fromName;
            if (sob instanceof WasDerivedFrom) {
                toName = nodeNamer.name(((WasDerivedFrom)sob).getGeneratedEntity());
                fromName = nodeNamer.name(((WasDerivedFrom)sob).getUsedEntity());
                graph.addEdgeIndex((Integer)indexMap.get(fromName), (Integer)indexMap.get(toName), "WasDerivedFrom", 1);
            } else if (sob instanceof WasGeneratedBy) {
                toName = nodeNamer.name(((WasGeneratedBy)sob).getEntity());
                fromName = nodeNamer.name(((WasGeneratedBy)sob).getActivity());
                graph.addEdgeIndex((Integer)indexMap.get(fromName), (Integer)indexMap.get(toName), "WasGeneratedBy", 1);
            } else if (sob instanceof WasAssociatedWith) {
                toName = nodeNamer.name(((WasAssociatedWith)sob).getAgent());
                fromName = nodeNamer.name(((WasAssociatedWith)sob).getActivity());
                graph.addEdgeIndex((Integer)indexMap.get(fromName), (Integer)indexMap.get(toName), "WasAssociatedWith", 1);
            } else if (sob instanceof WasAttributedTo) {
                toName = nodeNamer.name(((WasAttributedTo)sob).getEntity());
                fromName = nodeNamer.name(((WasAttributedTo)sob).getAgent());
                graph.addEdgeIndex((Integer)indexMap.get(fromName), (Integer)indexMap.get(toName), "WasAttributedTo", 1);
            } else if (sob instanceof Used) {
                toName = nodeNamer.name(((Used)sob).getActivity());
                fromName = nodeNamer.name(((Used)sob).getEntity());
                graph.addEdgeIndex((Integer)indexMap.get(fromName), (Integer)indexMap.get(toName), "Used", 1);
            }
        }

        return graph.toGraph();
    }

    public static boolean isSubgraphOf(Graph<String, String> a, Graph<String, String> b) {
        return subgraphComparator.compare(a, b) == 0;
    }

    public static Collection<String> getNodesOf(Graph<String, String> graph) {
        Collection<String> nodes = new HashSet();
        Iterator nodeIter = graph.nodeIterator();

        while(nodeIter.hasNext()) {
            Node<String, String> node = (Node)nodeIter.next();
            nodes.add(node.getLabel());
        }

        return nodes;
    }

    public static Collection<String> getEdgesOf(Graph<String, String> graph, String containing) {
        Collection<String> nodes = new HashSet();
        Iterator edgeIter = graph.edgeIterator();

        while(edgeIter.hasNext()) {
            Edge<String, String> edge = (Edge)edgeIter.next();
            Node<String, String> n1 = edge.getNodeA();
            Node<String, String> n2 = edge.getNodeB();
            if (containing != null && ((String)edge.getLabel()).contains(containing)) {
                nodes.add((String)n1.getLabel() + "->" + (String)n2.getLabel());
            }
        }

        return nodes;
    }
}
