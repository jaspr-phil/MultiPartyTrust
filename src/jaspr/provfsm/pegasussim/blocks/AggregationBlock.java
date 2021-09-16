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

public class AggregationBlock extends RedistributionBlock {

    public AggregationBlock(int numInputs, int numOutputs, List<ITask> allTasks) {
        super(numInputs, numOutputs, numOutputs, allTasks);//inputs, outputs, tasks
//        super(numInputs, 1, numOutputs, allTasks);//inputs, outputs/tasks, tasks
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
//        double outputDivisor = outputDivisorWeight==0 ? 1 : outputDivisorWeight * (numInputs()+numOutputs());
//        return (taskInputUtility + worker.meanCompetency(task)) / outputDivisor;
//    }

//    @Override
//    public List<IWidget> doTasks(PegasusWorkflow workflow, List<IWidget> inputs, List<IWorker> workers) {
//        List<IWidget> outputs = new ArrayList<>();
//
//        int numTaskInputs = numInputs() / numTasks();
//        int numTaskOutputs = numOutputs() / numTasks();
//
//        int inputPos = 0;
//        int outputPos = 0;
//
//        for (int ti=0;ti<numTasks();ti++) {
//            Task task = (Task) tasks.get(ti);
//            Worker worker = (Worker) workers.get(ti);
//
//            if (ti == numTasks()-1) {
//                numTaskInputs = numInputs() - inputPos;
//                numTaskOutputs = numOutputs() - outputPos;
//            }
//
//            List<IWidget> taskInputs = new ArrayList<>();
//            double taskInputUtility = 0;
//            for (int ii=inputPos;ii<inputPos+numTaskInputs;ii++) {
//                Widget taskInput = (Widget)inputs.get(ii);
//                taskInputUtility += taskInput.meanProperty();
//                taskInputs.add(taskInput);
//            }
//            inputPos = inputPos+numTaskInputs;
//
//            List<IWidget> taskOutputs = new ArrayList<>();
//            for (int oi=outputPos;oi<outputPos+numTaskOutputs;oi++) {
//                double taskOutputUtility = taskInputUtility + 1;
//                Widget taskOutput = new Widget("" + workflow.nextOutputIndex(), taskOutputUtility);
//                taskOutputs.add(taskOutput);
//            }
//            outputPos = outputPos+numTaskOutputs;
//
//            System.out.println("\t\tWORKER: "+worker.name()+" x TASK: "+task.name()+" : "+taskInputs.size()+" -> "+taskOutputs.size());
//            workflow.updateProv(taskInputs, worker, task, taskOutputs);
//            outputs.addAll(taskOutputs);
//        }
//
//        return outputs;
//    }
}
