package jaspr.provfsm.designersim.workflows;

import jaspr.provfsm.core.ProvUtil;
import jaspr.provfsm.treeworkflowsim.workflows.Task;
import jaspr.provfsm.treeworkflowsim.workflows.TreeWorkflow;
import jaspr.provfsm.treeworkflowsim.workflows.Widget;
import jaspr.provfsm.treeworkflowsim.workflows.Worker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phil on 25/05/18.
 */
public class Designer {

    private List<Task> tasks;
    private List<Worker> workers;
    private List<Widget> widgets;

    private int maxDepth;
    private int branchingFactor;
    private int numExecutions;
    private boolean workerServiceAssignment;

    public Designer(
            List<Task> tasks,
            List<Worker> workers,
            List<Widget> widgets,
            int maxDepth,
            int branchingFactor,
            int numExecutions,
            boolean workerServiceAssignment
    ) {
        this.tasks = tasks;
        this.workers = workers;
        this.widgets = widgets;
        this.maxDepth = maxDepth;
        this.branchingFactor = branchingFactor;
        this.numExecutions = numExecutions;
        this.workerServiceAssignment = workerServiceAssignment;
    }


    public TreeWorkflow design() {
        return new TreeWorkflow(tasks, workers, widgets, workerServiceAssignment, maxDepth, branchingFactor, 0, 0.5, numExecutions, ProvUtil.factory);
    }

    public List<TreeWorkflow> designPortfolio(int portfolioSize) {
        List<TreeWorkflow> workflows = new ArrayList<>();
        for (int i=0;i<portfolioSize;i++) {
            workflows.add(this.design());
        }
        return workflows;
    }

    public String toString() {
        return "D="+maxDepth+" B="+branchingFactor+" E="+numExecutions+" W="+workers.size()+" T="+tasks.size();
    }

}
