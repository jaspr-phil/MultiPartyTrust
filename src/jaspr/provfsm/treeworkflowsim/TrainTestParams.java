package jaspr.provfsm.treeworkflowsim;

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
 * Created by phil on 25/05/18.
 */
public class TrainTestParams {

    @Parameter(names = {"--trainWorkflows"})
    public int trainWorkflows;
    @Parameter(names = {"--minExecObs"})
    public int minExecObs;
    @Parameter(names = {"--maxExecObs"})
    public int maxExecObs;
    @Parameter(names = {"--testWorkflows"})
    public int testWorkflows;

    @Parameter(names = {"--binaryUtilities"})
    public String binaryUtilitiesStr;
    public boolean binaryUtilities;

    public Settings<String,String> gspanSettings;
    @Parameter(names = {"--gspanSupport"})
    public double gspanSupport;
    @Parameter(names = {"--maxGspanSupport"})
    public double maxGspanSupport;
    @Parameter(names = {"--gspanMinNodes"})
    public int gspanMinNodes;
    @Parameter(names = {"--gspanMaxNodes"})
    public int gspanMaxNodes;


    @Parameter(names = {"--totTasks"})
    public int totTasks;
    @Parameter(names = {"--totItems"})
    public int totItems;
    @Parameter(names = {"--totWorkers"})
    public int totWorkers;

    @Parameter(names = {"--minTasks"})
    public int minTasks;
    @Parameter(names = {"--minItems"})
    public int minItems;
    @Parameter(names = {"--minWorkers"})
    public int minWorkers;
    @Parameter(names = {"--maxTasks"})
    public int maxTasks;
    @Parameter(names = {"--maxItems"})
    public int maxItems;
    @Parameter(names = {"--maxWorkers"})
    public int maxWorkers;

    @Parameter(names = {"--minDepth"})
    public int minDepth;
    @Parameter(names = {"--minBranchingFactor"})
    public int minBranchingFactor;
    @Parameter(names = {"--repeatTaskLikelihood"})
    public double repeatTaskLikelihood;
    @Parameter(names = {"--minExecutions"})
    public int minExecutions;

    @Parameter(names = {"--maxDepth"})
    public int maxDepth;
    @Parameter(names = {"--maxBranchingFactor"})
    public int maxBranchingFactor;
    @Parameter(names = {"--maxExecutions"})
    public int maxExecutions;

    @Parameter(names = {"--isOrLikelihood"})
    public double isOrLikelihood;
    @Parameter(names = {"--workerStd"})
    public double workerStd;
    @Parameter(names = {"--taskStd"})
    public double proficiencyStd;
    @Parameter(names = {"--proficiencyStd"})
    public double taskStd;
    @Parameter(names = {"--followMean"})
    public double followMean;
    @Parameter(names = {"--followStd"})
    public double followStd;
    @Parameter(names = {"--execStd"})
    public double execStd;


    @Parameter(names = {"--workerServiceAssignment"})
    public String workerServiceAssignmentStr;
    public boolean workerServiceAssignment;


    @Parameter(names = "--models")
    private String modelString;

    String[] args;


    public TrainTestParams(String[] args) {
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

        if (this.maxTasks == 0 || this.maxTasks > this.totTasks) this.maxTasks = this.totTasks;
        if (this.maxWorkers == 0 || this.maxWorkers > this.totWorkers) this.maxWorkers = this.totWorkers;
        if (this.maxItems == 0 || this.maxItems > this.totItems) this.maxItems = this.totItems;
        if (this.minTasks > this.totTasks) this.minTasks = this.totTasks;
        if (this.minWorkers > this.totWorkers) this.minWorkers = this.totWorkers;
        if (this.minItems > this.totItems) this.minItems = this.totItems;
        if (this.minTasks == 0) this.minTasks = 1;
        if (this.minWorkers == 0) this.minWorkers = 1;
        if (this.minItems == 0) this.minItems = 1;


        this.gspanSettings = new Settings<>();

        this.gspanSettings.minFreq = new IntFrequency((int)(this.trainWorkflows*this.maxExecutions*this.gspanSupport));
        this.gspanSettings.maxFreq = new IntFrequency((int)(this.trainWorkflows*this.maxExecutions*this.maxGspanSupport));
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
            models.add(ISimParams.modelFactory(ms, binaryUtilities, gspanSettings));
        }
        return models;
    }


}
