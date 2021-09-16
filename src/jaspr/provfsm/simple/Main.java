package jaspr.provfsm.simple;


import jaspr.provfsm.core.*;
import jaspr.provfsm.simple.*;
import jaspr.provfsm.models.*;
import jaspr.provfsm.core.Execution;
import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.*;
import weka.classifiers.Evaluation;

import java.util.*;

public class Main {

    public static void main(String [] args) throws Exception {

        RandUtil.init();
        ProvUtil.init();
        GraphUtil.init();

        if (args.length == 0) {
            args = (
                    "--totItems 5 " +
                    "--simItems 1 " +
                    "--randomItemUtilities false " +
                    "--simulationType combination " +
                    "--numSimulations 1000 " +
                    "--learningInterval 900 " +
                    "--numWorkflows 0 " +
                    "--numExecutions 1 " +
                    "--totTasks 25 " +
                    "--simTasks 25 " +
                    "--totWorkers 25 " +
                    "--simWorkers 5 " +
                    "--taskPriorities false " +
                    "--models \"mean," +
//                              "random," +
                            "simple-full," +
//                            "context-full-100," +
                            "context-full-500," +
//                            "context-full-1000," +
//                            "gspan-prefix-100," +
//                            "gspan-prefix-500," +
//                            "gspan-prefix-1000," +
//                            "gspan-full-100," +
//                            "gspan-full-500," +
//                            "gspan-full-1000," +
                            "kernel-full," +
//                            "kernel-prefix," +
                            "\" " +
                    "--gspanSupport 0.01 " +
                    "--maxGspanSupport 1 " +
                    "--gspanMinNodes 2 " +
                    "--gspanMaxNodes 3 " +
                    "--binaryUtilities false"
            ).split(" ");
        }

        SimParams params = new SimParams(args);
        ISimulation simulation = new jaspr.provfsm.simple.Simulation(params);

        simulation.run();

        simulation.printResults();


        System.out.println(Util.format(params.args));
    }

    static void saveProv(Document document, String file) {
        InteropFramework intF=new InteropFramework();
        intF.writeDocument(file, document);
        intF.writeDocument(System.out, InteropFramework.ProvFormat.PROVN, document);
    }
}
