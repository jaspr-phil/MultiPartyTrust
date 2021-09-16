package jaspr.provfsm.treeworkflowsim_jaamas;

import de.parsemis.graph.Graph;
import jaspr.provfsm.core.*;
import jaspr.provfsm.models.FullNodeNamer;
import jaspr.provfsm.models.NodeNamer;
import jaspr.provfsm.models.PrefixNodeNamer;
import jaspr.provfsm.treeworkflowsim_jaamas.workflows.Task;
import jaspr.provfsm.treeworkflowsim_jaamas.workflows.TreeWorkflow;
import jaspr.provfsm.treeworkflowsim_jaamas.workflows.Widget;
import jaspr.provfsm.treeworkflowsim_jaamas.workflows.Worker;

import java.util.*;

public class Investigation {

    public static void main(String[] args) throws Exception {

        RandUtil.init();
        ProvUtil.init();
        GraphUtil.init();

        int numItems = 1;

        int numAgents = 250;
        int numTasks = numAgents;
        int numWorkers = numAgents;
        int iterations = 10;
        int testingWorkflows = 100;




        System.out.println("Depth\tNEdges\tNEdges-err\tNStuff\tNStuff-err\tNWorkers\tNWorkers-err\tNTasks\tNTasks-err\tR2\tR2-err\tMAE\tMAE-err");
        for (int depth = 1; depth <= 9; depth++) {

            List<Double> corrs = new ArrayList<>();
            List<Double> maes = new ArrayList<>();
            List<Double> numedges = new ArrayList<>();
            List<Double> numworkers = new ArrayList<>();
            List<Double> numtasks = new ArrayList<>();
            List<Double> numstuff = new ArrayList<>();


            for (int iter=0;iter<iterations;iter++) {

                List<Widget> widgets = initItems(numItems);
                List<Task> tasklist = initTasks(numTasks);
                List<Worker> workerlist = initWorkers(numWorkers, tasklist);

                NodeNamer namer = new PrefixNodeNamer();

                Map<String, Task> tasks = new HashMap<>();
                for (Task t : tasklist) {
                    tasks.put(namer.name(t.asProv(ProvUtil.factory).getId()), t);
                }

                Map<String, Worker> workers = new HashMap<>();
                for (Worker w : workerlist) {
                    workers.put(namer.name(w.asProv(ProvUtil.factory).getId()), w);
                }


                Result res = sim(testingWorkflows, depth, tasklist, workerlist, widgets);
//            System.out.println(Util.format(res.utilities));
//            System.out.println(Util.mean(res.utilities));
//            System.out.println(Util.std(res.utilities));

                List<Double> ests = new ArrayList<>();
                List<Double> utilities = new ArrayList<>();
                List<Double> errs = new ArrayList<>();
                List<Double> nedgs = new ArrayList<>();
                List<Double> ntsks = new ArrayList<>();
                List<Double> nwrkrs = new ArrayList<>();

                for (int i = 0; i < testingWorkflows; i++) {

                    Collection<String> edges = GraphUtil.getEdgesOf(res.graphs.get(i), "WasAssociatedWith");
                    Set<String> workerset = new HashSet<>();
                    Set<String> taskset = new HashSet<>();

                    double est = 0;
                    for (String e : edges) {
                        String worker = e.split("->")[0];
                        String task = e.split("->")[1];
                        workerset.add(worker);
                        taskset.add(task);
//                        double taskProperty = tasks.get(task).meanProperty();
//                        double workerProperty = workers.get(worker).meanProperty();
//                        est += taskProperty * workerProperty;
                        est += workers.get(worker).meanCompetency(tasks.get(task));
                    }
                    est /= Math.sqrt(depth);
                    ests.add(est);
                    utilities.add(res.utilities.get(i));
                    errs.add(Math.abs(est - res.utilities.get(i)));
                    nedgs.add((double)edges.size());
                    nwrkrs.add((double)workerset.size());
                    ntsks.add((double)taskset.size());
                }

//                System.out.println(Util.formats("\t", depth,
//                        Util.mean(utilities), Util.std(utilities),
//                        Util.mean(ests), Util.std(ests),
//                        Util.corr(utilities, ests),
//                        Math.pow(Util.corr(utilities, ests), 2),
//                        Util.mean(errs)
//                ));

                corrs.add(Math.pow(Util.corr(utilities, ests),2));
                maes.add(Util.mean(errs));
                numedges.add(Util.mean(nedgs));
                numworkers.add(Util.mean(nwrkrs));
                numtasks.add(Util.mean(ntsks));
                numstuff.add(Util.mean(nwrkrs)+Util.mean(ntsks));

            }
            System.out.println(Util.formats("\t", depth,
                    Util.mean(numedges), Util.std(numedges)/Math.sqrt((double)iterations),
                    Util.mean(numstuff), Util.std(numstuff)/Math.sqrt((double)iterations),
                    Util.mean(numworkers), Util.std(numworkers)/Math.sqrt((double)iterations),
                    Util.mean(numtasks), Util.std(numtasks)/Math.sqrt((double)iterations),
                    Util.mean(corrs), Util.std(corrs)/Math.sqrt((double)iterations),
                    Util.mean(maes), Util.std(maes)/Math.sqrt((double)iterations)
            ));
        }
    }

    static class Result {
        public List<Double> utilities;
        public List<Graph<String,String>> graphs;
        public Result() {
            this.utilities = new ArrayList<>();
            this.graphs = new ArrayList<>();
        }

        public void addUtility(double utility) {
            this.utilities.add(utility);
        }

        public void addGraph(Graph<String,String> graph) {
            this.graphs.add(graph);
        }
    }

    static Result sim(int iterations, int depth, List<Task> tasks, List<Worker> workers, List<Widget> widgets) {
        int numItems = 1;
        int branchingFactor = 3;
        double repeatTaskProb = 0;
        int numExecutions = 1;
        double isOrProb = 0.5;

        Result res = new Result();
        for (int i=0;i<iterations;i++) {
            TreeWorkflow workflow = new TreeWorkflow(tasks, workers, widgets, false, depth, branchingFactor, repeatTaskProb, isOrProb, numExecutions, ProvUtil.factory);
            List<Execution> executions = workflow.getExecutions();
            for (Execution e : executions) {
                res.addUtility(e.getUtility());
                res.addGraph(GraphUtil.makeGraph(e.getDocument(), new FullNodeNamer()));
            }
        }
        return res;
    }


    static int numProperties = 1;

    static List<Task> initTasks(int numTasks) {
        double taskStd = 0.25;
        double followMean = 0;
        double followStd = 0.5;

        List<Task> tasks = new ArrayList<>();
        for (int i=0;i<numTasks;i++) {
            List<Double> properties = new ArrayList<>(RandUtil.randGaussians(numProperties, 1, taskStd));
            tasks.add(new Task(""+i, 1, 1, properties));
        }

        for (Task task : tasks) {
            task.setFollowWeightings(tasks, followMean, followStd);
        }
        return tasks;
    }
    static List<Worker> initWorkers(int numWorkers,  List<Task> tasks) {
        double workerStd = 0.5;
        double proficiencyStd = 0.5;
        double execStd = 0.01;
        List<Worker> workers = new ArrayList<>();

        for (int i=0;i<numWorkers;i++) {
            List<Double> properties = new ArrayList<>(RandUtil.randGaussians(numProperties, 1, workerStd));
            workers.add(new Worker(""+i, properties, tasks, proficiencyStd, execStd));
        }
        return workers;
    }

    static List<Widget> initItems(int numItems) {
        List<Widget> widgets = new ArrayList<>();

        for (int i=0;i<numItems;i++) {
            List<Double> properties = Util.ones(numProperties);
            widgets.add(new Widget(""+i, 0, properties));
        }
        return widgets;

    }

}
