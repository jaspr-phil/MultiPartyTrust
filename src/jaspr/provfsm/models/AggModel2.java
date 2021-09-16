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

public class AggModel2 extends Model {
    NodeNamer nodeNamer;
    Map<String, AggModel2.Aggregate> aggs;

    public AggModel2(NodeNamer nodeNamer) {
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
            Collection<String> graphNodes = GraphUtil.getNodesOf(graph);
            double value = (Double)valueIter.next() / (double)graphNodes.size();
            Iterator var9 = graphNodes.iterator();

            while(var9.hasNext()) {
                String node = (String)var9.next();
                if (!this.aggs.containsKey(node)) {
                    this.aggs.put(node, new AggModel2.Aggregate(value, 1));
                } else {
                    ((AggModel2.Aggregate)this.aggs.get(node)).addObservation(value);
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
            Collection<String> graphNodes = GraphUtil.getNodesOf(graph);
            Iterator var6 = graphNodes.iterator();

            while(var6.hasNext()) {
                String node = (String)var6.next();
                if (this.aggs.containsKey(node)) {
                    sum += ((AggModel2.Aggregate)this.aggs.get(node)).mean();
                    ++count;
                }
            }

            return sum;
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
