package jaspr.provfsm.epigenicssim;

import jaspr.provfsm.core.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EpigenomicsWorkflow extends Workflow {

    public static void main(String[] args) {

        RandUtil.init();
        ProvUtil.init();
        GraphUtil.init();

        int numWorkers = 10;

        List<IWorker> workers = new ArrayList<>();
        for (int i=0;i<numWorkers;i++) {
            workers.add(new Worker(""+i, RandUtil.randGaussians(1, 1, 0.5), EpigenomicsWorkflow.getTasks(), 0.5, 0.1));
        }

        List<IWidget> inputs = new ArrayList<>();
        Widget input = new Widget(""+0, 1);
        inputs.add(input);

        EpigenomicsWorkflow workflow = new EpigenomicsWorkflow(5, 10, 0.9, workers, inputs, true);
        List<Execution> executions = workflow.getExecutions();
        for (int e = 0; e < executions.size(); e++) {
            double utility = executions.get(e).getUtility();
            System.out.println(utility);
//            ProvUtil.writeDocument("./test/" + e +"-"+utility+ ".svg", executions.get(e).getDocument());
//            System.out.println(executions.get(e).getDocument());
        }

    }


    private static Map<String,Task> tasks = null;
    private static int numProperties = 1;

    public static Map<String,Task> getTaskMap(double taskStd) {
        if (tasks == null) {
            System.out.println("task std::: "+taskStd);
            tasks = new HashMap<>();
            tasks.put("fastQSplit", new Task("fastQSplit", RandUtil.randGaussians(numProperties, 1, taskStd)));
            tasks.put("filterContams", new Task("filterContams", RandUtil.randGaussians(numProperties, 1, taskStd)));
            tasks.put("sol2sanger", new Task("sol2sanger", RandUtil.randGaussians(numProperties, 1, taskStd)));
            tasks.put("fastq2bfq", new Task("fastq2bfq", RandUtil.randGaussians(numProperties, 1, taskStd)));
            tasks.put("map", new Task("map", RandUtil.randGaussians(numProperties, 1, taskStd)));
            tasks.put("mapMerge", new Task("mapMerge", RandUtil.randGaussians(numProperties, 1, taskStd)));
            tasks.put("maqIndex", new Task("maqIndex", RandUtil.randGaussians(numProperties, 1, taskStd)));
            tasks.put("pileup", new Task("pileup", RandUtil.randGaussians(numProperties, 1, taskStd)));
//            tasks.put("fastQSplit", new Task("fastQSplit", RandUtil.randDoubles(numProperties, 0, 1)));
//            tasks.put("filterContams", new Task("filterContams", RandUtil.randDoubles(numProperties, 0, 1)));
//            tasks.put("sol2sanger", new Task("sol2sanger", RandUtil.randDoubles(numProperties, 0, 1)));
//            tasks.put("fastq2bfq", new Task("fastq2bfq", RandUtil.randDoubles(numProperties, 0, 1)));
//            tasks.put("map", new Task("map", RandUtil.randDoubles(numProperties, 0, 0.5)));
//            tasks.put("mapMerge", new Task("mapMerge", RandUtil.randDoubles(numProperties, 0, 1)));
//            tasks.put("maqIndex", new Task("maqIndex", RandUtil.randDoubles(numProperties, 0, 1)));
//            tasks.put("pileup", new Task("pileup", RandUtil.randDoubles(numProperties, 0, 1)));
        }
        return tasks;
    }

    public static List<Task> getTasks(double taskStd) {
        return new ArrayList<>(getTaskMap(taskStd).values());
    }

    public static List<Task> getTasks() {
        return new ArrayList<>(getTaskMap(0.5).values());
    }

    public static Task getTask(String taskname) {
        return getTaskMap(0.5).get(taskname);
    }



    private double branchLikelihood;
    private int outputIndex;
    private int width;
    private List<IWorker> workers;
    private List<IWidget> inputs;
    private Map<Task,IWorker> workerMap;

    public EpigenomicsWorkflow(int numExecutions, int width, double branchLikelihood, List<IWorker> workers, List<IWidget> inputs, boolean workerServiceAssignment) {
        super(numExecutions);

        this.workers = workers;
        this.inputs = inputs;
        this.branchLikelihood = branchLikelihood;
        this.width = width;

        this.workerMap = new HashMap<>();
        if (workerServiceAssignment) {
            for (Task task : getTasks()) {
                workerMap.put(task, RandUtil.choose(workers));
            }
        }
//        System.out.println(workerMap);

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

    public List<Execution> getExecutions() {
        List<Execution> exs = super.getExecutions();
        this.executions = null;
        return exs;
    }

    public IWorker getWorker(Task task) {
        if (workerMap.containsKey(task)) {
            return workerMap.get(task);
        } else {
            return RandUtil.choose(workers);
        }
    }

    @Override
    public void execute() {
        if (!executed) this.reset();
//        System.out.println("---");

        int execWidth = RandUtil.randInt((int)(branchLikelihood*width), width+1);
        List<IWidget> outputs = doTask(inputs, getTask("fastQSplit"), getWorker(getTask("fastQSplit")), execWidth, null);

        List<IWidget> outputs2 = new ArrayList<>();
        for (int i=0;i<outputs.size();i++) {
            List<IWidget> ioutputs = doTask(outputs.subList(i,i+1), getTask("filterContams"), getWorker(getTask("filterContams")), 1, getWorker(getTask("fastQSplit")));
            ioutputs = doTask(ioutputs, getTask("sol2sanger"), getWorker(getTask("sol2sanger")), 1, getWorker(getTask("filterContams")));
            ioutputs = doTask(ioutputs, getTask("fastq2bfq"), getWorker(getTask("fastq2bfq")), 1, getWorker(getTask("sol2sanger")));
            ioutputs = doTask(ioutputs, getTask("map"), getWorker(getTask("map")), 1, getWorker(getTask("fastq2bfq")));
            outputs2.addAll(ioutputs);
        }

        outputs = doTask(outputs2, getTask("mapMerge"), getWorker(getTask("mapMerge")), 1, getWorker(getTask("map")));
        outputs = doTask(outputs, getTask("maqIndex"), getWorker(getTask("maqIndex")), 1, getWorker(getTask("mapMerge")));
        outputs = doTask(outputs, getTask("pileup"), getWorker(getTask("pileup")), 1, getWorker(getTask("maqIndex")));

        System.out.println(outputs);
        List<Double> utilities = new ArrayList<>();
        for (IWidget o : outputs) utilities.add(Util.mean(((Widget)o).getProperties()));
        this.utility = Util.mean(utilities);
    }

    List<IWidget> doTask(List<IWidget> inputs, Task task, IWorker worker, int numOutputs, IWorker prevWorker) {


        double followWeight = ((Worker)worker).followWeight(prevWorker);
//        double followWeight = 1;

        List<Double> utilities = new ArrayList<>();
        for (IWidget i : inputs) utilities.add(Util.mean(((Widget)i).getProperties()));
//        double inputUtility = Util.mean(utilities);
        double inputUtility = Util.max(utilities) * followWeight;

        double taskUtility = inputUtility + ((Worker)worker).meanCompetency(task);
        List<IWidget> outputs = new ArrayList<>();
        for (int i=0;i<numOutputs;i++) {
            outputs.add(new Widget(""+outputIndex++, taskUtility));
        }
        this.updateProv(inputs, worker, task, outputs);
//        System.out.println(inputs+" -> "+task+" x "+worker+" -> "+outputs);
        return outputs;
    }
}
