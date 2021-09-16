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
import jaspr.provfsm.core.RandUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

public class GSPANModel extends Model {
    private Classifier learner;
    private Classifier model;
    private Instances header;
    private Settings<String, String> gspanSettings;
    List<Graph<String, String>> subgraphs;
    NodeNamer nodeNamer;
    private int maxNumFeatures;
    private boolean binary;

    public GSPANModel(Classifier learner, Settings<String, String> gspanSettings, NodeNamer nodeNamer, int maxNumFeatures, boolean binary) {
        this.learner = learner;
        this.binary = binary;
        this.model = null;
        this.gspanSettings = gspanSettings;
        this.subgraphs = new ArrayList();
        this.nodeNamer = nodeNamer;
        this.maxNumFeatures = maxNumFeatures;
    }

    public NodeNamer getNodeNamer() {
        return this.nodeNamer;
    }

    public void forget() {
        this.model = null;
        this.header = null;
        this.subgraphs = new ArrayList();
    }

    public void learn(List<Graph<String, String>> graphs, List<Double> values) throws Exception {
        this.forget();
        System.out.println("\tMining subgraphs from " + graphs.size() + " graphs...");
        ArrayList<Attribute> atts = new ArrayList();
        List<String> binaryValues = new ArrayList();
        binaryValues.add("F");
        binaryValues.add("T");
        Collection<Fragment<String, String>> fragments = Miner.mine(graphs, this.gspanSettings);
        if (this.binary) {
            atts.add(new Attribute("Target", binaryValues));
        } else {
            atts.add(new Attribute("Target"));
        }

        int atti = 0;

        for(Iterator var7 = fragments.iterator(); var7.hasNext(); ++atti) {
            Fragment<String, String> fragment = (Fragment)var7.next();
            Graph<String, String> subgraph = fragment.toGraph();
            atts.add(new Attribute(atti + "", binaryValues));
            this.subgraphs.add(subgraph);
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
            List<Graph<String, String>> subgraphs_cpy = new ArrayList();
            int[] var16 = selectedFragments;
            int var17 = selectedFragments.length;

            for(int var18 = 0; var18 < var17; ++var18) {
                int sfi = var16[var18];
                if (sfi != 0) {
                    subgraphs_cpy.add(this.subgraphs.get(sfi - 1));
                }
            }

            this.subgraphs = subgraphs_cpy;
            System.out.println("\tdone.\tData size = " + data.numInstances() + "x" + data.numAttributes());
        }

        this.header = new Instances(data, 0, 0);
        System.out.println("\tLearning model...");
        this.model = AbstractClassifier.makeCopy(this.learner);
        this.model.buildClassifier(data);
        System.out.println("\tdone.");
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
        double[] ret = new double[this.subgraphs.size() + 1];
        ret[0] = value;
        int i = 1;

        for(Iterator var6 = this.subgraphs.iterator(); var6.hasNext(); ++i) {
            Graph<String, String> sg = (Graph)var6.next();
            if (GraphUtil.isSubgraphOf(sg, graph)) {
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
