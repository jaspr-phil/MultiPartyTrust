//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.core;

import jaspr.provfsm.models.Model;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.openprovenance.prov.model.Document;
import weka.classifiers.Evaluation;

public abstract class ISimulation<T extends ISimParams> {
    protected T p;
    protected List<IWorkflow> workflows = new ArrayList();
    public List<Document> documents = new ArrayList();
    public List<Double> observedValues = new ArrayList();
    public List<Double> expectedValues = new ArrayList();
    public Map<Model, List<Double>> predictions = new HashMap();
    public List<Model> models;
    public Map<Model, List<Evaluation>> results;
    public Map<Model, Evaluation> overallResults;
    HashMap<IWorkflow, Integer> picked = new HashMap();
    Map<IWorkflow, Integer> counts = new HashMap();

    public ISimulation(T p) {
        this.p = p;
        this.models = p.makeModels();
        for (Model model : models) {
            this.predictions.put(model, new ArrayList());
        }

    }

    public IWorkflow generateWorkflow() {
        IWorkflow workflow;
        if (this.p.numWorkflows == 0) {
            workflow = this.generateNewWorkflow();
        } else {
            workflow = (IWorkflow)RandUtil.choose(this.workflows);
            this.workflows.indexOf(workflow);
        }

        if (this.picked.containsKey(workflow)) {
            this.picked.put(workflow, (Integer)this.picked.get(workflow) + 1);
        } else {
            this.picked.put(workflow, 1);
        }

        return workflow;
    }

    private void refreshWorkflows() {
        System.out.println("Refreshing workflow bank");
        this.workflows = new ArrayList();
        if (this.p.numWorkflows != 0) {
            for(int i = 0; i < this.p.numWorkflows; ++i) {
                this.workflows.add(this.generateNewWorkflow());
            }
        }

    }

    protected abstract IWorkflow generateNewWorkflow();

    public void run() throws Exception {
        for (Model model : models) {
            predictions.put(model, new ArrayList<>());
        }

        this.refreshWorkflows();

        for(int simid = 0; simid < this.p.numSimulations - 1; ++simid) {
            if (simid % this.p.learningInterval == this.p.learningInterval - 1) {
                System.out.println("\tLearning... (round " + simid + ")");

                for (Model model : models) {
                    model.forget();
                    model.learnFromProv(documents, observedValues);
                }

                System.out.println("\tdone.");
                this.refreshWorkflows();
            }

            IWorkflow workflow = this.generateWorkflow();
            List<Document> provDocuments = new ArrayList();
            List<Double> provUtilities = new ArrayList();
            Map<Model, List<Double>> provPredictions = new HashMap();

            for (Model model : models) {
                provPredictions.put(model, new ArrayList<>());
            }

            List<Execution> executions = workflow.getExecutions();
            for (Execution execution : executions) {
                Document document = execution.getDocument();
                double utility;
                if (this.p.binaryUtilities) {
                    utility = execution.getSuccess() ? 0.0D : 1.0D;
                } else {
                    utility = execution.getUtility();
                }

                for (Model model : models) {
                    double prediction;
                    if (p.binaryUtilities) {
                        prediction = model.predict(document) > 0.5 ? 1 : 0;
                    } else {
                        prediction = model.predict(document);
                    }
                    provPredictions.get(model).add(prediction);
                }

                provDocuments.add(document);
                provUtilities.add(utility);
            }

            double expectedUtility = Util.mean(provUtilities);

            for(int i = 0; i < Math.min(this.p.numExecutionObs, executions.size()); ++i) {
                Document observedDocument = (Document)provDocuments.get(i);
                double observedUtility = (Double)provUtilities.get(i);
                this.documents.add(observedDocument);
                this.observedValues.add(observedUtility);
                this.expectedValues.add(expectedUtility);
            }

            List<Double> workflowPredictions = new ArrayList();
            for (Model model : models) {
                double pred = Util.mean((Collection)provPredictions.get(model));
                if (this.p.binaryUtilities) {
                    pred = pred < 0.5D ? 0.0D : 1.0D;
                }

                ((List)this.predictions.get(model)).add(pred);
                workflowPredictions.add(pred);
            }

            System.out.println("Result: " + simid + " : " + Util.format(expectedUtility) + " : " + Util.format(workflowPredictions) + "\n");
        }

        System.out.println("----");
        this.results = Util.evaluate(this.predictions, this.expectedValues, this.p.learningInterval);
        this.overallResults = Util.evaluateAll(this.predictions, this.expectedValues, this.p.binaryUtilities);
    }

    public void printResults() throws Exception {
        System.out.println("\tR2\t\tMAE\t\tRMSE");
        for (Model model : this.models) {
            Evaluation eval = this.overallResults.get(model);
            System.out.println(Util.format(eval) + "\t\t" + model.name());
        }

        for (Model model : this.models) {
            System.out.println(model.name());
            System.out.println("\t\tR2\t\tMAE\t\tRMSE");
            List<Evaluation> evals = this.results.get(model);
            int numRepresentedRounds = this.p.learningInterval;
            for (Evaluation eval : evals) {
                System.out.println(numRepresentedRounds + "\t" + Util.format(eval));
                numRepresentedRounds += this.p.learningInterval;
            }
        }
    }

}
