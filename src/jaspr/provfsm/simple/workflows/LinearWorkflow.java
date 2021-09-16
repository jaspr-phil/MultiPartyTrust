package jaspr.provfsm.simple.workflows;

import jaspr.provfsm.core.RandUtil;

import java.util.*;

public class LinearWorkflow extends ProcessingWorkflow {

    protected int nextItemId;
    protected Map<Item,Double> priorities;

    public LinearWorkflow(List<Item> items, List<Task> tasks, List<Worker> workers, int numExecutions) {
        super(items, tasks, workers,numExecutions);
    }

    public void reset() {
        super.reset();

        this.nextItemId = 0;

        this.priorities = new HashMap<>();

        for (Item item : this.items) {
            priorities.put(item, Double.NEGATIVE_INFINITY);
        }
//        System.out.println("Reset.");
    }

    public void execute() {
        if (this.executed) {
            this.reset();
        }

        for (int ti=0;ti<this.tasks.size();ti++) {
            Task task = getTask(ti);
            Worker worker = getWorker(ti);
            List<Item> inputItems = RandUtil.sample(items, RandUtil.randInt(1, items.size()));
            List<Item> outputItems = this.doTask(task, worker, inputItems);

//            System.out.println(items + " : " + inputItems + "("+Util.format(priorities.get(inputItems.get(0)))+") " + worker + " " + task + "("+ Util.format(task.priority) + ") : " + outputItems+"\t");
            this.updateProv(inputItems, worker, task, outputItems);
            this.updateItems(inputItems, outputItems);
//            System.out.println();
        }
        for (Item item : this.items) {
            this.utility += item.utility;
        }
//        System.out.println(this.utility);
        this.executed = true;
    }

    public List<Item> doTask(Task task, Worker worker, List<Item> inputItems) {
        Item inputItem = inputItems.get(0);
        double inputPriority = this.priorities.get(inputItem);

        double taskUtility;

        if (inputPriority <= task.priority) {
            taskUtility = inputItem.utility + worker.taskCompetency(task);
        } else {
            taskUtility = inputItem.utility;// - worker.taskCompetency(task);
//            taskUtility = -inputItem.utility + worker.taskCompetency(task);
        }

//        System.out.print(Util.format(task.priority -this.priorities.get(inputItem))+"\t");

        Item outputItem = new Item(task.name(), taskUtility);
//        double outputPriority = task.priority;
        double outputPriority = Math.max(task.priority, inputPriority);
        this.priorities.put(outputItem, outputPriority);

        List<Item> outputItems = new ArrayList<>();
        outputItems.add(outputItem);
        return outputItems;
    }

    protected Task getTask(int taskIndex) {
        return tasks.get(taskIndex);
    }

    protected Worker getWorker(int taskIndex) {
        return RandUtil.choose(workers);
    }

    private String nextItemName() {
        ++nextItemId;
        return "d"+nextItemId;
    }

    private void updateItems(List<Item> inputItems, List<Item> outputItems) {
        this.items.removeAll(inputItems);
        this.items.addAll(outputItems);
    }
}
