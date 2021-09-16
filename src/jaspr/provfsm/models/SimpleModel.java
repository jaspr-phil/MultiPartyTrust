//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.models;

import de.parsemis.graph.Graph;
import jaspr.provfsm.core.GraphUtil;
import jaspr.provfsm.core.RandUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class SimpleModel extends Model {
    private Classifier learner;
    private Classifier model;
    private Instances header;
    private Set<String> nodeList;
    private NodeNamer nodeNamer;
    private boolean binary;

    public SimpleModel(Classifier learner, NodeNamer nodeNamer, boolean binary) {
        this.learner = learner;
        this.model = null;
        this.nodeNamer = nodeNamer;
        this.binary = binary;
    }

    public NodeNamer getNodeNamer() {
        return this.nodeNamer;
    }

    public void forget() {
        this.model = null;
        this.header = null;
        this.nodeList = null;
    }

    public void learn(List<Graph<String, String>> graphs, List<Double> values) throws Exception {
        ArrayList<Attribute> atts = new ArrayList();
        List<String> binaryValues = new ArrayList();
        binaryValues.add("F");
        binaryValues.add("T");
        this.nodeList = new HashSet();
        Iterator var5 = graphs.iterator();

        while(var5.hasNext()) {
            Graph<String, String> graph = (Graph)var5.next();
            this.nodeList.addAll(GraphUtil.getNodesOf(graph));
        }

        System.out.println(this.nodeList);
        if (this.binary) {
            atts.add(new Attribute("Target", binaryValues));
        } else {
            atts.add(new Attribute("Target"));
        }

        int atti = 0;

        for(Iterator var15 = this.nodeList.iterator(); var15.hasNext(); ++atti) {
            String node = (String)var15.next();
            atts.add(new Attribute(atti + "", binaryValues));
        }

        Instances data = new Instances("TrainingData", atts, graphs.size());
        data.setClassIndex(0);
        Iterator<Graph<String, String>> graphIter = graphs.iterator();
        Iterator valueIter = values.iterator();

        do {
            Graph<String, String> graph = (Graph)graphIter.next();
            double value = (Double)valueIter.next();
            double[] input = this.makeFeatures(graph, value);
            Instance instance = new DenseInstance(1.0D, input);
            data.add(instance);
        } while(graphIter.hasNext() && valueIter.hasNext());

        System.out.println("\tData size = " + data.numInstances() + "x" + data.numAttributes());
        this.header = new Instances(data, 0, 0);
        this.model = AbstractClassifier.makeCopy(this.learner);
        this.model.buildClassifier(data);
        System.out.println("\tdone.");
    }

    public double predict(Graph<String, String> graph) throws Exception {
        if (this.model == null && this.binary) {
            return RandUtil.rand.nextBoolean() ? 1.0D : 0.0D;
        } else if (this.model == null) {
            return 0.0D;
        } else {
            Instance input = new DenseInstance(1.0D, this.makeFeatures(graph, 0.0D));
            input.setDataset(this.header);
            return this.model.classifyInstance(input);
        }
    }

    private double[] makeFeatures(Graph<String, String> graph, double value) {
        double[] ret = new double[this.nodeList.size() + 1];
        Collection<String> graphNodes = GraphUtil.getNodesOf(graph);
        ret[0] = value;
        int i = 1;

        for(Iterator var7 = this.nodeList.iterator(); var7.hasNext(); ++i) {
            String node = (String)var7.next();
            if (graphNodes.contains(node)) {
                ret[i] = 1.0D;
            } else {
                ret[i] = 0.0D;
            }
        }

        return ret;
    }

    public String name() {
        return this.getClass().getSimpleName() + "-" + this.nodeNamer.getClass().getSimpleName();
    }
}
