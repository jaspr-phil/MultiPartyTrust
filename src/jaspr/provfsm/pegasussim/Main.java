//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.pegasussim;

import jaspr.provfsm.core.GraphUtil;
import jaspr.provfsm.core.ISimulation;
import jaspr.provfsm.core.ProvUtil;
import jaspr.provfsm.core.RandUtil;
import jaspr.provfsm.core.Util;

public class Main {
    public Main() {
    }

    public static void main(String[] args) throws Exception {
        RandUtil.init();
        ProvUtil.init();
        GraphUtil.init();
        if (args.length == 0) {
            args = ("--numSimulations 1000 " +
                    "--learningInterval 800 " +
                    "--numWorkflows 0 " +
                    "--numExecutions 1 " +
                    "--numExecutionObs 1 " +
                    "--workerServiceAssignment false " +
                    "--singleBlockWorker false " +
                    "--workflowComplexity 3 " +
                    "--totWorkers 100 " +
                    "--totTasks 100 " +
                    "--workflowTasks 100 " +
                    "--workflowWorkers 100 " +
                    "--taskOutputDivisorWeight 0. " +
                    "--workerLeaveProb 0.01 " +
                    "--workerStd 0.5 " +
                    "--taskStd 0.5 " +
                    "--proficiencyStd 0.25 " +
                    "--followStd 0.0 " +
                    "--execStd 0.05 " +
                    "--models \"mean,agg-prefix,contextagg-prefix,agg2-prefix,contextagg2-prefix,simple-prefix,gspanagg-full,\" " +
                    "--gspanSupport 0.05 " +
                    "--maxGspanSupport 1 " +
                    "--gspanMinNodes 2 " +
                    "--gspanMaxNodes 25 " +
                    "--binaryUtilities false").split(" ");
        }

        SimParams params = new SimParams(args);
        ISimulation simulation = new Simulation(params);
        simulation.run();
        simulation.printResults();
        System.out.println(Util.format(params.args));
    }
}
