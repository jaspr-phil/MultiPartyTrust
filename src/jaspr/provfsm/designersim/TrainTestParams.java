package jaspr.provfsm.designersim;

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
import jaspr.provfsm.models.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phil on 25/05/18.
 */
public class TrainTestParams {


    @Parameter(names = {"--trainDesigners"})
    public int trainDesigners;
    @Parameter(names = {"--testDesigners"})
    public int testDesigners;

    @Parameter(names = {"--trainPortfolioSize"})
    public int trainPortfolioSize;
    @Parameter(names = {"--testPortfolioSize"})
    public int testPortfolioSize;

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

    @Parameter(names = {"--designerMinTasks"})
    public int designerMinTasks;
    @Parameter(names = {"--designerMinItems"})
    public int designerMinItems;
    @Parameter(names = {"--designerMinWorkers"})
    public int designerMinWorkers;
    @Parameter(names = {"--designerMaxTasks"})
    public int designerMaxTasks;
    @Parameter(names = {"--designerMaxItems"})
    public int designerMaxItems;
    @Parameter(names = {"--designerMaxWorkers"})
    public int designerMaxWorkers;

    @Parameter(names = {"--designerMinDepth"})
    public int designerMinDepth;
    @Parameter(names = {"--designerMinBranchingFactor"})
    public int designerMinBranchingFactor;
    @Parameter(names = {"--designerMinExecutions"})
    public int designerMinExecutions;

    @Parameter(names = {"--designerMaxDepth"})
    public int designerMaxDepth;
    @Parameter(names = {"--designerMaxBranchingFactor"})
    public int designerMaxBranchingFactor;
    @Parameter(names = {"--designerMaxExecutions"})
    public int designerMaxExecutions;

    @Parameter(names = {"--workerStd"})
    public double workerStd;
    @Parameter(names = {"--taskStd"})
    public double proficiencyStd;
    @Parameter(names = {"--proficiencyStd"})
    public double taskStd;
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

        this.gspanSettings = new Settings<>();

        this.gspanSettings.minFreq = new IntFrequency((int)(this.trainDesigners*this.trainPortfolioSize*this.gspanSupport));
        this.gspanSettings.maxFreq = new IntFrequency((int)(this.trainDesigners*this.trainPortfolioSize*this.maxGspanSupport));
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
