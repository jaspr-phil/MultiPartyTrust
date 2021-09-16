package jaspr.provfsm.simple.workflows;

import jaspr.provfsm.core.ProvUtil;
import jaspr.provfsm.core.RandUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DecisionWorkflow extends LinearWorkflow {


    private int numExecutionTasks;

    public DecisionWorkflow(List<Item> items, List<Task> tasks, List<Worker> workers, int numExecutionTasks, int numExecutions) {
        super(items, tasks, workers, numExecutions);
        this.numExecutionTasks = numExecutionTasks;
    }

    @Override
    public void reset() {
        super.reset();

        this.items = new ArrayList<>(this._items);
        List<Integer> selected = new ArrayList<>(RandUtil.randIntsUniq(this.numExecutionTasks, 0, this._tasks.size()));
        Collections.sort(selected);
        this.tasks = new ArrayList<>();
        this.workers = new ArrayList<>();
        for (int i : selected) {
            this.tasks.add(this._tasks.get(i));
            this.workers.add(this._workers.get(i));
        }

        this.document = factory.newDocument();
        document.setNamespace(ProvUtil.namespace);
        this.utility = 0.;
        this.executed = false;

        this.nextItemId = 0;

        this.priorities = new HashMap<>();

        for (Item item : this.items) {
            priorities.put(item, Double.NEGATIVE_INFINITY);
        }
//        System.out.println("Reset.");
    }

    protected Task getTask(int taskIndex) {
        return tasks.get(taskIndex);
    }

    protected Worker getWorker(int taskIndex) {
        return workers.get(taskIndex);
//        return RandUtil.choose(workers);
    }

}