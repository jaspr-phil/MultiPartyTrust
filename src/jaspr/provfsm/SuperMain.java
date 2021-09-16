package jaspr.provfsm;

import jaspr.provfsm.treeworkflowsim.Main;

public class SuperMain {

    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            args = ("10 " +
                    "--numExecutions 1 " +
                    "--taskStd 0.5 " +
                    "--gspanMinNodes 2 " +
                    "--numWorkflows 0 " +
                    "--maxDepth 5 " +
                    "--gspanSupport 0.01 " +
                    "--totTasks 100 " +
                    "--totWorkers 100 " +
                    "--simWorkers 10 " +
                    "--models mean " +
                    "--binaryUtilities 'false' " +
                    "--proficiencyStd 0.25 " +
                    "--followStd 0.25 " +
                    "--randomWorkerAssignment 'false' " +
                    "--branchingFactor 3 " +
                    "--learningInterval 100 " +
                    "--numSimulations 2500 " +
                    "--maxGspanSupport 1 " +
                    "--workerStd 0.5 " +
                    "--execStd 0.1 " +
                    "--totItems 1 " +
                    "--simTasks 25 " +
                    "--gspanMaxNodes 25 "
            ).split(" ");
        }

        int iterations = Integer.parseInt(args[0]);
        String[] simargs = new String[args.length-1];
        for (int i=1;i<args.length;i++) {
            simargs[i-1] = args[i];
        }

        for (int i=0;i<iterations;i++) {
            System.out.println("---- ITERATION "+i+" ----");
            Main.main(simargs);
        }
    }

}
