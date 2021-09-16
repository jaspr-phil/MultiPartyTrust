//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.models;

import de.parsemis.graph.Graph;
import it.uniroma2.sag.kelp.data.dataset.Dataset;
import it.uniroma2.sag.kelp.data.dataset.SimpleDataset;
import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.data.example.SimpleExample;
import it.uniroma2.sag.kelp.data.label.Label;
import it.uniroma2.sag.kelp.data.label.NumericLabel;
import it.uniroma2.sag.kelp.data.label.StringLabel;
import it.uniroma2.sag.kelp.data.manipulator.WLSubtreeMapper;
import it.uniroma2.sag.kelp.data.representation.graph.DirectedGraphRepresentation;
import it.uniroma2.sag.kelp.data.representation.structure.UntypedStructureElement;
import it.uniroma2.sag.kelp.kernel.Kernel;
import it.uniroma2.sag.kelp.kernel.graph.ShortestPathKernel;
import it.uniroma2.sag.kelp.kernel.vector.LinearKernel;
import it.uniroma2.sag.kelp.learningalgorithm.regression.RegressionLearningAlgorithm;
import it.uniroma2.sag.kelp.learningalgorithm.regression.libsvm.EpsilonSvmRegression;
import it.uniroma2.sag.kelp.predictionfunction.PredictionFunction;
import jaspr.provfsm.core.RandUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.NotSupportedException;
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

public class GraphKernelModel extends Model {
    Label targetLabel = new StringLabel("Target");
    private RegressionLearningAlgorithm model = null;
    private PredictionFunction predictor;
    private Set<String> nodeList;
    private NodeNamer nodeNamer;
    private boolean binary;
    WLSubtreeMapper mapper = new WLSubtreeMapper("graph", "vector", 25);

    public static void main(String[] args) {
        DirectedGraphRepresentation representation = new DirectedGraphRepresentation();
        NumericLabel label = new NumericLabel(new StringLabel("Target"), 0.0F);
        SimpleExample example = new SimpleExample();
        example.addLabel(label);
        example.addRepresentation("graph", representation);
        SimpleDataset dataset = new SimpleDataset();
        dataset.addExample(example);
        ShortestPathKernel spk = new ShortestPathKernel();
        EpsilonSvmRegression svm = new EpsilonSvmRegression();
        svm.setKernel(spk);
        svm.setC(1.0F);
        svm.setpReg(0.1F);
        svm.learn(dataset);
    }

    public GraphKernelModel(NodeNamer nodeNamer, boolean binary) {
        this.nodeNamer = nodeNamer;
        this.binary = binary;
    }

    public NodeNamer getNodeNamer() {
        return this.nodeNamer;
    }

    public void forget() {
        this.model = null;
        this.nodeList = null;
    }

    public DirectedGraphRepresentation makeGraph(Document document) {
        return makeGraph(document, this.nodeNamer);
    }

    public static DirectedGraphRepresentation makeGraph(Document document, NodeNamer nodeNamer) {
        DirectedGraphRepresentation graph = new DirectedGraphRepresentation();
        int id = 0;
        Map<String, Integer> indexMap = new HashMap();
        Iterator var5 = document.getStatementOrBundle().iterator();

        StatementOrBundle sob;
        String toName;
        while(var5.hasNext()) {
            sob = (StatementOrBundle)var5.next();
            toName = null;
            if (sob instanceof Agent) {
                toName = nodeNamer.name(((Agent)sob).getId());
            } else if (sob instanceof Entity) {
                toName = nodeNamer.name(((Entity)sob).getId());
            } else if (sob instanceof Activity) {
                toName = nodeNamer.name(((Activity)sob).getId());
            }

            if (toName != null) {
                graph.addNode(id, new UntypedStructureElement(toName));
                indexMap.put(toName, id++);
            }
        }

        var5 = document.getStatementOrBundle().iterator();

        while(var5.hasNext()) {
            sob = (StatementOrBundle)var5.next();
            toName = null;
            String fromName = null;
            if (sob instanceof WasDerivedFrom) {
                toName = nodeNamer.name(((WasDerivedFrom)sob).getGeneratedEntity());
                fromName = nodeNamer.name(((WasDerivedFrom)sob).getUsedEntity());
            } else if (sob instanceof WasGeneratedBy) {
                toName = nodeNamer.name(((WasGeneratedBy)sob).getEntity());
                fromName = nodeNamer.name(((WasGeneratedBy)sob).getActivity());
            } else if (sob instanceof WasAssociatedWith) {
                toName = nodeNamer.name(((WasAssociatedWith)sob).getAgent());
                fromName = nodeNamer.name(((WasAssociatedWith)sob).getActivity());
            } else if (sob instanceof WasAttributedTo) {
                toName = nodeNamer.name(((WasAttributedTo)sob).getEntity());
                fromName = nodeNamer.name(((WasAttributedTo)sob).getAgent());
            } else if (sob instanceof Used) {
                toName = nodeNamer.name(((Used)sob).getActivity());
                fromName = nodeNamer.name(((Used)sob).getEntity());
            }

            if (fromName != null && toName != null) {
                graph.addEdge((Integer)indexMap.get(fromName), (Integer)indexMap.get(toName));
            }
        }

        graph.getNodeDistances();
        return graph;
    }

    private Example makeExample(Document document, double value) {
        return this.makeExample(this.makeGraph(document), value);
    }

    private Example makeExample(DirectedGraphRepresentation graph, double value) {
        Example example = new SimpleExample();
        example.addLabel(new NumericLabel(this.targetLabel, (float)value));
        example.addRepresentation("graph", graph);
        return example;
    }

    public void learnFromProv(List<Document> documents, List<Double> values) throws Exception {
        Dataset dataset = new SimpleDataset();

        for(int i = 0; i < documents.size(); ++i) {
            dataset.addExample(this.makeExample((Document)documents.get(i), (Double)values.get(i)));
        }

        this.learn(dataset);
    }

    public void learnFromDGR(List<DirectedGraphRepresentation> documents, List<Double> values) throws Exception {
        Dataset dataset = new SimpleDataset();

        for(int i = 0; i < documents.size(); ++i) {
            dataset.addExample(this.makeExample((DirectedGraphRepresentation)documents.get(i), (Double)values.get(i)));
        }

        this.learn(dataset);
    }

    public double predict(Document document) throws Exception {
        if (this.model == null && this.binary) {
            return RandUtil.rand.nextBoolean() ? 1.0D : 0.0D;
        } else if (this.model == null) {
            return 0.0D;
        } else {
            Example example = this.makeExample(document, 0.0D);
            this.mapper.manipulate(example);
            return (double)this.predictor.predict(example).getScore(this.targetLabel);
        }
    }

    private void learn(Dataset dataset) throws Exception {
        System.out.println(dataset);
        Iterator var2 = dataset.getExamples().iterator();

        while(var2.hasNext()) {
            Example e = (Example)var2.next();
            this.mapper.manipulate(e);
        }

        Kernel kernel = new LinearKernel();
        ((LinearKernel)kernel).setRepresentation("vector");
        this.model = new EpsilonSvmRegression();
        ((EpsilonSvmRegression)this.model).setKernel(kernel);
        ((EpsilonSvmRegression)this.model).setC(10.0F);
        ((EpsilonSvmRegression)this.model).setpReg(0.1F);
        ((EpsilonSvmRegression)this.model).setLabel(this.targetLabel);
        this.model.learn(dataset);
        this.predictor = this.model.getPredictionFunction();
    }

    public void learn(List<Graph<String, String>> graphs, List<Double> values) throws Exception {
        throw new NotSupportedException();
    }

    public double predict(Graph<String, String> graph) throws Exception {
        throw new NotSupportedException();
    }

    public String name() {
        return this.getClass().getSimpleName() + "-" + this.nodeNamer.getClass().getSimpleName();
    }
}
