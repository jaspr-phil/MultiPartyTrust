package jaspr.provfsm.treeworkflowsim.workflows;

import jaspr.provfsm.core.GraphUtil;
import jaspr.provfsm.core.ProvUtil;
import jaspr.provfsm.core.RandUtil;
import jaspr.provfsm.core.Util;
import jaspr.provfsm.core.Execution;
import jaspr.provfsm.core.Workflow;
import org.openprovenance.prov.model.ProvFactory;

import java.util.*;

/**
 * Created by phil on 08/03/18.
 */
public class TreeWorkflow extends Workflow {

    public static void main(String[] args) {
        RandUtil.init();
        ProvUtil.init();
        GraphUtil.init();

        int numWorkers = 100;
        int numTasks = 100;
        int numItems = 1;

        int numProperties = 1;

        List<Task> tasks = new ArrayList<>();
        List<Worker> workers = new ArrayList<>();
        List<Widget> widgets = new ArrayList<>();

        for (int i=0;i<numTasks;i++) {
            List<Double> properties = new ArrayList<>(RandUtil.randGaussians(numProperties, 1, 0.5));
//            List<Double> properties = new ArrayList<>();
//            properties.add(5.);
            tasks.add(new Task(""+i, 1, 1, properties));
        }

        for (Task task : tasks) {
            task.setFollowWeightings(tasks, 0, 0.5);
        }

        for (int i=0;i<numWorkers;i++) {
            List<Double> properties = new ArrayList<>(RandUtil.randGaussians(numProperties, 1, 0.5));
//            List<Double> properties = new ArrayList<>();
//            properties.add(5.);
            workers.add(new Worker(""+i, properties, tasks, 0.5, 0.1));
        }

        for (int i=0;i<numItems;i++) {
            List<Double> properties = Util.ones(numProperties);
            System.out.println(properties);
            widgets.add(new Widget(""+i, 0, properties));
        }

//        System.out.println(tasks.size());
//        int a = 2;
//        System.out.println(RandUtil.sample(tasks, RandUtil.randInt(a, a+1)));
//        System.out.println(RandUtil.sample(tasks, RandUtil.randInt(a, a+1)));
//        System.out.println(RandUtil.sample(tasks, RandUtil.randInt(a, a+1)));
//        System.out.println(RandUtil.sample(tasks, RandUtil.randInt(a, a+1)));

        for (int d=0;d<=9;d++) {
            for (int i=0;i<10;i++) {
                TreeWorkflow workflow = new TreeWorkflow(tasks, workers, widgets, false, d, 3, 0.0, 0.5, 5, ProvUtil.factory);
                List<Execution> executions = workflow.getExecutions();
                for (int e = 0; e < executions.size(); e++) {
                    double utility = executions.get(e).getUtility();
                    System.out.println(utility);
                    ProvUtil.writeDocument("./test/" + d + "_" + i + "_" + e +"-"+utility+ ".svg", executions.get(e).getDocument());
                }
            }
        }
    }



    int maxDepth;
    int branchingFactor;
    double usedTaskProb;
    double isOrProb;
    TaskNode head;

    List<Task> tasks = new ArrayList<>();
    List<Worker> workers = new ArrayList<>();
    List<Widget> widgets = new ArrayList<>();

    int outputIndex = 1;

    public TreeWorkflow(
        List<Task> tasks,
        List<Worker> workers,
        List<Widget> widgets,
        boolean workerServiceAssignment,
        int maxDepth,
        int branchingFactor,
        double usedTaskProb,
        double isOrProb,
        int numExecutions,
        ProvFactory factory
    ) {
        super(numExecutions, factory);

        this.usedTaskProb = usedTaskProb;
        this.maxDepth = maxDepth;
        this.branchingFactor = branchingFactor;

        if (workerServiceAssignment) {
            this.workerMap = new HashMap<>();
            for (Task task : tasks) {
                workerMap.put(task, RandUtil.choose(workers));
            }
        }

        this.tasks = tasks;
        this.workers = workers;
        this.widgets = widgets;
        this.isOrProb = isOrProb;

        Task t = RandUtil.choose(tasks);
        head = new TaskNode(t, getWorker(t), RandUtil.randBoolean(isOrProb));
        List<Task> usedTasks = new ArrayList<>();
        usedTasks.add(t);
        generateWorkflow(head, 0, usedTasks);

        this.reset();
    }

    @Override
    public void reset() {
        this.document = factory.newDocument();
        document.setNamespace(ProvUtil.namespace);
        this.utility = 0.;
        this.executed = false;
        outputIndex = 1;
    }

    public void generateWorkflow(TaskNode current, int depth, List<Task> usedTasks) {
//        if (depth >= RandUtil.randInt(depth,maxDepth)) return;
//        if (depth >= maxDepth || RandUtil.randBoolean(0.25)) return;
//        System.out.println("(" + depth + "/" + maxDepth + ")" + StringUtils.repeat("\t", depth) + current);
        if (depth >= maxDepth) return;
//        List<Task> nexts = RandUtil.samplewr(tasks, RandUtil.randInt(1, branchingFactor+1));
        List<Task> nexts = getNextTasks(RandUtil.randInt(1, branchingFactor+1), this.usedTaskProb, usedTasks);
//        System.out.println(nexts);
        for (Task t : nexts) {
            boolean isOr = RandUtil.randBoolean(isOrProb);
            TaskNode next = new TaskNode(t, getWorker(t), isOr);
            current.addNexts(next);
            generateWorkflow(next, depth + 1, usedTasks);
        }
    }

    @Override
    public void execute() {
        if (!executed) this.reset();

//        TaskNode current = head;
        Stack<TaskNode> execStack = new Stack<>();
        execStack.push(head);
        Widget input = RandUtil.choose(widgets);
        List<Widget> inputs = new ArrayList<>();
        List<Widget> outputs = new ArrayList<>();
        inputs.add(input);

//        System.out.println(inputs);
        doTask(execStack, inputs, outputs);
//        System.out.println(outputs);
        for (Widget output : outputs) {
            utility += Util.mean(output.getProperties());
        }
//        utility /= (double)outputs.size();
//        utility = Util.mean(RandUtil.choose(outputs).getProperties());

//        System.out.println(utility);
//
//        ProvUtil.writeDocument("./test/"+Util.format(utility)+"_"+this.hashCode()+".png", this.document);
//        System.out.println("\n---\n");
    }

    public void doTask(Stack<TaskNode> execStack, List<Widget> inputs, List<Widget> allOutputs) {
        TaskNode current = execStack.peek();
        Worker worker = current.getWorker();

        List<Widget> outputs = new ArrayList<>();
        for (Widget input : inputs) {
            List<Widget> output = worker.process(current, input, execStack, outputIndex);
            outputs.addAll(output);
            for (Widget o : output) {
                updateProv(input, worker, current.getTask(), o);
                outputIndex++;
            }
        }

        allOutputs.addAll(outputs);
        allOutputs.removeAll(inputs);
//        System.out.println(outputIndex+" : "+current+" x "+worker+" : "+inputs+" -> "+outputs+" : "+allOutputs);

        if (current.isOr()) { //OR
            if (current.hasNext()) {
                execStack.push(current.getNext());
                doTask(execStack, outputs, allOutputs);
            }
        } else { //AND
            for (TaskNode next : current.getNexts()) {
                execStack.push(next);
                doTask(execStack, outputs, allOutputs);
            }
        }
        execStack.pop();
    }


    private List<Task> getNextTasks(int num, double usedTaskProb, List<Task> usedTasks) {
        List<Task> nexts = new ArrayList<>();
        for (int i=0;i<num;i++) {
            if (RandUtil.randBoolean(usedTaskProb)) {
//                System.out.println("PREUSED");
                Task t = RandUtil.choose(usedTasks);
                nexts.add(t);
                usedTasks.add(t);
            } else {
                Task t = RandUtil.choose(tasks);
//                System.out.println("NEW");
                nexts.add(t);
                usedTasks.add(t);
            }
        }
        return nexts;
    }

    Map<Task,Worker> workerMap = null;
    private Worker getWorker(Task task) {
        if (workerMap != null) return workerMap.get(task);
        else return RandUtil.choose(this.workers);
    }
}
