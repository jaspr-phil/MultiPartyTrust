package jaspr.provfsm.pegasussim;

import de.parsemis.graph.Graph;
import it.uniroma2.sag.kelp.data.representation.graph.DirectedGraphRepresentation;
import jaspr.provfsm.core.*;
import jaspr.provfsm.models.GraphKernelModel;
import jaspr.provfsm.models.Model;
import jaspr.provfsm.models.NodeNamer;
import weka.classifiers.Evaluation;

import java.util.*;
import java.util.stream.Collectors;

public class TrainTestSimulation {

    public static void main(String[] args) throws Exception {

        RandUtil.init();
        ProvUtil.init();
        GraphUtil.init();

        if (args.length == 0) {
            args = (
                            "--testWorkflows 100 " +
                            "--trainWorkflows 400 " +
                            "--minExecObs 1 " +
                            "--maxExecObs 1 " +
                            "--minExecutions 1 " +
                            "--maxExecutions 1 " +
                            "--workerServiceAssignment false " +
                            "--singleBlockWorker false " +
                            "--workflowComplexity 2 " +
                            "--totWorkers 100 " +
                            "--totTasks 100 " +
                            "--workflowTasks 100 " +
                            "--workflowWorkers 100 " +
                            "--taskOutputDivisorWeight 0. " +
                            "--workerLeaveProb 0.01 " +
                            "--workerStd 0.5 " +
                            "--taskStd 0.5 " +
                            "--proficiencyStd 0.25 " +
                            "--followStd 0. " +
                            "--execStd 0.05 " +
                            "--models \"mean," +
                            "agg-prefix," +
                            "contextagg-prefix," +
//                              "random," +
                            "simple-prefix," +
                            "simple-full," +
////                            "context-full-100," +
//                            "context-full-500," +
                            "context-prefix," +
//                            "gspan-prefix-100," +
////                            "gspan-prefix-500," +
//                            "gspan-prefix-1000," +
//                            "gspan-full-100," +
//                            "gspan-full-500," +
                            "gspan-full-1000," +
                            "kernel-full," +
//                            "kernel-prefix," +
//                            "kernel-prefix," +
                            "\" " +
                            "--gspanSupport 0.05 " +
                            "--maxGspanSupport 1 " +
                            "--gspanMinNodes 2 " +
                            "--gspanMaxNodes 25 " +
                            "--binaryUtilities false"
            ).split(" ");
        }

        TrainTestParams params = new TrainTestParams(args);

        TrainTestSimulation simulation = new TrainTestSimulation(params);

        simulation.init();
        simulation.run();
        simulation.printResults();

        System.out.println(Util.format(params.args));
    }

    class Result {
        int index;
        private List<Integer> numExecutions = new ArrayList<Integer>();
        private List<Double> observedWorkflowUtilities = new ArrayList<>();
        private Map<Model,List<Double>> predictedWorkflowUtilities = new LinkedHashMap<>();
        private List<Double> observedExecutionUtilities = new ArrayList<>();
        private Map<Model,List<Double>> predictedExecutionUtilities = new LinkedHashMap<>();
        public Result(int index) {
            this.index = index;
        }

        public void addObservedWorkflowUtility(double utility) {
            this.observedWorkflowUtilities.add(utility);
        }
        public void addPredictedWorkflowUtility(Model model, double utility) {
            if (!this.predictedWorkflowUtilities.containsKey(model)) {
                this.predictedWorkflowUtilities.put(model, new ArrayList<>());
            }
            this.predictedWorkflowUtilities.get(model).add(utility);
        }

        public void addNumExecutions(int numExecutions) {
            this.numExecutions.add(numExecutions);
        }

        public void addObservedExecutionUtility(double utility) {
            this.observedExecutionUtilities.add(utility);
        }
        public void addPredictedExecutionUtility(Model model, double utility) {
            if (!this.predictedExecutionUtilities.containsKey(model)) {
                this.predictedExecutionUtilities.put(model, new ArrayList<>());
            }
            this.predictedExecutionUtilities.get(model).add(utility);
        }

        public String toString() {
//            StringBuilder r = new StringBuilder("Result: ").append(index).append(" : ");
            StringBuilder r = new StringBuilder();
            r.append(Util.format(this.observedWorkflowUtilities)).append("\t")
                    .append(Util.format(this.observedExecutionUtilities))
                    .append("\tObserved");
            for (Model model : models) {
                r.append("\n")
                        .append(Util.format(this.predictedWorkflowUtilities.get(model))).append("\t")
                        .append(Util.format(this.predictedExecutionUtilities.get(model)))
                        .append("\t").append(model.name());
            }
            return r.toString();
        }
    }

    TrainTestParams p;
    List<Model> models;
    Set<NodeNamer> nodeNamers;
    private List<Result> results;

    TrainTestSimulation(TrainTestParams params) {
        this.p = params;

        this.models = p.makeModels();
        this.nodeNamers = new HashSet<>();
        for (Model model : models) {
            nodeNamers.add(model.getNodeNamer());
        }

        this.results = new ArrayList<>();
    }

    private List<IWorker> workers;
    private List<IWidget> inputs;
    private List<ITask> tasks;
    int numProperties = 1;
    int nextWorkerId = 1;


    public void init() {
        int numTasks = p.totTasks;
        int numWorkers = p.totWorkers;

        int numProperties = 1;

        this.workers = new ArrayList<>();
        this.inputs = new ArrayList<>();
        this.tasks = new ArrayList<>();


        for (int i=0;i<numTasks;i++) {
            Task task = new Task("" + i, RandUtil.randGaussians(numProperties, 1, p.taskStd));
//            Task task = new Task("" + i, RandUtil.randDoubles(numProperties, p.taskStd==0?1:0, p.taskStd==0?1:p.taskStd));
            this.tasks.add(task);
        }

        System.out.println(tasks);
        for (int i=0;i<numWorkers;i++) {
            List<Double> properties = RandUtil.randGaussians(numProperties, 1, p.workerStd);
//            List<Double> properties = RandUtil.randDoubles(numProperties, 0, p.workerStd);
//            System.out.println(properties);
//            List<Double> properties = RandUtil.randDoubles(numProperties, 0, 10);
            workers.add(new Worker(""+nextWorkerId, properties, tasks, p.proficiencyStd, p.execStd, p.followStd));
            nextWorkerId ++ ;
            System.out.println(workers.get(i));
        }

    }

    public PegasusWorkflow newWorkflow() {
        List<ITask> workflowTasks = RandUtil.sample(tasks, p.workflowTasks);
        List<IWorker> workflowWorkers = RandUtil.sample(workers, p.workflowWorkers);

        int numExecutions = RandUtil.randInt(p.minExecutions,p.maxExecutions+1);
        System.out.println(numExecutions);
        return new PegasusWorkflow(
                numExecutions,
                workflowTasks,
                workflowWorkers,
                p
        );
    }

    void run() throws Exception {

        Map<NodeNamer,List<Graph<String,String>>> graphs = new HashMap<>();
        Map<NodeNamer,List<DirectedGraphRepresentation>> dirgraphs = new HashMap<>();
        List<Double> trainUtilities = new ArrayList<>();

        for (NodeNamer nodeNamer : nodeNamers) {
            graphs.put(nodeNamer, new ArrayList<>());
            dirgraphs.put(nodeNamer, new ArrayList<>());
        }

        boolean needsGraph = models.stream().anyMatch(m -> !(m instanceof GraphKernelModel));
        boolean needsDGR = models.stream().anyMatch(m -> m instanceof GraphKernelModel);

        for (int w = 0; w < p.trainWorkflows; w++) {
            PegasusWorkflow workflow = newWorkflow();
            List<Execution> executions = workflow.getExecutions();
//            for (Execution ex : RandUtil.sample(executions, p.trainExecObs)) {
//                for (NodeNamer nodeNamer : this.nodeNamers) {
//                    if (needsGraph) graphs.get(nodeNamer).add(GraphUtil.makeGraph(ex.getDocument(), nodeNamer));
//                    if (needsDGR) dirgraphs.get(nodeNamer).add(GraphKernelModel.makeGraph(ex.getDocument(), nodeNamer));
//                }
//                trainUtilities.add(ex.getUtility());
//            }
            int numObs = RandUtil.randInt(p.minExecObs,p.maxExecObs+1);
            for (int i=0;i<numObs;i++) {
                Execution ex = RandUtil.choose(executions);
                for (NodeNamer nodeNamer : this.nodeNamers) {
                    if (needsGraph) graphs.get(nodeNamer).add(GraphUtil.makeGraph(ex.getDocument(), nodeNamer));
                    if (needsDGR) dirgraphs.get(nodeNamer).add(GraphKernelModel.makeGraph(ex.getDocument(), nodeNamer));
                }
                trainUtilities.add(ex.getUtility());
            }
        }

        System.out.println("Training size: " + trainUtilities.size());
        for (Model model : models) {
            model.forget();
            if (model instanceof GraphKernelModel) {
                ((GraphKernelModel) model).learnFromDGR(dirgraphs.get(model.getNodeNamer()), trainUtilities);
            } else {
                model.learn(graphs.get(model.getNodeNamer()), trainUtilities);
            }
//            model.learnFromProv(trainDocuments, trainUtilities);
        }

        for (int w = 0; w < p.testWorkflows; w++) {
            PegasusWorkflow workflow = newWorkflow();
            Result result = new Result(w);

            double observedWorkflowUtility = assessWorkflow(workflow.getExecutions(), result);
            result.addObservedWorkflowUtility(observedWorkflowUtility);

            for (Model model : models) {
                double predictedWorkflowUtility = assessWorkflow(workflow.getExecutions(), model, result);
                result.addPredictedWorkflowUtility(model, predictedWorkflowUtility);
            }

            results.add(result);
            System.out.println(result);
        }

    }

    public double assessWorkflow(List<Execution> executions, Result result) {
        List<Double> executionUtilities = new ArrayList<>();
        for (Execution ex : executions) {
            executionUtilities.add(ex.getUtility());
            result.addObservedExecutionUtility(ex.getUtility());
        }
        result.addNumExecutions(executionUtilities.size());
        return Util.mean(executionUtilities);
    }

    public double assessWorkflow(List<Execution> executions, Model model, Result result) throws Exception {
        List<Double> executionUtilities = new ArrayList<>();
        for (Execution ex : executions) {
            double pred = model.predict(ex.getDocument());
            executionUtilities.add(pred);
            result.addPredictedExecutionUtility(model, pred);
        }
        return Util.mean(executionUtilities);
    }


    public StringBuilder evaluationsToString(Map<Model,Evaluation> evaluations) throws Exception {
        StringBuilder evalStr = new StringBuilder("\tR2\t\tMAE\t\tRMSE\n");
        for (Model model : this.models) {
            Evaluation eval = evaluations.get(model);
            evalStr.append(Util.format(eval)).append("\t\t").append(model.name()).append("\n");
        }
        return evalStr;
    }

    void printResults() throws Exception {
        System.out.println(results.stream().flatMap(r -> r.observedExecutionUtilities.stream()).mapToDouble(x -> x).summaryStatistics());
        System.out.println(evaluationsToString(Util.evaluateAll(
                Util.flatGroupMapKeys(results.stream().map(r -> r.predictedExecutionUtilities).collect(Collectors.toList())),
                results.stream().flatMap(r -> r.observedExecutionUtilities.stream()).collect(Collectors.toList()),
                p.binaryUtilities
        )));

        System.out.println(results.stream().flatMap(r -> r.observedWorkflowUtilities.stream()).mapToDouble(x -> x).summaryStatistics());
        System.out.println(evaluationsToString(Util.evaluateAll(
                Util.flatGroupMapKeys(results.stream().map(r -> r.predictedWorkflowUtilities).collect(Collectors.toList())),
                results.stream().flatMap(r -> r.observedWorkflowUtilities.stream()).collect(Collectors.toList()),
                p.binaryUtilities
        )));

    }

}
