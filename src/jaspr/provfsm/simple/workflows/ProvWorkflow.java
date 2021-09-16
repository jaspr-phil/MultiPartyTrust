package jaspr.provfsm.simple.workflows;

import jaspr.provfsm.core.Execution;
import jaspr.provfsm.core.IWorkflow;
import jaspr.provfsm.core.Workflow;

import java.util.List;

/**
 * Created by phil on 25/01/18.
 */
public class ProvWorkflow implements IWorkflow {

    List<Execution> executions;

    public ProvWorkflow(Workflow generator) {
        this.executions = generator.getExecutions();
    }

    public List<Execution> getExecutions() {
        return executions;
    }

    public String toString() {
        return executions.toString();
    }
}
