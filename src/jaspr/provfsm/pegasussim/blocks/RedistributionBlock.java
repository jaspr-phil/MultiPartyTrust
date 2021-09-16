package jaspr.provfsm.pegasussim.blocks;

import jaspr.provfsm.core.ITask;
import jaspr.provfsm.core.IWidget;
import jaspr.provfsm.core.IWorker;
import jaspr.provfsm.core.Util;
import jaspr.provfsm.pegasussim.PegasusWorkflow;
import jaspr.provfsm.pegasussim.Task;
import jaspr.provfsm.pegasussim.Widget;
import jaspr.provfsm.pegasussim.Worker;

import java.util.*;

public class RedistributionBlock extends Block {

    public RedistributionBlock(int numInputs, int numOutputs, int numTasks, List<ITask> allTasks) {
//        super(numInputs, numOutputs*numTasks, numTasks, allTasks);//inputs, outputs, tasks
        super(numInputs, numOutputs, numTasks, allTasks);//inputs, outputs, tasks
    }

    public double getTaskInputUtility(List<IWidget> taskInputs) {
        List<Double> taskInputUtilities = new ArrayList();

        for(int i = 0; i < taskInputs.size(); ++i) {
            Widget taskInput = (Widget)taskInputs.get(i);
            taskInputUtilities.add(taskInput.meanProperty());
        }

        return Util.max(taskInputUtilities);
    }

    public double getTaskOutputUtility(double taskInputUtility, Worker worker, Task task, double outputDivisorWeight) {
        double outputDivisor = outputDivisorWeight == 0.0D ? 1.0D : outputDivisorWeight * (double)this.numOutputs();
        return taskInputUtility + worker.meanCompetency(task) * outputDivisor;
    }

    public double followWeight(Worker worker, Set<IWorker> otherWorkers) {
        double w = 1;
        for (IWorker ow : otherWorkers) {
            w *= worker.followWeight(ow);
        }
        return w;
    }

    @Override
    public List<IWidget> doTasks(PegasusWorkflow workflow, List<IWidget> inputs, List<IWorker> workers, Set<IWorker> otherWorkers) {

        List<IWidget> outputs = new ArrayList();
        int numTaskInputs = this.numInputs() / this.numTasks();
        int numTaskOutputs = this.numOutputs() / this.numTasks();
        int inputPos = 0;
        int outputPos = 0;
        Set<IWorker> prevWorkers = new HashSet(otherWorkers);


//        otherWorkers.clear();

        for(int ti = 0; ti < this.numTasks(); ++ti) {
            Task task = (Task)this.tasks.get(0);
            Worker worker = (Worker)workers.get(workflow.isSingleBlockWorker() ? 0 : ti);
            if (ti == this.numTasks() - 1) {
                numTaskInputs = this.numInputs() - inputPos;
                numTaskOutputs = this.numOutputs() - outputPos;
            }

//            List<IWidget> taskInputs = new ArrayList<>();
//            List<Double> taskInputUtilities = new ArrayList<>();
//            for (int ii=inputPos;ii<inputPos+numTaskInputs;ii++) {
//                Widget taskInput = (Widget)inputs.get(ii);
//                taskInputUtilities.add(taskInput.meanProperty());
//                taskInputs.add(taskInput);
//            }
//            inputPos = inputPos+numTaskInputs;
//
//            double taskInputUtility = Util.max(taskInputUtilities);

            List<IWidget> taskInputs = inputs.subList(inputPos, inputPos + numTaskInputs);
            inputPos += numTaskInputs;
            double fw = this.followWeight(worker, prevWorkers);
            double taskInputUtility = this.getTaskInputUtility(taskInputs) * fw;
            List<IWidget> taskOutputs = new ArrayList();

            for(int oi = outputPos; oi < outputPos + numTaskOutputs; ++oi) {
                double taskOutputUtility = this.getTaskOutputUtility(taskInputUtility, worker, task, workflow.getOutputDivisorWeight());
                Widget taskOutput = new Widget("" + workflow.nextOutputIndex(), taskOutputUtility);
                taskOutputs.add(taskOutput);
            }

            outputPos += numTaskOutputs;
//            System.out.println("\t\tWORKER: " + worker.name() + " x TASK: " + task.name() + " : " + taskInputs.size() + " -> " + taskOutputs.size());
            workflow.updateProv(taskInputs, worker, task, taskOutputs);
            outputs.addAll(taskOutputs);
            otherWorkers.add(worker);
        }

        return outputs;
    }
}
