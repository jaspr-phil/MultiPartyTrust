//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.pegasussim;

import jaspr.provfsm.core.ITask;
import jaspr.provfsm.core.IWorker;
import jaspr.provfsm.core.NamedObject;
import jaspr.provfsm.core.RandUtil;
import jaspr.provfsm.core.Util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.openprovenance.prov.model.Element;
import org.openprovenance.prov.model.ProvFactory;

public class Worker extends NamedObject implements Comparable<Worker>, IWorker {
    private Map<ITask, List<Double>> competencies;
    private Map<String, Double> followWeight;
    private List<Double> properties;
    double execStd;
    double followStd;

    public Worker(String name, List<Double> properties, List<ITask> tasks, double proficiencyStd, double execStd, double followStd) {
        super(name);
        this.properties = properties;
        this.execStd = execStd;
        this.followStd = followStd;
        this.followWeight = new HashMap();
        this.competencies = new HashMap();

        for (ITask task : tasks) {
            List<Double> tps = ((Task)task).getProperties();
            List<Double> comp = new ArrayList();

            for(int i = 0; i < properties.size(); ++i) {
                comp.add((Double)properties.get(i) + RandUtil.randGaussian((Double)tps.get(i), proficiencyStd));
            }

            this.competencies.put(task, comp);
        }

    }

    public Element asProv(ProvFactory factory) {
        return factory.newAgent(this.getQualifiedName(factory), (String)null);
    }

    public double meanCompetency(Task task) {
        return Util.mean(competencies.get(task)) + RandUtil.randGaussian(0, execStd);
    }

    public double meanProperty() {
        return this.properties.stream().mapToDouble((x) -> {
            return x;
        }).average().getAsDouble();
    }

    public double followWeight(IWorker worker) {
        if (worker == null) {
            return 1.0D;
        } else {
            if (!this.followWeight.containsKey(worker.name())) {
                this.followWeight.put(worker.name(), RandUtil.randGaussian(1.0D, this.followStd));
            }

            return (Double)this.followWeight.get(worker.name());
        }
    }

    public int compareTo(Worker o) {
        return this.meanProperty() > o.meanProperty() ? 1 : -1;
    }

    public String toString() {
        return super.toString() + ":" + this.competencies;
    }
}
