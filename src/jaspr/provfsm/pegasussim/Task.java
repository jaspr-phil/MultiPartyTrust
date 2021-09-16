//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.pegasussim;

import jaspr.provfsm.core.ITask;
import jaspr.provfsm.core.NamedObject;
import jaspr.provfsm.core.RandUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.openprovenance.prov.model.Element;
import org.openprovenance.prov.model.ProvFactory;

public class Task extends NamedObject implements Comparable<Task>, ITask {
    private Map<Task, Double> followWeightings = new HashMap();
    private List<Double> properties;

    public Task(String name, List<Double> properties) {
        super(name);
        this.properties = properties;
    }

    public Element asProv(ProvFactory factory) {
        return factory.newActivity(this.getQualifiedName(factory), (String)null);
    }

    public List<Double> getProperties() {
        return this.properties;
    }

    public void setFollowWeightings(List<Task> tasks, double followMean, double followStd) {
        for (Task task : tasks) {
            double weight = RandUtil.randGaussian(followMean, followStd);
//            double weight = RandUtil.randDouble(-followStd,followStd);
//            double weight = followStd;
            followWeightings.put(task, weight);
        }
    }

    public double followWeighting(Task task) {
        return this.followWeightings.containsKey(task) ? (Double)this.followWeightings.get(task) : 1.0D;
    }

    public double meanProperty() {
        return this.properties.stream().mapToDouble(x -> x).average().getAsDouble();
    }

    public int compareTo(Task o) {
        return this.meanProperty() > o.meanProperty() ? 1 : -1;
    }
}
