//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.models;

import de.parsemis.graph.Graph;
import jaspr.provfsm.core.GraphUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ContextAggModel extends Model {
    NodeNamer nodeNamer;
    Map<String, ContextAggModel.Aggregate> aggs;

    public ContextAggModel(NodeNamer nodeNamer) {
        this.nodeNamer = nodeNamer;
        this.aggs = new HashMap();
    }

    public void forget() {
        this.aggs = new HashMap();
    }

    public void learn(List<Graph<String, String>> graphs, List<Double> values) {
        Iterator<Graph<String, String>> graphIter = graphs.iterator();
        Iterator valueIter = values.iterator();

        do {
            Graph<String, String> graph = (Graph)graphIter.next();
            double value = (Double)valueIter.next();
            Collection<String> graphEdges = GraphUtil.getEdgesOf(graph, "WasAssociatedWith");
            Iterator var9 = graphEdges.iterator();

            while(var9.hasNext()) {
                String edge = (String)var9.next();
                if (!this.aggs.containsKey(edge)) {
                    this.aggs.put(edge, new ContextAggModel.Aggregate(value, 1));
                } else {
                    ((ContextAggModel.Aggregate)this.aggs.get(edge)).addObservation(value);
                }
            }
        } while(graphIter.hasNext() && valueIter.hasNext());

    }

    public double predict(Graph<String, String> graph) {
        if (this.aggs.isEmpty()) {
            return 0.0D;
        } else {
            double sum = 0.0D;
            int count = 1;
            Collection<String> graphEdges = GraphUtil.getEdgesOf(graph, "WasAssociatedWith");
            Iterator var6 = graphEdges.iterator();

            while(var6.hasNext()) {
                String edge = (String)var6.next();
                if (this.aggs.containsKey(edge)) {
                    sum += ((ContextAggModel.Aggregate)this.aggs.get(edge)).mean();
                    ++count;
                }
            }

            return sum / (double)count;
        }
    }

    public Collection<Double> predicts(List<Graph<String, String>> graphs) {
        Collection<Double> ret = new ArrayList();
        Iterator var3 = graphs.iterator();

        while(var3.hasNext()) {
            Graph graph = (Graph)var3.next();
            ret.add(this.predict(graph));
        }

        return ret;
    }

    public NodeNamer getNodeNamer() {
        return this.nodeNamer;
    }

    class Aggregate {
        double sum;
        int count;

        public Aggregate(double sum, int count) {
            this.sum = sum;
            this.count = count;
        }

        public void addObservation(double value) {
            this.sum += value;
            ++this.count;
        }

        public double mean() {
            return this.sum / (double)this.count;
        }

        public String toString() {
            return this.sum + "/" + this.count + "=" + this.mean();
        }
    }
}
