//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.models;

import de.parsemis.graph.Graph;
import jaspr.provfsm.core.GraphUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.openprovenance.prov.model.Document;

public abstract class Model {
    public Model() {
    }

    public abstract void forget();

    public abstract NodeNamer getNodeNamer();

    public abstract void learn(List<Graph<String, String>> var1, List<Double> var2) throws Exception;

    public void learnFromProv(List<Document> documents, List<Double> values) throws Exception {
        List<Graph<String, String>> graphs = new ArrayList();
        Iterator var4 = documents.iterator();

        while(var4.hasNext()) {
            Document document = (Document)var4.next();
            Graph<String, String> graph = GraphUtil.makeGraph(document, this.getNodeNamer());
            graphs.add(graph);
        }

        this.learn(graphs, values);
    }

    public abstract double predict(Graph<String, String> var1) throws Exception;

    public double predict(Document document) throws Exception {
        Graph<String, String> graph = GraphUtil.makeGraph(document, this.getNodeNamer());
        return this.predict(graph);
    }

    public String name() {
        return this.getClass().getSimpleName();
    }
}
