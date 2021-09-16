package jaspr.provfsm.epigenicssim;

import jaspr.provfsm.core.IWorker;
import jaspr.provfsm.core.NamedObject;
import jaspr.provfsm.core.RandUtil;
import jaspr.provfsm.core.Util;
import org.openprovenance.prov.model.Element;
import org.openprovenance.prov.model.ProvFactory;

import java.util.*;

/**
 * Created by phil on 08/03/18.
 */
public class Worker extends NamedObject implements Comparable<Worker>, IWorker {

    private Map<Task,List<Double>> competencies;
    private Map<String,Double> followWeight;
    private List<Double> properties;
    double execStd;

    public Worker(String name, List<Double> properties, List<Task> tasks, double proficiencyStd, double execStd) {
        super(name);
        this.properties = properties;
        this.execStd = execStd;

        this.followWeight = new HashMap<>();
        competencies = new HashMap<>();
        for (Task task : tasks) {
            List<Double> comp = new ArrayList<>();
            for (int i=0;i<properties.size();i++) {
//                comp.add((properties.get(i)+RandUtil.randGaussian(0,0.5)));// * task.getProperties().get(i));
                comp.add(properties.get(i) * task.getProperties().get(i) + RandUtil.randGaussian(0, proficiencyStd));
            }

            competencies.put(task, comp);
        }
    }

    public Element asProv(ProvFactory factory) {
        return factory.newAgent(getQualifiedName(factory), (String)null);
    }

    public double meanCompetency(Task task) {
        return Util.mean(competencies.get(task));
    }

    public double meanProperty() {
        return this.properties.stream().mapToDouble(x -> x).average().getAsDouble();
    }

    public double followWeight(IWorker worker) {
        if (worker == null) return 1;
        if (!followWeight.containsKey(worker.name())) {
            followWeight.put(worker.name(), RandUtil.randDouble(0.,2.));
        }
//        System.out.println(followWeight);
//        System.out.println(this.name()+"->"+worker.name()+": "+followWeight.get(worker.name()));
        return followWeight.get(worker.name());
    }

    @Override
    public int compareTo(Worker o) {
        return this.meanProperty() > o.meanProperty() ? 1 : -1;
    }

    public String toString() {
        return super.toString()+":"+competencies;
    }
}
