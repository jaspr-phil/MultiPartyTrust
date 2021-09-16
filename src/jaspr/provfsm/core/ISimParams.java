//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.core;

import com.beust.jcommander.Parameter;
import de.parsemis.miner.environment.Settings;
import jaspr.provfsm.models.AggModel;
import jaspr.provfsm.models.AggModel2;
import jaspr.provfsm.models.ContextAggModel;
import jaspr.provfsm.models.ContextAggModel2;
import jaspr.provfsm.models.ContextModel;
import jaspr.provfsm.models.FullNodeNamer;
import jaspr.provfsm.models.GSPANAggModel;
import jaspr.provfsm.models.GSPANModel;
import jaspr.provfsm.models.GraphKernelModel;
import jaspr.provfsm.models.MeanModel;
import jaspr.provfsm.models.Model;
import jaspr.provfsm.models.NodeNamer;
import jaspr.provfsm.models.PrefixNodeNamer;
import jaspr.provfsm.models.RandomModel;
import jaspr.provfsm.models.SimpleModel;
import java.util.List;
import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.SMOreg;

public abstract class ISimParams {
    @Parameter(
            names = {"--numSimulations"}
    )
    public int numSimulations;
    @Parameter(
            names = {"--numWorkflows"}
    )
    public int numWorkflows;
    @Parameter(
            names = {"--learningInterval"}
    )
    public int learningInterval;
    @Parameter(
            names = {"--numExecutions"}
    )
    public int numExecutions;
    @Parameter(
            names = {"--numExecutionObs"}
    )
    public int numExecutionObs;
    @Parameter(
            names = {"--binaryUtilities"}
    )
    public String binaryUtilitiesStr;
    public boolean binaryUtilities;
    public Settings<String, String> gspanSettings;
    @Parameter(
            names = {"--gspanSupport"}
    )
    public double gspanSupport;
    @Parameter(
            names = {"--maxGspanSupport"}
    )
    public double maxGspanSupport;
    @Parameter(
            names = {"--gspanMinNodes"}
    )
    public int gspanMinNodes;
    @Parameter(
            names = {"--gspanMaxNodes"}
    )
    public int gspanMaxNodes;
    public String[] args;

    public ISimParams() {
    }

    public abstract List<Model> makeModels();

    public Model modelFactory(String ms) {
        return modelFactory(ms, this.binaryUtilities, this.gspanSettings);
    }

    public static Model modelFactory(String ms, boolean binaryUtilities, Settings<String, String> gspanSettings) {
        System.out.println("Making model: " + ms);
        String[] modelsplt = ms.split("-");
        String model = modelsplt[0].toLowerCase();
        String namer = modelsplt.length >= 2 ? modelsplt[1].toLowerCase() : "";
        int maxFeatures = modelsplt.length >= 3 ? Integer.parseInt(modelsplt[2]) : 2147483647;
        Object learner;
        if (binaryUtilities) {
            learner = new SMO();
        } else {
            learner = new SMOreg();
        }

        Object nodeNamer;
        if (namer.equals("prefix")) {
            nodeNamer = new PrefixNodeNamer();
        } else if (namer.equals("full")) {
            nodeNamer = new FullNodeNamer();
        } else {
            nodeNamer = null;
        }

        if (model.equals("gspan")) {
            return new GSPANModel((Classifier)learner, gspanSettings, (NodeNamer)nodeNamer, maxFeatures, binaryUtilities);
        } else if (model.equals("gspanagg")) {
            return new GSPANAggModel(gspanSettings, (NodeNamer)nodeNamer, maxFeatures, binaryUtilities);
        } else if (model.equals("simple")) {
            return new SimpleModel((Classifier)learner, (NodeNamer)nodeNamer, binaryUtilities);
        } else if (model.equals("context")) {
            return new ContextModel((Classifier)learner, (NodeNamer)nodeNamer, maxFeatures, binaryUtilities);
        } else if (ms.equals("mean")) {
            return new MeanModel();
        } else if (ms.equals("random")) {
            return new RandomModel(binaryUtilities);
        } else if (model.equals("kernel")) {
            return new GraphKernelModel((NodeNamer)nodeNamer, binaryUtilities);
        } else if (model.equals("agg")) {
            return new AggModel((NodeNamer)nodeNamer);
        } else if (model.equals("contextagg")) {
            return new ContextAggModel((NodeNamer)nodeNamer);
        } else if (model.equals("agg2")) {
            return new AggModel2((NodeNamer)nodeNamer);
        } else if (model.equals("contextagg2")) {
            return new ContextAggModel2((NodeNamer)nodeNamer);
        } else {
            throw new UnsupportedOperationException("Model requested does not exist: " + ms);
        }
    }
}
