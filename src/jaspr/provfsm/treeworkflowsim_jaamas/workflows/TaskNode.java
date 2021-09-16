package jaspr.provfsm.treeworkflowsim_jaamas.workflows;

import jaspr.provfsm.core.RandUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phil on 09/04/18.
 */
public class TaskNode {

    boolean isOr;
    Task task;
    Worker worker;
    List<TaskNode> nexts;

    public TaskNode(Task task, Worker worker, boolean isOr) {
        this.isOr = isOr;
        this.task = task;
        this.worker = worker;
        this.nexts = new ArrayList<>();
    }

    public void addNexts(TaskNode node) {
        nexts.add(node);
    }

    public Task getTask() {
        return task;
    }
    public Worker getWorker() {
        return worker;
    }

    public boolean isOr() {
        return isOr;
    }

    public TaskNode getNext() {
        return RandUtil.choose(nexts);
    }

    public List<TaskNode> getNexts() {
        return nexts;
    }

    public boolean hasNext() {
        return !nexts.isEmpty();
    }

    public String toString() {
        return (isOr ? "OR" : "AND") + ">" + task.toString();
    }
}
