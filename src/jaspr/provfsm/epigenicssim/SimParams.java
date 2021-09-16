package jaspr.provfsm.epigenicssim;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import de.parsemis.graph.HPListGraph;
import de.parsemis.miner.environment.LocalEnvironment;
import de.parsemis.miner.environment.Settings;
import de.parsemis.miner.general.IntFrequency;
import de.parsemis.parsers.DotGraphParser;
import de.parsemis.parsers.GraphParser;
import de.parsemis.parsers.StringLabelParser;
import de.parsemis.strategy.RecursiveStrategy;
import jaspr.provfsm.core.ISimParams;
import jaspr.provfsm.core.Util;
import jaspr.provfsm.models.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phil on 09/11/17.
 */
public class SimParams extends ISimParams {

    @Parameter(names = {"--totItems"})
    public int totItems;
    @Parameter(names = {"--totWorkers"})
    public int totWorkers;

    @Parameter(names = {"--workerStd"})
    public double workerStd;
    @Parameter(names = {"--taskStd"})
    public double taskStd;
    @Parameter(names = {"--proficiencyStd"})
    public double proficiencyStd;
    @Parameter(names = {"--execStd"})
    public double execStd;


    @Parameter(names = {"--workerServiceAssignment"})
    public String workerServiceAssignmentStr;
    public boolean workerServiceAssignment;

    @Parameter(names = {"--workflowWidth"})
    public int workflowWidth;
    @Parameter(names = {"--branchLikelihood"})
    public double branchLikelihood;



    @Parameter(names = "--models")
    private String modelString;




    public SimParams(String[] args) {
        /**
         * Parameters are to include:
         *  totTasks, totItems, totWorkers //
         *  simTasks, simItems, simWorkers //
         *  numExecutions //
         *  simulationType:
         *      Combination (simTasks=totTasks!!!, simAgents=totAgents???)
         *      Linear (simAgents=totAgents???)
         *  numSimulations, learningInterval //
         *  gspanSupport (percent of f(learningInterval,numSimulations), gspanMinNodes, gspanMaxNodes //
         */

        this.args = args;

        JCommander.newBuilder()
                .addObject(this)
                .build()
                .parse(args);

        System.out.println(Util.format(args));


        this.binaryUtilities = Boolean.parseBoolean(this.binaryUtilitiesStr);
        this.workerServiceAssignment = Boolean.parseBoolean(this.workerServiceAssignmentStr);

        this.gspanSettings = new Settings<>();

        this.gspanSettings.minFreq = new IntFrequency((int)(this.numSimulations*this.gspanSupport));
        this.gspanSettings.maxFreq = new IntFrequency((int)(this.numSimulations*this.maxGspanSupport));
        this.gspanSettings.minNodes = this.gspanMinNodes;
        this.gspanSettings.maxNodes = this.gspanMaxNodes;

        GraphParser<String, String> parser = new DotGraphParser<>(new StringLabelParser(), new StringLabelParser());
        this.gspanSettings.serializer = parser;
        LocalEnvironment.create(this.gspanSettings, 0, null, null, "NULLNODE", "NULLEDGE", null);

        this.gspanSettings.factory = new HPListGraph.Factory<>(parser.getNodeParser(), parser.getEdgeParser());
        this.gspanSettings.algorithm = new de.parsemis.algorithms.gSpan.Algorithm<>();
        this.gspanSettings.strategy = new RecursiveStrategy<>();
    }

    public List<Model> makeModels() {
        String[] modelStrings = modelString.split(",");
        List<Model> models = new ArrayList<>();
        for (String ms : modelStrings) {
            models.add(this.modelFactory(ms));
        }
        return models;
    }


}

