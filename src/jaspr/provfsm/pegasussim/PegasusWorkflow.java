//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.pegasussim;

import jaspr.provfsm.core.Execution;
import jaspr.provfsm.core.GraphUtil;
import jaspr.provfsm.core.ITask;
import jaspr.provfsm.core.IWidget;
import jaspr.provfsm.core.IWorker;
import jaspr.provfsm.core.ProvUtil;
import jaspr.provfsm.core.RandUtil;
import jaspr.provfsm.core.Util;
import jaspr.provfsm.core.Workflow;
import jaspr.provfsm.pegasussim.blocks.Block;
import jaspr.provfsm.pegasussim.blocks.Block.BlockType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PegasusWorkflow extends Workflow {
    List<Block> workflowBlocks;
    List<IWorker> workflowWorkers;
    double outputDivisorWeight;
    boolean singleBlockWorker;
    private int outputIndex;

    public static void main(String[] args) {
        RandUtil.init();
        ProvUtil.init();
        GraphUtil.init();

        int numWorkers = 5;
        int numTasks = 100;

        List<ITask> tasks = new ArrayList<>();
//        for (int i=0;i<numTasks;i++) {
//            Task task = new Task("" + i, RandUtil.randGaussians(1, 1, 0.5));
//            tasks.add(task);
//        }
        List<ITask> mineTasks = new ArrayList<>();
        mineTasks.add(new Task("MineOre", RandUtil.randGaussians(1, 1, 0.5)));
        List<ITask> assayTasks = new ArrayList<>();
        assayTasks.add(new Task("AssayOre", RandUtil.randGaussians(1, 1, 0.5)));
        List<ITask> ship1Tasks = new ArrayList<>();
        ship1Tasks.add(new Task("Shipping1", RandUtil.randGaussians(1, 1, 0.5)));
        List<ITask> ship2Tasks = new ArrayList<>();
        ship2Tasks.add(new Task("Shipping2", RandUtil.randGaussians(1, 1, 0.5)));
        List<ITask> refineTasks = new ArrayList<>();
        refineTasks.add(new Task("LowRefine", RandUtil.randGaussians(1, 1, 0.5)));
//        refineTasks.add(new Task("HighRefine", RandUtil.randGaussians(1, 1, 0.5)));

        tasks.addAll(mineTasks);
        tasks.addAll(ship1Tasks);
        tasks.addAll(ship2Tasks);
        tasks.addAll(assayTasks);
        tasks.addAll(refineTasks);

        List<IWorker> workers = new ArrayList<>();
        for (int i=0;i<numWorkers;i++) {
            workers.add(new Worker("Worker"+i, RandUtil.randGaussians(1, 1, 0.5), tasks, 0.5, 0.1, 0.1));
        }

//        List<Widget> inputs = new ArrayList<>();
//        Widget input = new Widget(""+0, 1);
//        inputs.add(input);

        // THIS BELOW LINE WILL NOT WORK UNTIL SIMPARAMS IS PROPERLY INSTANTIATED. I SUGGEST MAKING A SECOND CONSTRUCTOR.
//        for (int wc=1;wc<=5;wc++){
//            PegasusWorkflow workflow = new PegasusWorkflow(5, tasks, workers, wc);
//            List<Execution> executions = workflow.getExecutions();
//            for (int e = 0; e < executions.size(); e++) {
//                double utility = executions.get(e).getUtility();
//                System.out.println(utility);
//                ProvUtil.writeDocument("./test/" + wc+"-"+e+".svg", executions.get(e).getDocument());
////            System.out.println(executions.get(e).getDocument());
//            }
//        }


        PegasusWorkflow workflow = new PegasusWorkflow(5, mineTasks, assayTasks, ship1Tasks, refineTasks, ship2Tasks, workers);
        List<Execution> executions = workflow.getExecutions();

        for(int e = 0; e < executions.size(); ++e) {
            double utility = ((Execution)executions.get(e)).getUtility();
            System.out.println(utility);
            ProvUtil.writeDocument("./test/EXAMPLE-"+e+".svg", executions.get(e).getDocument());
//            System.out.println(executions.get(e).getDocument());
        }

    }

    public double getOutputDivisorWeight() {
        return this.outputDivisorWeight;
    }

    public boolean isSingleBlockWorker() {
        return this.singleBlockWorker;
    }

    public PegasusWorkflow(int numExecutions, List<ITask> workflowTasks, List<IWorker> workflowWorkers, SimParams p) {
        super(numExecutions);
        this.outputDivisorWeight = p.taskOutputDivisorWeight;
        this.singleBlockWorker = p.singleBlockWorker;
        this.workflowWorkers = workflowWorkers;
        this.workflowBlocks = new ArrayList();

        Block current = null;
        while (current == null){
            current = Block.init(RandUtil.choose(Block.BlockType.values()), RandUtil.randInt(1, p.workflowComplexity), workflowTasks);
        }

        while(current != null) {
            this.workflowBlocks.add(current);
            current = current.getNext((BlockType)RandUtil.choose(BlockType.values()), RandUtil.randInt(1, p.workflowComplexity), workflowTasks);
        }

    }

    public PegasusWorkflow(int numExecutions, List<ITask> workflowTasks, List<IWorker> workflowWorkers) {
        super(numExecutions);
        this.outputDivisorWeight = 0.;

        this.singleBlockWorker = false;
        this.workflowWorkers = workflowWorkers;

        this.workflowBlocks = new ArrayList<>();

        Block current = Block.init(Block.BlockType.Pipline, 1, workflowTasks);
        workflowBlocks.add(current);
        current = current.getNext(Block.BlockType.Distribution, 2, workflowTasks);
        workflowBlocks.add(current);
        current = current.getNext(Block.BlockType.Redistribution, 1, workflowTasks);
        workflowBlocks.add(current);
        current = current.getNext(Block.BlockType.Aggregation, 1, workflowTasks);
        workflowBlocks.add(current);
    }


    public PegasusWorkflow(int numExecutions, List<ITask> mineTasks, List<ITask> assayTasks, List<ITask> ship1Tasks, List<ITask> refineTasks, List<ITask> ship2Tasks, List<IWorker> workflowWorkers) {
        super(numExecutions);
        this.outputDivisorWeight = 0.;

        this.singleBlockWorker = false;
        this.workflowWorkers = workflowWorkers;

        this.workflowBlocks = new ArrayList<>();

        Block current = Block.init(Block.BlockType.Distribution, 2, mineTasks);
        workflowBlocks.add(current);
        Block one = current;
        current = one.getNext(Block.BlockType.Pipline, 1, assayTasks);
        workflowBlocks.add(current);
        current = one.getNext(Block.BlockType.Aggregation, 1, ship1Tasks);
        workflowBlocks.add(current);
        current = current.getNext(Block.BlockType.Pipline, 1, refineTasks);
        workflowBlocks.add(current);
        current = current.getNext(Block.BlockType.Pipline, 1, ship2Tasks);
        workflowBlocks.add(current);
    }

    public PegasusWorkflow(int numExecutions, List<ITask> workflowTasks, List<IWorker> workflowWorkers, int workflowComplexity) {
        super(numExecutions);
        this.outputDivisorWeight = 0.;

        this.singleBlockWorker = false;
        this.workflowWorkers = workflowWorkers;

        this.workflowBlocks = new ArrayList<>();

        Block current = null;
        while (current == null){
            current = Block.init(RandUtil.choose(Block.BlockType.values()), workflowComplexity, workflowTasks);
        }
        while (current != null) {
            workflowBlocks.add(current);
            current = current.getNext(RandUtil.choose(Block.BlockType.values()), workflowComplexity, workflowTasks);
        }
    }

    public PegasusWorkflow(int numExecutions, List<ITask> workflowTasks, List<IWorker> workflowWorkers, TrainTestParams p) {
        super(numExecutions);
        this.outputDivisorWeight = p.taskOutputDivisorWeight;
        this.singleBlockWorker = p.singleBlockWorker;
        this.workflowWorkers = workflowWorkers;
        this.workflowBlocks = new ArrayList();

        Block current;
        for(current = null; current == null; current = Block.init((BlockType)RandUtil.choose(BlockType.values()), RandUtil.randInt(1, p.workflowComplexity), workflowTasks)) {
        }

        while(current != null) {
            this.workflowBlocks.add(current);
            current = current.getNext((BlockType)RandUtil.choose(BlockType.values()), RandUtil.randInt(1, p.workflowComplexity), workflowTasks);
        }

    }

    public int nextOutputIndex() {
        return this.outputIndex++;
    }

    public void reset() {
        this.document = this.factory.newDocument();
        this.document.setNamespace(ProvUtil.namespace);
        this.utility = 0.0D;
        this.executed = false;
        this.outputIndex = 1;
    }

    public void execute() {
        List<IWidget> inputs = new ArrayList();
        List<IWidget> outputs = null;

        for(int i = 0; i < ((Block)this.workflowBlocks.get(0)).numInputs(); ++i) {
            ((List)inputs).add(new Widget("IN", 1.0D));
        }

        Set<IWorker> prevWorkers = new HashSet();

        for(int i = 0; i < this.workflowBlocks.size(); ++i) {
            Block block = (Block)this.workflowBlocks.get(i);
            List<IWorker> blockWorkers = RandUtil.samplewr(this.workflowWorkers, block.numTasks());
            System.out.println(block);
            outputs = block.doTasks(this, (List)inputs, blockWorkers, prevWorkers);
            inputs = outputs;
        }

        List<Double> utilities = new ArrayList();

        for (IWidget o : outputs) utilities.add(Util.mean(((Widget)o).getProperties()));

        this.utility = Util.max(utilities);
        System.out.println("---");
    }
}
