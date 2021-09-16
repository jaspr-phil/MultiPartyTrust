package jaspr.provfsm.pegasussim;

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

    @Parameter(names = {"--minExecutions"})
    public int minExecutions;
    @Parameter(names = {"--maxExecutions"})
    public int maxExecutions;


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


    @Parameter(names = {"--workflowTasks"})
    public int workflowTasks;
    @Parameter(names = {"--workflowWorkers"})
    public int workflowWorkers;
    @Parameter(names = {"--totWorkers"})
    public int totWorkers;
    @Parameter(names = {"--totTasks"})
    public int totTasks;

    @Parameter(names = {"--taskOutputDivisorWeight"})
    public double taskOutputDivisorWeight;

    @Parameter(names = {"--workerStd"})
    public double workerStd;
    @Parameter(names = {"--taskStd"})
    public double taskStd;
    @Parameter(names = {"--proficiencyStd"})
    public double proficiencyStd;
    @Parameter(names = {"--execStd"})
    public double execStd;
    @Parameter(names = {"--followStd"})
    public double followStd;

    @Parameter(names = {"--workflowComplexity"})
    public int workflowComplexity;
    @Parameter(names = {"--workerLeaveProb"})
    public double workerLeaveProb;

    @Parameter(names = {"--workerServiceAssignment"})
    public String workerServiceAssignmentStr;
    public boolean workerServiceAssignment;

    @Parameter(names = {"--singleBlockWorker"})
    public String singleBlockWorkerStr;
    public boolean singleBlockWorker;


    @Parameter(names = "--models")
    private String modelString;

    String[] args;


    public TrainTestParams(String[] args) {

        this.args = args;

        JCommander.newBuilder()
                .addObject(this)
                .build()
                .parse(args);

        System.out.println(Util.format(args));


        this.binaryUtilities = Boolean.parseBoolean(this.binaryUtilitiesStr);
        this.workerServiceAssignment = Boolean.parseBoolean(this.workerServiceAssignmentStr);
        this.singleBlockWorker = Boolean.parseBoolean(this.singleBlockWorkerStr);


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
