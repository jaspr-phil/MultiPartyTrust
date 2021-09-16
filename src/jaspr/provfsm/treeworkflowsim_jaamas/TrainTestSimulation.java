package jaspr.provfsm.treeworkflowsim_jaamas;

import de.parsemis.graph.Graph;
import it.uniroma2.sag.kelp.data.representation.graph.DirectedGraphRepresentation;
import jaspr.provfsm.core.*;
import jaspr.provfsm.designersim.workflows.Designer;
import jaspr.provfsm.models.GraphKernelModel;
import jaspr.provfsm.models.Model;
import jaspr.provfsm.models.NodeNamer;
import jaspr.provfsm.treeworkflowsim_jaamas.workflows.Task;
import jaspr.provfsm.treeworkflowsim_jaamas.workflows.TreeWorkflow;
import jaspr.provfsm.treeworkflowsim_jaamas.workflows.Widget;
import jaspr.provfsm.treeworkflowsim_jaamas.workflows.Worker;
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
                            "--trainWorkflows 900 " +
                            "--minExecObs 1 " +
                            "--maxExecObs 1 " +
                            "--totTasks 250 " +
                            "--totWorkers 250 " +
                            "--workerStd 0.5 " +
                            "--taskStd 0.25 " +
                            "--proficiencyStd 0.5 " +
                            "--followStd 0.5 " +
                            "--followMean 0 " +
                            "--execStd 0.01 " +
                            "--minTasks 7 " +
                            "--maxTasks 7 " +
                            "--minWorkers 7 " +
                            "--maxWorkers 7 " +
                            "--minDepth 5 " +
                            "--maxDepth 5 " +
                            "--minBranchingFactor 3 " +
                            "--maxBranchingFactor 3 " +
                            "--isOrLikelihood 0.5 " +
                            "--repeatTaskLikelihood 0. " +
                            "--minExecutions 1 " +
                            "--maxExecutions 1 " +
                            "--models \"" +
                            "mean," +
                            "simple-prefix," +
                            "context-prefix," +
//                            "context-full-50," +
//                            "context-prefix-200," +
//                            "context-prefix-300," +
//                            "context-prefix-400," +
//                            "context-prefix-500," +
//                            "context-prefix," +
                            "gspan-full-500," +
//                            "kernel-full," +
                            "\" " +
                            "--gspanSupport 0.025 " +
                            "--maxGspanSupport 1 " +
                            "--gspanMinNodes 2 " +
                            "--gspanMaxNodes 25 " +
                            "--totItems 1 " +
                            "--minItems 1 " +
                            "--maxItems 1 " +
                            "--binaryUtilities false " +
                            "--workerServiceAssignment false "
            ).split(" ");
        }

        jaspr.provfsm.treeworkflowsim_jaamas.TrainTestParams params = new TrainTestParams(args);

        jaspr.provfsm.treeworkflowsim_jaamas.TrainTestSimulation simulation = new jaspr.provfsm.treeworkflowsim_jaamas.TrainTestSimulation(params);

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

    public List<Task> tasks;
    public List<Worker> workers;
    public List<Widget> widgets;

    public void init() {
        int numTasks = p.totTasks;
        int numItems = p.totItems;
        int numWorkers = p.totWorkers;

        int numProperties = 1;

        this.tasks = new ArrayList<>();
        this.workers = new ArrayList<>();
        this.widgets = new ArrayList<>();

        for (int i=0;i<numTasks;i++) {
            List<Double> properties = new ArrayList<>(RandUtil.randGaussians(numProperties, 1, p.taskStd));
            tasks.add(new Task(""+i, 1, 1, properties));
        }

        for (Task task : tasks) {
            task.setFollowWeightings(tasks, p.followMean, p.followStd);
        }

        for (int i=0;i<numWorkers;i++) {
            List<Double> properties = new ArrayList<>(RandUtil.randGaussians(numProperties, 1, p.workerStd));
            workers.add(new Worker(""+i, properties, tasks, p.proficiencyStd, p.execStd));
        }

        for (int i=0;i<numItems;i++) {
            List<Double> properties = Util.ones(numProperties);
            System.out.println(properties);
            widgets.add(new Widget(""+i, 0, properties));
        }

    }

    public TreeWorkflow newWorkflow() {
        int maxDepth = RandUtil.randInt(p.minDepth, p.maxDepth+1);
        int branchingFactor = RandUtil.randInt(p.minBranchingFactor, p.maxBranchingFactor+1);
        int numExecutions = RandUtil.randInt(p.minExecutions, p.maxExecutions+1);

        List<Task> taskPalette = RandUtil.sample(tasks, RandUtil.randInt(p.minTasks, p.maxTasks+1));
        List<Widget> widgetPalette = RandUtil.sample(widgets, RandUtil.randInt(p.minItems, p.maxItems+1));
        List<Worker> workerPalette = RandUtil.sample(workers, RandUtil.randInt(p.minWorkers, p.maxWorkers+1));


        return new TreeWorkflow(taskPalette, workerPalette, widgetPalette, p.workerServiceAssignment,
                maxDepth, branchingFactor, p.repeatTaskLikelihood, p.isOrLikelihood, numExecutions, ProvUtil.factory);
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
            TreeWorkflow workflow = newWorkflow();
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
            TreeWorkflow workflow = newWorkflow();
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
