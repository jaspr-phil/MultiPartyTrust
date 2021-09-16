//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.models;

import de.parsemis.graph.Graph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.openprovenance.prov.model.Document;

public class MeanModel extends Model {
    double sumValues = 0.0D;
    int numExamples = 0;

    public MeanModel() {
    }

    public void forget() {
        this.sumValues = 0.0D;
        this.numExamples = 0;
    }

    public void learn(List<Graph<String, String>> graphs, List<Double> values) {
        for(Iterator var3 = values.iterator(); var3.hasNext(); ++this.numExamples) {
            double v = (Double)var3.next();
            this.sumValues += v;
        }

    }

    public void learnFromProv(List<Document> graphs, List<Double> values) {
        for(Iterator var3 = values.iterator(); var3.hasNext(); ++this.numExamples) {
            double v = (Double)var3.next();
            this.sumValues += v;
        }

    }

    public double predict(Graph<String, String> graph) {
        return this.numExamples == 0 ? 0.0D : this.sumValues / (double)this.numExamples;
    }

    public double predict(Document document) {
        return this.numExamples == 0 ? 0.0D : this.sumValues / (double)this.numExamples;
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
        return new FullNodeNamer();
    }
}
