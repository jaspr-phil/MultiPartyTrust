package jaspr.provfsm.treeworkflowsim;


import jaspr.provfsm.core.*;
import jaspr.provfsm.treeworkflowsim.workflows.TreeWorkflow;
import jaspr.provfsm.treeworkflowsim.workflows.Task;
import jaspr.provfsm.treeworkflowsim.workflows.Widget;
import jaspr.provfsm.treeworkflowsim.workflows.Worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by phil on 18/04/18.
 */
public class Simulation extends ISimulation<SimParams> {

    private List<Task> tasks;
    private List<Worker> workers;
    private List<Widget> widgets;

    public Simulation(SimParams p) {
        super(p);

        int numTasks = p.totTasks;
        int numItems = p.totItems;
        int numWorkers = p.totWorkers;

        int numProperties = 1;

        this.tasks = new ArrayList<>();
        this.workers = new ArrayList<>();
        this.widgets = new ArrayList<>();

        for (int i=0;i<numTasks;i++) {
            List<Double> properties = new ArrayList<>(RandUtil.randGaussians(numProperties, 1, p.taskStd));
//            System.out.println(properties);
            tasks.add(new Task(""+i, 1, 1, properties));
        }

        for (Task task : tasks) {
            task.setFollowWeightings(tasks, 0, p.followStd);
        }

        for (int i=0;i<numWorkers;i++) {
            List<Double> properties = new ArrayList<>(RandUtil.randGaussians(numProperties, 1, p.workerStd));
//            System.out.println(properties);
            workers.add(new Worker(""+i, properties, tasks, p.proficiencyStd, p.execStd));
        }

        for (int i=0;i<numItems;i++) {
//            List<Double> properties = new ArrayList<>(RandUtil.randDoubles(numProperties, 0,1));
            List<Double> properties = Util.ones(numProperties);
            System.out.println(properties);
            widgets.add(new Widget(""+i, 0, properties));
        }

        if (p.numWorkflows == 0) {
            for (int i=0;i<p.numSimulations;i++) {
                workflows.add(generateNewWorkflow());
            }
        } else {
            for (int i=0;i<p.numWorkflows;i++) {
                workflows.add(generateNewWorkflow());
            }
        }


    }

    @Override
    protected IWorkflow generateNewWorkflow() {
        List<Task> simTasks = RandUtil.sample(this.tasks, p.simTasks == 0 ? this.tasks.size() : p.simTasks);
        List<Worker> simWorkers = RandUtil.sample(this.workers, p.simWorkers == 0 ? this.workers.size() : p.simWorkers);
        List<Widget> simWidgets = this.widgets;

        Collections.shuffle(simTasks);
        Collections.shuffle(simWorkers);
        Collections.shuffle(simWidgets);

        return new TreeWorkflow(
                simTasks,
                simWorkers,
                simWidgets,
                p.randomWorkerAssignment,
                p.maxDepth,
                p.branchingFactor,
                0,
                0.5,
                p.numExecutions,
                ProvUtil.factory
        );
    }
}



