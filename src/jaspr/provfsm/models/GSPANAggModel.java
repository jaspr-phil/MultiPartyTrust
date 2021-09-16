//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.models;

import de.parsemis.Miner;
import de.parsemis.graph.Graph;
import de.parsemis.miner.environment.Settings;
import de.parsemis.miner.general.Fragment;
import jaspr.provfsm.core.GraphUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GSPANAggModel extends Model {
    Map<Graph<String, String>, GSPANAggModel.Aggregate> aggs = new HashMap();
    private Settings<String, String> gspanSettings;
    List<Graph<String, String>> subgraphs;
    NodeNamer nodeNamer;
    private int maxNumFeatures;
    private boolean binary;

    public GSPANAggModel(Settings<String, String> gspanSettings, NodeNamer nodeNamer, int maxNumFeatures, boolean binary) {
        this.binary = binary;
        this.gspanSettings = gspanSettings;
        this.subgraphs = new ArrayList();
        this.nodeNamer = nodeNamer;
        this.maxNumFeatures = maxNumFeatures;
    }

    public NodeNamer getNodeNamer() {
        return this.nodeNamer;
    }

    public void forget() {
        this.aggs = new HashMap();
        this.subgraphs = new ArrayList();
    }

    public void learn(List<Graph<String, String>> graphs, List<Double> values) throws Exception {
        this.forget();
        Collection<Fragment<String, String>> fragments = Miner.mine(graphs, this.gspanSettings);
        Iterator graphIter = fragments.iterator();

        Graph graph;
        while(graphIter.hasNext()) {
            Fragment<String, String> fragment = (Fragment)graphIter.next();
            graph = fragment.toGraph();
            this.subgraphs.add(graph);
        }

        graphIter = graphs.iterator();
        Iterator valueIter = values.iterator();

        do {
            graph = (Graph)graphIter.next();
            double value = (Double)valueIter.next();
            List<Graph<String, String>> sgs = new ArrayList();
            Iterator var10 = this.subgraphs.iterator();

            Graph sg;
            while(var10.hasNext()) {
                sg = (Graph)var10.next();
                if (GraphUtil.isSubgraphOf(sg, graph)) {
                    sgs.add(sg);
                }
            }

            value /= (double)sgs.size();
            var10 = sgs.iterator();

            while(var10.hasNext()) {
                sg = (Graph)var10.next();
                if (!this.aggs.containsKey(sg)) {
                    this.aggs.put(sg, new GSPANAggModel.Aggregate(value, 1));
                } else {
                    ((GSPANAggModel.Aggregate)this.aggs.get(sg)).addObservation(value);
                }
            }
        } while(graphIter.hasNext() && valueIter.hasNext());

        System.out.println("\tdone.");
    }

    public double predict(Graph<String, String> graph) throws Exception {
        if (this.aggs.isEmpty()) {
            return 0.0D;
        } else {
            double sum = 0.0D;
            int count = 1;
            Iterator var5 = this.subgraphs.iterator();

            while(var5.hasNext()) {
                Graph<String, String> sg = (Graph)var5.next();
                if (GraphUtil.isSubgraphOf(sg, graph)) {
                    sum += ((GSPANAggModel.Aggregate)this.aggs.get(sg)).mean();
                    ++count;
                }
            }

            return sum;
        }
    }

    public String name() {
        return this.getClass().getSimpleName() + "-" + this.nodeNamer.getClass().getSimpleName() + "-" + this.maxNumFeatures;
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
