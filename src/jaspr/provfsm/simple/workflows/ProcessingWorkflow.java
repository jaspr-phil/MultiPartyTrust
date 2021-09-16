package jaspr.provfsm.simple.workflows;

import jaspr.provfsm.core.ProvUtil;
import jaspr.provfsm.core.Workflow;
import org.openprovenance.prov.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phil on 10/11/17.
 */
public abstract class ProcessingWorkflow extends Workflow {

    final List<Item> _items;
    final List<Task> _tasks;
    final List<Worker> _workers;

    List<Item> items;
    List<Task> tasks;
    List<Worker> workers;

    public ProcessingWorkflow(List<Item> items, List<Task> tasks, List<Worker> workers, int numExecutions) {
        super(numExecutions);

        this._items = new ArrayList<>(items);
        this._tasks = new ArrayList<>(tasks);
        this._workers = new ArrayList<>(workers);

        this.reset();
    }

    public void reset() {
        this.items = new ArrayList<>(this._items);
        this.tasks = new ArrayList<>(this._tasks);
        this.workers = new ArrayList<>(this._workers);

        this.document = factory.newDocument();
        document.setNamespace(ProvUtil.namespace);
        this.utility = 0.;
        this.executed = false;

//        Worker worker = new Worker("init", null);
//        Task task = new Task("init", 0.);
//        Worker agent = worker.asProv(this.factory);
//        Task activity = task.asProv(this.factory);
//        document.getStatementOrBundle().add(agent);
//        document.getStatementOrBundle().add(activity);
//
//        WasAssociatedWith waw = this.factory.newWasAssociatedWith(null, activity.getId(), agent.getId());
//        document.getStatementOrBundle().add(waw);

        for (Item item : items) {
            Entity entity = item.asProv(this.factory);
            document.getStatementOrBundle().add(entity);
//            WasAttributedTo wat = this.factory.newWasAttributedTo(null, entity.getId(), agent.getId());
//            WasGeneratedBy wgb = this.factory.newWasGeneratedBy(null, entity.getId(), activity.getId());
//            document.getStatementOrBundle().add(wat);
//            document.getStatementOrBundle().add(wgb);
        }
    }


}
