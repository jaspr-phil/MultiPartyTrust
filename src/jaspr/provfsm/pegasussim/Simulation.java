//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.pegasussim;

import jaspr.provfsm.core.ISimulation;
import jaspr.provfsm.core.ITask;
import jaspr.provfsm.core.IWidget;
import jaspr.provfsm.core.IWorker;
import jaspr.provfsm.core.IWorkflow;
import jaspr.provfsm.core.RandUtil;
import java.util.ArrayList;
import java.util.List;

public class Simulation extends ISimulation<SimParams> {
    private List<IWorker> workers;
    private List<IWidget> inputs;
    private List<ITask> tasks;
    int numProperties = 1;
    int nextWorkerId = 1;

    public Simulation(SimParams p) {
        super(p);
        int numWorkers = p.totWorkers;
        int numTasks = p.totTasks;
        this.workers = new ArrayList();
        this.inputs = new ArrayList();
        this.tasks = new ArrayList();

        int i;
        for(i = 0; i < numTasks; ++i) {
            Task task = new Task("" + i, RandUtil.randGaussians(this.numProperties, 1.0D, p.taskStd));
            this.tasks.add(task);
        }

        System.out.println(this.tasks);

        for(i = 0; i < numWorkers; ++i) {
            List<Double> properties = RandUtil.randGaussians(this.numProperties, 1.0D, p.workerStd);
            this.workers.add(new Worker("" + this.nextWorkerId, properties, this.tasks, p.proficiencyStd, p.execStd, p.followStd));
            ++this.nextWorkerId;
            System.out.println(this.workers.get(i));
        }

    }

    protected IWorkflow generateNewWorkflow() {
        List<IWorker> tmpworkers = new ArrayList();

        List properties;
        for(int i = 0; i < this.workers.size(); ++i) {
            if (RandUtil.randBoolean(((SimParams)this.p).workerLeaveProb)) {
                properties = RandUtil.randGaussians(this.numProperties, 1.0D, ((SimParams)this.p).workerStd);
                Worker newworker = new Worker("" + this.nextWorkerId, properties, this.tasks, ((SimParams)this.p).proficiencyStd, ((SimParams)this.p).execStd, ((SimParams)this.p).followStd);
                ++this.nextWorkerId;
                tmpworkers.add(newworker);
            } else {
                tmpworkers.add(this.workers.get(i));
            }
        }

        this.workers = tmpworkers;
        List<ITask> workflowTasks = RandUtil.sample(this.tasks, ((SimParams)this.p).workflowTasks);
        properties = RandUtil.sample(this.workers, ((SimParams)this.p).workflowWorkers);
        return new PegasusWorkflow(((SimParams)this.p).numExecutions, workflowTasks, properties, (SimParams)this.p);
    }
}
