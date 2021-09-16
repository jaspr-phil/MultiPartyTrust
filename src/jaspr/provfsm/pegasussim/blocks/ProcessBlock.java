package jaspr.provfsm.pegasussim.blocks;

import jaspr.provfsm.core.ITask;
import jaspr.provfsm.core.IWidget;
import jaspr.provfsm.core.IWorker;
import jaspr.provfsm.core.Util;
import jaspr.provfsm.pegasussim.PegasusWorkflow;
import jaspr.provfsm.pegasussim.Task;
import jaspr.provfsm.pegasussim.Widget;
import jaspr.provfsm.pegasussim.Worker;

import java.util.ArrayList;
import java.util.List;

public class ProcessBlock extends RedistributionBlock {

    public ProcessBlock(int numInputs, List<ITask> allTasks) {
        super(numInputs, numInputs, numInputs, allTasks);//inputs, outputs, tasks
//        super(numInputs, 1, numInputs, allTasks);//inputs/tasks, outputs, tasks
    }

//    @Override
//    public double getTaskInputUtility(List<IWidget> taskInputs) {
//        List<Double> taskInputUtilities = new ArrayList<>();
//        for (int i=0;i<taskInputs.size();i++) {
//            Widget taskInput = (Widget)taskInputs.get(i);
//            taskInputUtilities.add(taskInput.meanProperty());
//        }
//        return Util.max(taskInputUtilities);
//    }

//    @Override
//    public double getTaskOutputUtility(double taskInputUtility, Worker worker, Task task, double outputDivisorWeight) {
////        double outputDivisor = outputDivisorWeight==0 ? 1 : outputDivisorWeight * numOutputs();
//        return (taskInputUtility + worker.meanCompetency(task));
//    }


//    @Override
//    public List<IWidget> doTasks(PegasusWorkflow workflow, List<IWidget> inputs, List<IWorker> workers) {
//        List<IWidget> outputs = new ArrayList<>();
//
//        for (int ti=0;ti<numTasks();ti++) {
//            Task task = (Task) tasks.get(ti);
//            Worker worker = (Worker) workers.get(ti);
//            Widget taskInput = (Widget)inputs.get(ti);
//
//            double taskInputUtility = taskInput.meanProperty();
//            double taskOutputUtility = taskInputUtility + 1;
//            Widget taskOutput = new Widget("" + workflow.nextOutputIndex(), taskOutputUtility);
//
//            System.out.println("\t\tWORKER: "+worker.name()+" x TASK: "+task.name()+" : "+taskInputs.size()+" -> "+taskOutputs.size());
//            outputs.add(taskOutput);
//            workflow.updateProv(taskInput, worker, task, taskOutput);
//        }
//
//        return outputs;
//    }
}
