package jaspr.provfsm.epigenicssim;


import jaspr.provfsm.core.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phil on 18/04/18.
 */
public class Simulation extends ISimulation<SimParams> {

    private List<IWorker> workers;
    private List<IWidget> inputs;

    public Simulation(SimParams p) {
        super(p);

        int numItems = p.totItems;
        int numWorkers = p.totWorkers;

        int numProperties = 1;

        this.workers = new ArrayList<>();
        this.inputs = new ArrayList<>();


        for (int i=0;i<numWorkers;i++) {
            List<Double> properties = RandUtil.randGaussians(numProperties, 1, p.workerStd);
//            System.out.println(properties);
//            List<Double> properties = RandUtil.randDoubles(numProperties, 0, 10);
            workers.add(new Worker(""+i, properties, EpigenomicsWorkflow.getTasks(p.taskStd), p.proficiencyStd, p.execStd));
            System.out.println(workers.get(i));
        }

        for (int i=0;i<numItems;i++) {
//            List<Double> properties = RandUtil.randDoubles(numProperties, 0, 1);
            List<Double> properties = Util.ones(numProperties);
            System.out.println(properties);
            inputs.add(new Widget(""+i, properties));
            System.out.println(inputs.get(i)+" "+properties);
        }
    }

    @Override
    protected IWorkflow generateNewWorkflow() {

        int width = p.workflowWidth;
        double branchLikelihood = p.branchLikelihood;

        return new EpigenomicsWorkflow(
                p.numExecutions,
                width,
                branchLikelihood,
                workers,
                inputs,
                p.workerServiceAssignment
        );

    }
}



