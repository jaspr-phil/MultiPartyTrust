package jaspr.provfsm.treeworkflowsim_jaamas.workflows;

import jaspr.provfsm.core.IWidget;
import jaspr.provfsm.core.IWorker;
import jaspr.provfsm.core.RandUtil;
import jaspr.provfsm.core.Util;

import java.util.*;

/**
 * Created by phil on 08/03/18.
 */
public class Worker extends NamedObject implements Comparable<Worker>, IWorker {


    private Map<Task,List<Double>> competencies;
    private List<Double> properties;
    double execStd;

    public Worker(String name, List<Double> properties, List<Task> tasks, double proficiencyStd, double execStd) {
        super(name);
        this.properties = properties;
        this.execStd = execStd;

        competencies = new HashMap<>();
        for (Task task : tasks) {
            List<Double> comp = new ArrayList<>();
            for (int i=0;i<properties.size();i++) {
//                comp.add((properties.get(i)+RandUtil.randGaussian(0,0.5)));// * task.getProperties().get(i));
                comp.add(properties.get(i) * task.getProperties().get(i) * RandUtil.randGaussian(1, proficiencyStd));
            }

            competencies.put(task, comp);
        }
    }

    public double meanCompetency(Task task) {
        return Util.mean(competencies.get(task));
    }

    public List<Widget> process(TaskNode current, Widget input, Stack<TaskNode> execStack, int index) {
        Task task = current.getTask();
        List<Widget> outputs = new ArrayList<>();

        for (int i=0;i<task.getNumOutputs();i++) {
            double taskWeighting = taskWeighting(task, execStack);
            List<Double> outputProperties = makeOutputProperties(task.getProperties(), input.getProperties(), competencies.get(task), taskWeighting);
            outputs.add(new Widget(input.shortName(), index++, outputProperties));
        }
        return outputs;
    }

    public List<Double> makeOutputProperties(List<Double> taskProperties, List<Double> inputProperties, List<Double> competency, double taskWeighting) {
//        System.out.println(taskProperties+" "+properties+" "+inputProperties);
        List<Double> ret = new ArrayList<>();
        for (int i=0;i<taskProperties.size();i++) {
//            double x = competency.get(i) + inputProperties.get(i);
//            double x = 0.5*inputProperties.get(i) + competency.get(i) * taskWeighting * RandUtil.randGaussian(1, execStd);
            double x = taskWeighting * inputProperties.get(i) + competency.get(i) * RandUtil.randGaussian(1, execStd);

            ret.add(x);
        }
//        System.out.println(ret);
        return ret;
    }

    public double taskWeighting(Task task, Stack<TaskNode> execStack) {
        double weighting = 1.;
//        for (TaskNode node : execStack.subList(0, execStack.size()-1)) {
        if (execStack.size() > 1) {
            TaskNode node = execStack.get(execStack.size()-2);
            weighting *= task.followWeighting(node.getTask());
        }
//        System.out.println(weighting +" "+task+" "+execStack);
        return weighting;
    }

    public double meanProperty() {
        return this.properties.stream().mapToDouble(x -> x).average().getAsDouble();
    }

    @Override
    public int compareTo(Worker o) {
        return this.meanProperty() > o.meanProperty() ? 1 : -1;
    }
}
