//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.models;

import de.parsemis.graph.Graph;
import jaspr.provfsm.core.RandUtil;
import java.util.List;
import org.openprovenance.prov.model.Document;

public class RandomModel extends Model {
    private boolean isBinary;

    public RandomModel(boolean isBinary) {
        this.isBinary = isBinary;
    }

    public void forget() {
    }

    public NodeNamer getNodeNamer() {
        throw new UnsupportedOperationException();
    }

    public void learnFromProv(List<Document> graphs, List<Double> values) {
    }

    public void learn(List<Graph<String, String>> graphs, List<Double> values) throws Exception {
    }

    public double predict(Document document) {
        if (this.isBinary) {
            return RandUtil.rand.nextBoolean() ? 0.0D : 1.0D;
        } else {
            return RandUtil.randDouble(0.0D, 1.0D);
        }
    }

    public double predict(Graph<String, String> graph) throws Exception {
        if (this.isBinary) {
            return RandUtil.rand.nextBoolean() ? 0.0D : 1.0D;
        } else {
            return RandUtil.randDouble(0.0D, 1.0D);
        }
    }
}
