package jaspr.provfsm.simple;

import jaspr.provfsm.core.*;
import jaspr.provfsm.simple.workflows.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by phil on 09/11/17.
 */
public class Simulation extends ISimulation<SimParams> {

    private List<Item> items;
    private List<Task> tasks;
    private List<Worker> workers;


    public Simulation(SimParams p) {
        super(p);
        System.out.println("PARAMS: "+p);

        this.items = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.workers = new ArrayList<>();

        for (int i=1;i<=p.totTasks;i++) {
            if (p.taskPriorities) {
                tasks.add(new Task("" + i, RandUtil.randDouble(0,1)));
            } else {
                tasks.add(new Task(""+i, 0));
            }
        }
        for (int i=0;i<p.totItems;i++) {
            if (p.randomItemUtilities) {
                items.add(new Item("" + i, RandUtil.randGaussian(1, 0.5)));
            } else {
                items.add(new Item(""+i, 1.));
            }
        }
        for (int i=0;i<p.totWorkers;i++) {
            double baseCompetency = RandUtil.randDouble(0,1);
            Map<Task,Double> competencies = new HashMap<>();
            for (Task task : tasks) {
                competencies.put(task, Util.bound(0, 10000, RandUtil.randGaussian(baseCompetency, 1)));
            }
            System.out.println(i+"  "+Util.format(baseCompetency)+"  "+Util.format(competencies));
            workers.add(new Worker(""+i, competencies));
        }

        for (int i=0;i<p.numWorkflows;i++) {
            workflows.add(generateNewWorkflow());
        }
//        System.out.println(workflows);
    }

    protected IWorkflow generateNewWorkflow() {
        List<Item> simItems;
        if (items.isEmpty()) {
            simItems = new ArrayList<>();
            for (int i=0;i<RandUtil.randInt(2,p.simItems);i++) {
                if (p.randomItemUtilities) {
                    simItems.add(new Item("" + i, RandUtil.randGaussian(1,0.5)));
                } else {
                    simItems.add(new Item("" + i, 1.));
                }
            }
        } else {
//            simItems = RandUtil.sample(this.items, p.simItems);
            simItems = this.items.subList(0,p.simItems);
        }
        if (p.simulationType.toLowerCase().equals("linear")) {
            return new ProvWorkflow(new LinearWorkflow(
                    simItems,
                    RandUtil.sample(this.tasks, p.simTasks),
                    RandUtil.sample(this.workers, p.simWorkers),
                    p.numExecutions
            ));
        } else if (p.simulationType.toLowerCase().equals("combination")) {
            return new ProvWorkflow(new CombinationWorkflow(
                    simItems,
                    RandUtil.sample(this.tasks, p.simTasks),
                    RandUtil.sample(this.workers, p.simWorkers),
                    p.numExecutions
            ));
        } else if (p.simulationType.toLowerCase().startsWith("decision-")) {

            return new ProvWorkflow(new DecisionWorkflow(
                    simItems,
                    RandUtil.sample(this.tasks, p.simTasks),
                    RandUtil.sample(this.workers, p.simWorkers),
                    Integer.parseInt(p.simulationType.split("-")[1]),
                    p.numExecutions
            ));
        } else {
            throw new UnsupportedOperationException("The workflow simulation type does not exist: "+p.simulationType);
        }
    }
}
