package jaspr.provfsm.epigenicssim;


import jaspr.provfsm.core.*;

public class Main {

    public static void main(String [] args) throws Exception {

        RandUtil.init();
        ProvUtil.init();
        GraphUtil.init();

        if (args.length == 0) {
            args = (
                    "--numSimulations 500 " +
                            "--learningInterval 250 " +
                            "--numWorkflows 500 " +
                            "--numExecutions 1 " +
                            "--workflowWidth 5 " +
                            "--branchLikelihood 1 " +
                            "--workerServiceAssignment true " +
                            "--totWorkers 10 " +
                            "--totItems 1 " +
                            "--workerStd 0.5 " +
                            "--taskStd 0.5 " +
                            "--proficiencyStd 0.5 " +
                            "--execStd 0.05 " +
                            "--models \"mean," +
//                                "agg-full," +
//                            "contextagg-full," +
////                              "random," +
                            "simple-full," +
////                            "context-full-100," +
//                            "context-full-500," +
                            "context-full-1000," +
//                            "gspan-prefix-100," +
////                            "gspan-prefix-500," +
//                            "gspan-prefix-1000," +
//                            "gspan-full-100," +
//                            "gspan-full-500," +
                            "gspan-full-1000," +
                            "kernel-full," +
//                            "kernel-prefix," +
                            "\" " +
                            "--gspanSupport 0.05 " +
                            "--maxGspanSupport 1 " +
                            "--gspanMinNodes 2 " +
                            "--gspanMaxNodes 4 " +
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
