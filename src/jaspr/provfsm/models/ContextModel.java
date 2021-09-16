//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.models;

import de.parsemis.graph.Graph;
import jaspr.provfsm.core.GraphUtil;
import jaspr.provfsm.core.RandUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.CorrelationAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class ContextModel extends Model {
    private Classifier learner;
    private Classifier model;
    private Instances header;
    private List<String> edgeList;
    private NodeNamer nodeNamer;
    private int maxNumFeatures;
    private boolean binary;

    public ContextModel(Classifier learner, NodeNamer nodeNamer, int maxNumFeatures, boolean binary) {
        this.learner = learner;
        this.model = null;
        this.nodeNamer = nodeNamer;
        this.maxNumFeatures = maxNumFeatures;
        this.binary = binary;
    }

    public NodeNamer getNodeNamer() {
        return this.nodeNamer;
    }

    public void forget() {
        this.model = null;
        this.header = null;
        this.edgeList = null;
    }

    public void learn(List<Graph<String, String>> graphs, List<Double> values) throws Exception {
        ArrayList<Attribute> atts = new ArrayList();
        List<String> binaryValues = new ArrayList();
        binaryValues.add("F");
        binaryValues.add("T");
        this.edgeList = new ArrayList();
        Iterator var5 = graphs.iterator();

        while(var5.hasNext()) {
            Graph<String, String> graph = (Graph)var5.next();
            this.edgeList.addAll(GraphUtil.getEdgesOf(graph, "WasAssociatedWith"));
        }

        this.edgeList = new ArrayList(new HashSet(this.edgeList));
        System.out.println(this.edgeList);
        if (this.binary) {
            atts.add(new Attribute("Target", binaryValues));
        } else {
            atts.add(new Attribute("Target"));
        }

        int atti = 0;

        for(Iterator var20 = this.edgeList.iterator(); var20.hasNext(); ++atti) {
            String edge = (String)var20.next();
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
        if (data.numAttributes() > this.maxNumFeatures) {
            System.out.println("\tSelecting features...");
            AttributeSelection attsel = new AttributeSelection();
            ASEvaluation eval = new CorrelationAttributeEval();
            Ranker search = new Ranker();
            search.setNumToSelect(this.maxNumFeatures);
            attsel.setEvaluator(eval);
            attsel.setSearch(search);
            attsel.SelectAttributes(data);
            int[] selectedFragments = attsel.selectedAttributes();
            Arrays.sort(selectedFragments);
            Remove rusf = new Remove();
            rusf.setAttributeIndicesArray(selectedFragments);
            rusf.setInvertSelection(true);
            rusf.setInputFormat(data);
            data = Filter.useFilter(data, rusf);
            List<String> subgraphs_cpy = new ArrayList();
            int[] var15 = selectedFragments;
            int var16 = selectedFragments.length;

            for(int var17 = 0; var17 < var16; ++var17) {
                int sfi = var15[var17];
                if (sfi != 0) {
                    subgraphs_cpy.add(this.edgeList.get(sfi - 1));
                }
            }

            this.edgeList = subgraphs_cpy;
            System.out.println("\tdone.\tData size = " + data.numInstances() + "x" + data.numAttributes());
        }

        this.header = new Instances(data, 0, 0);
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
        double[] ret = new double[this.edgeList.size() + 1];
        Collection<String> graphEdges = GraphUtil.getEdgesOf(graph, "WasAssociatedWith");
        ret[0] = value;
        int i = 1;

        for(Iterator var7 = this.edgeList.iterator(); var7.hasNext(); ++i) {
            String edge = (String)var7.next();
            if (graphEdges.contains(edge)) {
                ret[i] = 1.0D;
            } else {
                ret[i] = 0.0D;
            }
        }

        return ret;
    }

    public String name() {
        return this.getClass().getSimpleName() + "-" + this.nodeNamer.getClass().getSimpleName() + "-" + this.maxNumFeatures;
    }
}
