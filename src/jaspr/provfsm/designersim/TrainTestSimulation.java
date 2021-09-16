package jaspr.provfsm.designersim;

import de.parsemis.graph.Graph;
import it.uniroma2.sag.kelp.data.representation.graph.DirectedGraphRepresentation;
import jaspr.provfsm.core.*;
import jaspr.provfsm.models.GraphKernelModel;
import jaspr.provfsm.models.Model;
import jaspr.provfsm.models.NodeNamer;
import jaspr.provfsm.designersim.workflows.*;
import jaspr.provfsm.treeworkflowsim.workflows.Task;
import jaspr.provfsm.treeworkflowsim.workflows.TreeWorkflow;
import jaspr.provfsm.treeworkflowsim.workflows.Widget;
import jaspr.provfsm.treeworkflowsim.workflows.Worker;
import weka.classifiers.Evaluation;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by phil on 25/05/18.
 */
public class TrainTestSimulation {

    public static void main(String[] args) throws Exception {

        RandUtil.init();
        ProvUtil.init();
        GraphUtil.init();

        if (args.length == 0) {
            args = (
                    "--testDesigners 50 " +
                    "--testPortfolioSize 50 " +
                    "--trainDesigners 1 " +
                    "--trainPortfolioSize 500 " +
                    "--totTasks 100 " +
                    "--totWorkers 100 " +
                    "--workerStd 0.5 " +
                    "--taskStd 0.5 " +
                    "--proficiencyStd 0.5 " +
                    "--followStd 0.5 " +
                    "--execStd 0.01 " +
                "--designerMinTasks 0 " +
                "--designerMaxTasks 0 " +
                "--designerMinWorkers 0 " +
                "--designerMaxWorkers 0 " +
                "--designerMinDepth 3 " +
                "--designerMaxDepth 3 " +
                "--designerMinBranchingFactor 0 " +
                "--designerMaxBranchingFactor 0 " +
                "--designerMinExecutions 1 " +
                "--designerMaxExecutions 1 " +
                    "--models \"" +
                            "mean," +
                            "simple-prefix," +
//                            "context-prefix-50," +
//                            "context-prefix-100," +
//                            "context-prefix-200," +
//                            "context-prefix-300," +
//                            "context-prefix-400," +
//                            "context-prefix-500," +
//                            "context-prefix," +
                            "gspan-full-500," +
//                            "kernel-full," +
                    "\" " +
                    "--gspanSupport 0.01 " +
                    "--maxGspanSupport 1 " +
                    "--gspanMinNodes 2 " +
                    "--gspanMaxNodes 5 " +
                    "--totItems 1 " +
                    "--designerMinItems 1 " +
                    "--designerMaxItems 1 " +
                    "--binaryUtilities false " +
                    "--workerServiceAssignment false "
            ).split(" ");
        }

        TrainTestParams params = new TrainTestParams(args);

        TrainTestSimulation simulation = new TrainTestSimulation(params);

        simulation.init();
        simulation.run();
        simulation.printResults();

        System.out.println(Util.format(params.args));
    }

    TrainTestParams p;
    List<Model> models;
    Set<NodeNamer> nodeNamers;

    class Result {
        int index;
        public String designer;
        private double observedTrust = Double.MIN_VALUE;
        private Map<Model,Double> predictedTrust = new LinkedHashMap<>();
        private List<Integer> numExecutions = new ArrayList<Integer>();
        private List<Double> observedWorkflowUtilities = new ArrayList<>();
        private Map<Model,List<Double>> predictedWorkflowUtilities = new LinkedHashMap<>();
        private List<Double> observedExecutionUtilities = new ArrayList<>();
        private Map<Model,List<Double>> predictedExecutionUtilities = new LinkedHashMap<>();
        public Result(int index, String designer) {
            this.index = index;
            this.designer = designer;
        }

        public void setObservedTrust(double utility) {
            this.observedTrust = utility;
        }
        public void setPredictedTrust(Model model, double utility) {
            this.predictedTrust.put(model, utility);
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

        public List<Double> absErrorTrust() {
            List<Double> ae = new ArrayList<>();
            for (Model model : models) {
                ae.add(Math.abs(observedTrust - predictedTrust.get(model)));
            }
            return ae;
        }

        public String toString() {
            StringBuilder r = new StringBuilder("Result: ").append(index).append(" : ").append(designer).append(" : ");
            r.append(Util.format(observedTrust)).append(" : ")
                    .append(Util.format(predictedTrust.values())).append(" : ")
                    .append(Util.format(this.absErrorTrust()));
            r.append("\nWorkflow: Observed : ").append(Util.format(this.observedWorkflowUtilities));
            for (Model model : models) {
                r.append("\nWorkflow: ").append(model.name()).append(" : ")
                        .append(Util.format(this.predictedWorkflowUtilities.get(model)));
            }
            r.append("\nExecution: Observed : ").append(Util.format(this.observedExecutionUtilities));
            for (Model model : models) {
                r.append("\nExecution: ").append(model.name()).append(" : ")
                        .append(Util.format(this.predictedExecutionUtilities.get(model)));
            }
            return r.toString();
        }
    }

    private List<Result> results;


    public TrainTestSimulation(TrainTestParams p) {
        this.p = p;

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
            task.setFollowWeightings(tasks, 0, p.followStd);
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

    public void run() throws Exception {

        Map<NodeNamer,List<Graph<String,String>>> graphs = new HashMap<>();
        Map<NodeNamer,List<DirectedGraphRepresentation>> dirgraphs = new HashMap<>();
        List<Double> trainUtilities = new ArrayList<>();

        for (NodeNamer nodeNamer : nodeNamers) {
            graphs.put(nodeNamer, new ArrayList<>());
            dirgraphs.put(nodeNamer, new ArrayList<>());
        }

        boolean needsGraph = models.stream().anyMatch(m -> !(m instanceof GraphKernelModel));
        boolean needsDGR = models.stream().anyMatch(m -> m instanceof GraphKernelModel);

        for (int d = 0; d < p.trainDesigners; d++) {
            Designer designer = newDesigner();
            System.out.println("Designer " + d + " (" + designer + ")...");

            List<TreeWorkflow> portfolio = designer.designPortfolio(p.trainPortfolioSize);
            for (TreeWorkflow workflow : portfolio) {
                List<Execution> executions = workflow.getExecutions();
                for (Execution ex : executions) {
                    for (NodeNamer nodeNamer : this.nodeNamers) {
                        if (needsGraph) graphs.get(nodeNamer).add(GraphUtil.makeGraph(ex.getDocument(), nodeNamer));
                        if (needsDGR) dirgraphs.get(nodeNamer).add(GraphKernelModel.makeGraph(ex.getDocument(), nodeNamer));
                    }
                    trainUtilities.add(ex.getUtility());
                }
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

        for (int d=0;d<p.testDesigners;d++) {

            Designer designer = newDesigner();
            Result result = new Result(d, designer.toString());

            List<TreeWorkflow> portfolio = designer.designPortfolio(p.testPortfolioSize);

            assessPortfolio(portfolio, result);
//            double actualTrust = assessTrust(portfolio, result);
//            result.setObservedTrust(actualTrust);
//
//            for (Model model : models) {
//                double estimatedTrust = assessTrust(portfolio, model, result);
//                result.setPredictedTrust(model, estimatedTrust);
//            }

            results.add(result);
            System.out.println(result);
        }
    }

    public void assessPortfolio(List<TreeWorkflow> portfolio, Result result) throws Exception {
        List<Double> observedUtilities = new ArrayList<>();
        Map<Model,List<Double>> predictedUtilities = new HashMap<>();
        for (Model model : models) {
            predictedUtilities.put(model, new ArrayList<>());
        }

        for (int i=portfolio.size()-1;i>=0;i--) {
            List<Execution> executions = portfolio.remove(i).getExecutions();

            double observedUtility = assessWorkflow(executions, result);
            result.addObservedWorkflowUtility(observedUtility);
            observedUtilities.add(observedUtility);

            for (Model model : models) {
                double predictedUtility = assessWorkflow(executions, model, result);
                result.addPredictedWorkflowUtility(model, predictedUtility);
                predictedUtilities.get(model).add(predictedUtility);
            }

        }

        result.setObservedTrust(Util.mean(observedUtilities));
        for (Model model : models) {
            result.setPredictedTrust(model, Util.mean(predictedUtilities.get(model)));
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


//    public double assessTrust(List<TreeWorkflow> portfolio, Result result) {
//        List<Double> workflowUtilities = new ArrayList<>();
//        for (TreeWorkflow workflow : portfolio) {
//            List<Double> executionUtilities = new ArrayList<>();
//            for (Execution ex : workflow.getExecutions()) {
//                executionUtilities.add(ex.getUtility());
//                result.addObservedExecutionUtility(ex.getUtility());
//            }
//            double workflowUtility = Util.mean(executionUtilities);
//            result.addNumExecutions(executionUtilities.size());
//            result.addObservedWorkflowUtility(workflowUtility);
//            workflowUtilities.add(workflowUtility);
//        }
//        return Util.mean(workflowUtilities);
//    }
//
//
//    public double assessTrust(List<TreeWorkflow> portfolio, Model model, Result result) throws Exception {
//        List<Double> workflowUtilities = new ArrayList<>();
//        for (TreeWorkflow workflow : portfolio) {
//            List<Double> estimatedUtilities = new ArrayList<>();
//            for (Execution ex : workflow.getExecutions()) {
//                double estimatedUtility = model.predict(ex.getDocument());
//                result.addPredictedExecutionUtility(model, estimatedUtility);
//                estimatedUtilities.add(estimatedUtility);
//            }
//            double workflowUtility = Util.mean(estimatedUtilities);
//            result.addPredictedWorkflowUtility(model, workflowUtility);
//            workflowUtilities.add(workflowUtility);
//        }
//        return Util.mean(workflowUtilities);
//    }


    public Designer newDesigner() {
        return new Designer(
                RandUtil.sample(tasks,p.designerMaxTasks > 0 ?  RandUtil.randInt(p.designerMinTasks,p.designerMaxTasks+1) : this.tasks.size()),
                RandUtil.sample(workers,p.designerMaxWorkers > 0 ? RandUtil.randInt(p.designerMinWorkers,p.designerMaxWorkers+1) : this.workers.size()),
                RandUtil.sample(widgets,p.designerMaxItems > 0 ? RandUtil.randInt(p.designerMinItems,p.designerMaxItems+1) : this.widgets.size()),
                RandUtil.randInt(p.designerMinDepth,p.designerMaxDepth+1),
                RandUtil.randInt(p.designerMinBranchingFactor,p.designerMaxBranchingFactor+1),
                RandUtil.randInt(p.designerMinExecutions,p.designerMaxExecutions+1),
                p.workerServiceAssignment
        );

//        return new Designer(
//                RandUtil.sample(
//                        tasks.stream().sorted().limit(tasks.size()/2).collect(Collectors.toList()),
//                        p.designerMaxTasks > 0 ?  RandUtil.randInt(p.designerMinTasks,p.designerMaxTasks+1) : this.tasks.size()
//                ),
//                RandUtil.sample(workers.stream().sorted().limit(workers.size()/2).collect(Collectors.toList()),
//                        p.designerMaxWorkers > 0 ? RandUtil.randInt(p.designerMinWorkers,p.designerMaxWorkers+1) : this.workers.size()
//                ),
//                RandUtil.sample(widgets,p.designerMaxItems > 0 ? RandUtil.randInt(p.designerMinItems,p.designerMaxItems+1) : this.widgets.size()),
//                RandUtil.randInt(p.designerMinDepth,p.designerMaxDepth+1),
//                RandUtil.randInt(p.designerMinBranchingFactor,p.designerMaxBranchingFactor+1),
//                RandUtil.randInt(p.designerMinExecutions,p.designerMaxExecutions+1)
//        );
    }

    public StringBuilder evaluationsToString(Map<Model,Evaluation> evaluations) throws Exception {
        StringBuilder evalStr = new StringBuilder("\tR2\t\tMAE\t\tRMSE\n");
        for (Model model : this.models) {
            Evaluation eval = evaluations.get(model);
            evalStr.append(Util.format(eval)).append("\t\t").append(model.name()).append("\n");
        }
        return evalStr;
    }

    public void printResults() throws Exception {

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

        System.out.println(results.stream().map(r -> r.observedTrust).mapToDouble(x -> x).summaryStatistics());
        System.out.println(evaluationsToString(Util.evaluateAll(
            Util.groupMapKeys(results.stream().map(r -> r.predictedTrust).collect(Collectors.toList())),
            results.stream().map(r -> r.observedTrust).collect(Collectors.toList()),
            p.binaryUtilities
        )));

    }
}
