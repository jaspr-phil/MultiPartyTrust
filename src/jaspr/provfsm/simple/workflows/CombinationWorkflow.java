package jaspr.provfsm.simple.workflows;

import jaspr.provfsm.core.RandUtil;
import jaspr.provfsm.core.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by phil on 10/11/17.
 */
public class CombinationWorkflow extends ProcessingWorkflow {

    private int nextItemId;
    private Map<Item,Double> priorities;

    public CombinationWorkflow(List<Item> items, List<Task> tasks, List<Worker> workers, int numExecutions) {
        super(items, tasks, workers, numExecutions);
    }

    @Override
    public void reset() {
        super.reset();

        this.nextItemId = 0;
        this.priorities = new HashMap<>();

        for (Item item : this.items) {
            priorities.put(item, Double.NEGATIVE_INFINITY);
        }
    }

    @Override
    public void execute() {
        if (this.executed) {
            this.reset();
        }
        int taskIndex = 0;
        while (this.items.size() > 1) {
            Worker worker = RandUtil.choose(workers);
//            Task task = RandUtil.choose(tasks);
            Task task = tasks.get(taskIndex);
//            List<Item> inputItems = RandUtil.sample(items, RandUtil.randInt(2, items.size()));
            List<Item> inputItems = RandUtil.sample(items, RandUtil.randInt(2,2));
//            List<Item> inputItems = RandUtil.sample(items, 2);
//            List<Item> inputItems = items.subList(items.size()-RandUtil.randInt(2,items.size()-1),items.size());

            List<Item> outputItems = this.doTask(task, worker, inputItems);
            System.out.println(Util.format(utility)+" "+this.items+" : "+inputItems+" "+worker+" "+task+" : "+Util.format(outputItems));
            this.updateProv(inputItems, worker, task, outputItems);
            this.updateItems(inputItems, outputItems);

            ++taskIndex;
        }
        this.utility = 0;
        for (Item item : this.items) {
            System.out.print("Utilities: "+item+"="+Util.format(item.utility)+", ");
            this.utility += item.utility;
        }
        System.out.println("\nExecution utility = "+Util.format(utility));
        this.executed = true;
    }

    public List<Item> doTask(Task task, Worker worker, List<Item> inputItems) {

        double taskUtility = 0.;
        for (Item item : inputItems) {
            if (priorities.get(item) <= task.priority) {
                taskUtility += item.utility;// + worker.taskCompetency(task);
            }
        }

        taskUtility = taskUtility * worker.taskCompetency(task);// / (double)inputItems.size();
        utility = taskUtility;

        List<Item> outputItems = new ArrayList<>();
        Item item = new Item(nextItemName(), taskUtility);
        outputItems.add(item);
        this.priorities.put(item, task.priority);

        return outputItems;
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
