//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.pegasussim;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import de.parsemis.algorithms.gSpan.Algorithm;
import de.parsemis.graph.HPListGraph.Factory;
import de.parsemis.miner.environment.LocalEnvironment;
import de.parsemis.miner.environment.Settings;
import de.parsemis.miner.environment.ThreadEnvironmentFactory;
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

public class SimParams extends ISimParams {
    @Parameter(
            names = {"--workflowTasks"}
    )
    public int workflowTasks;
    @Parameter(
            names = {"--workflowWorkers"}
    )
    public int workflowWorkers;
    @Parameter(
            names = {"--totWorkers"}
    )
    public int totWorkers;
    @Parameter(
            names = {"--totTasks"}
    )
    public int totTasks;
    @Parameter(
            names = {"--taskOutputDivisorWeight"}
    )
    public double taskOutputDivisorWeight;
    @Parameter(
            names = {"--workerStd"}
    )
    public double workerStd;
    @Parameter(
            names = {"--taskStd"}
    )
    public double taskStd;
    @Parameter(
            names = {"--proficiencyStd"}
    )
    public double proficiencyStd;
    @Parameter(
            names = {"--execStd"}
    )
    public double execStd;
    @Parameter(
            names = {"--followStd"}
    )
    public double followStd;
    @Parameter(
            names = {"--workflowComplexity"}
    )
    public int workflowComplexity;
    @Parameter(
            names = {"--workerLeaveProb"}
    )
    public double workerLeaveProb;
    @Parameter(
            names = {"--workerServiceAssignment"}
    )
    public String workerServiceAssignmentStr;
    public boolean workerServiceAssignment;
    @Parameter(
            names = {"--singleBlockWorker"}
    )
    public String singleBlockWorkerStr;
    public boolean singleBlockWorker;
    @Parameter(
            names = {"--models"}
    )
    private String modelString;

    public SimParams(String[] args) {
        this.args = args;
        JCommander.newBuilder().addObject(this).build().parse(args);
        System.out.println(Util.format(args));
        this.binaryUtilities = Boolean.parseBoolean(this.binaryUtilitiesStr);
        this.workerServiceAssignment = Boolean.parseBoolean(this.workerServiceAssignmentStr);
        this.singleBlockWorker = Boolean.parseBoolean(this.singleBlockWorkerStr);
        this.gspanSettings = new Settings();
        this.gspanSettings.minFreq = new IntFrequency((int)((double)this.numSimulations * this.gspanSupport));
        this.gspanSettings.maxFreq = new IntFrequency((int)((double)this.numSimulations * this.maxGspanSupport));
        this.gspanSettings.minNodes = this.gspanMinNodes;
        this.gspanSettings.maxNodes = this.gspanMaxNodes;
        GraphParser<String, String> parser = new DotGraphParser(new StringLabelParser(), new StringLabelParser());
        this.gspanSettings.serializer = parser;
        LocalEnvironment.create(this.gspanSettings, 0, (ArrayList)null, (ArrayList)null, "NULLNODE", "NULLEDGE", (ThreadEnvironmentFactory)null);
        this.gspanSettings.factory = new Factory(parser.getNodeParser(), parser.getEdgeParser());
        this.gspanSettings.algorithm = new Algorithm();
        this.gspanSettings.strategy = new RecursiveStrategy();
    }

    public List<Model> makeModels() {
        String[] modelStrings = this.modelString.split(",");
        List<Model> models = new ArrayList();
        String[] var3 = modelStrings;
        int var4 = modelStrings.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String ms = var3[var5];
            models.add(this.modelFactory(ms));
        }

        return models;
    }
}
