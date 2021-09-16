package jaspr.provfsm.treeworkflowsim;


import jaspr.provfsm.core.*;
import jaspr.provfsm.treeworkflowsim.SimParams;
import jaspr.provfsm.treeworkflowsim.Simulation;

public class Main {

    public static void main(String [] args) throws Exception {

        RandUtil.init();
        ProvUtil.init();
        GraphUtil.init();

        if (args.length == 0) {
            args = (
                    "--numSimulations 100 " +
                    "--learningInterval 900 " +
                    "--numWorkflows 0 " +
                    "--numExecutions 1 " +
                    "--maxDepth 10 " +
                    "--branchingFactor 3 " +
                    "--randomWorkerAssignment true " +
                    "--totTasks 100 " +
                    "--totWorkers 100 " +
                    "--totItems 1 " +
                    "--simTasks 25 " +
                    "--simWorkers 10 " +
                    "--workerStd 0.5 " +
                    "--taskStd 0.5 " +
                    "--proficiencyStd 0.25 " +
                    "--followStd 0. " +
                    "--execStd 0.1 " +
                    "--models \"mean," +
//                                "agg-full," +
//                            "contextagg-full," +
////                              "random," +
                            "simple-full," +
////                            "context-full-100," +
//                            "context-full-500," +
                            "context-full-1000," +
////                            "gspan-prefix-100," +
////                            "gspan-prefix-500," +
////                            "gspan-prefix-1000," +
////                            "gspan-full-100," +
//                            "gspan-full-500," +
//                            "gspan-full-1000," +
                            "kernel-full," +
////                            "kernel-prefix," +
                            "\" " +
                    "--gspanSupport 0.01 " +
                    "--maxGspanSupport 1 " +
                    "--gspanMinNodes 2 " +
                    "--gspanMaxNodes 3 " +
                    "--binaryUtilities false"
            ).split(" ");
        }

        SimParams params = new SimParams(args);

        ISimulation simulation = new Simulation(params);
        simulation.run();

        simulation.printResults();

        System.out.println(Util.format(params.args));
    }

}
