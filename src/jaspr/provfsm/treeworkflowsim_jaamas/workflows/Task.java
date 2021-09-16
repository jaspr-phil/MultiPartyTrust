package jaspr.provfsm.treeworkflowsim_jaamas.workflows;

import jaspr.provfsm.core.ITask;
import jaspr.provfsm.core.RandUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by phil on 08/03/18.
 */
public class Task extends NamedObject implements Comparable<Task>, ITask {

    private Map<Task,Double> followWeightings = new HashMap<>();
    private List<Double> properties;
    private int numInputs;
    private int numOutputs;

    public Task(String name, int numInputs, int numOutputs, List<Double> properties) {
        super(name);

        this.numInputs = numInputs;
        this.numOutputs = numOutputs;
        this.properties = properties;
    }

    public int getNumInputs() {
        return numInputs;
    }

    public int getNumOutputs() {
        return numOutputs;
    }

    public List<Double> getProperties() {
        return properties;
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
        if (followWeightings.containsKey(task)) {
            return followWeightings.get(task);
        } else {
            return 1;
        }
    }

    public double meanProperty() {
        return this.properties.stream().mapToDouble(x -> x).average().getAsDouble();
    }

    @Override
    public int compareTo(Task o) {
         return this.meanProperty() > o.meanProperty() ? 1 : -1;
    }
}
