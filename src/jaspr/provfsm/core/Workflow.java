//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.core;

import jaspr.provfsm.simple.workflows.Item;
import jaspr.provfsm.simple.workflows.Task;
import jaspr.provfsm.simple.workflows.Worker;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Activity;
import org.openprovenance.prov.model.Agent;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.QualifiedName;
import org.openprovenance.prov.model.Used;
import org.openprovenance.prov.model.WasAssociatedWith;
import org.openprovenance.prov.model.WasAttributedTo;
import org.openprovenance.prov.model.WasDerivedFrom;
import org.openprovenance.prov.model.WasGeneratedBy;

public abstract class Workflow implements IWorkflow {
    protected final ProvFactory factory;
    protected Document document;
    protected double utility;
    protected boolean executed;
    protected List<Execution> executions;
    private int numExecutions;

    public Workflow(int numExecutions, ProvFactory factory) {
        this.executions = null;
        this.numExecutions = numExecutions;
        this.factory = factory;
        this.utility = 0.0D;
        this.executed = false;
    }

    public Workflow(int numExecutions) {
        this(numExecutions, InteropFramework.newXMLProvFactory());
    }

    public abstract void reset();

    public abstract void execute();

    public List<Execution> getExecutions() {
        if (this.executions == null) {
            this.executions = new ArrayList();

            for(int i = 0; i < this.numExecutions; ++i) {
                this.reset();
                this.execute();
                this.executions.add(new Execution(this.document, this.utility));
            }
        }

        return this.executions;
    }

    public void updateProv(IWidget inputItem, IWorker worker, ITask task, IWidget outputItem) {
        Agent agent = (Agent)worker.asProv(this.factory);
        this.document.getStatementOrBundle().add(agent);
        Activity activity = (Activity)task.asProv(this.factory);
        this.document.getStatementOrBundle().add(activity);
        WasAssociatedWith waw = this.factory.newWasAssociatedWith((QualifiedName)null, agent.getId(), activity.getId());
        this.document.getStatementOrBundle().add(waw);
        List<Entity> inputEntities = new ArrayList();
        Entity inputEntity = (Entity)inputItem.asProv(this.factory);
        inputEntities.add(inputEntity);
        this.document.getStatementOrBundle().add(inputEntity);
        Used u = this.factory.newUsed((QualifiedName)null, activity.getId(), inputEntity.getId());
        this.document.getStatementOrBundle().add(u);
        List<Entity> outputEntities = new ArrayList();
        Entity outputEntity = (Entity)outputItem.asProv(this.factory);
        outputEntities.add(outputEntity);
        this.document.getStatementOrBundle().add(outputEntity);
        WasAttributedTo wat = this.factory.newWasAttributedTo((QualifiedName)null, outputEntity.getId(), agent.getId());
        this.document.getStatementOrBundle().add(wat);
        WasGeneratedBy wgb = this.factory.newWasGeneratedBy((QualifiedName)null, outputEntity.getId(), activity.getId());
        this.document.getStatementOrBundle().add(wgb);
        WasDerivedFrom wdf = this.factory.newWasDerivedFrom((QualifiedName)null, outputEntity.getId(), inputEntity.getId());
        this.document.getStatementOrBundle().add(wdf);
        this.document.getStatementOrBundle().addAll(outputEntities);
    }

    public void updateProv(List<IWidget> inputItems, IWorker worker, ITask task, IWidget outputItem) {
        Agent agent = (Agent)worker.asProv(this.factory);
        this.document.getStatementOrBundle().add(agent);
        Activity activity = (Activity)task.asProv(this.factory);
        this.document.getStatementOrBundle().add(activity);
        WasAssociatedWith waw = this.factory.newWasAssociatedWith((QualifiedName)null, agent.getId(), activity.getId());
        this.document.getStatementOrBundle().add(waw);
        List<Entity> inputEntities = new ArrayList();
        Iterator var9 = inputItems.iterator();

        while(var9.hasNext()) {
            IWidget item = (IWidget)var9.next();
            Entity entity = (Entity)item.asProv(this.factory);
            inputEntities.add(entity);
            this.document.getStatementOrBundle().add(entity);
            Used u = this.factory.newUsed((QualifiedName)null, activity.getId(), entity.getId());
            this.document.getStatementOrBundle().add(u);
        }

        List<Entity> outputEntities = new ArrayList();
        Entity outputEntity = (Entity)outputItem.asProv(this.factory);
        outputEntities.add(outputEntity);
        this.document.getStatementOrBundle().add(outputEntity);
        WasAttributedTo wat = this.factory.newWasAttributedTo((QualifiedName)null, outputEntity.getId(), agent.getId());
        this.document.getStatementOrBundle().add(wat);
        WasGeneratedBy wgb = this.factory.newWasGeneratedBy((QualifiedName)null, outputEntity.getId(), activity.getId());
        this.document.getStatementOrBundle().add(wgb);
        Iterator var13 = inputEntities.iterator();

        while(var13.hasNext()) {
            Entity inputEntity = (Entity)var13.next();
            WasDerivedFrom wdf = this.factory.newWasDerivedFrom((QualifiedName)null, outputEntity.getId(), inputEntity.getId());
            this.document.getStatementOrBundle().add(wdf);
        }

        this.document.getStatementOrBundle().addAll(outputEntities);
    }

    public void updateProv(List<IWidget> inputItems, IWorker worker, ITask task, List<IWidget> outputItems) {
        Agent agent = (Agent)worker.asProv(this.factory);
        this.document.getStatementOrBundle().add(agent);
        Activity activity = (Activity)task.asProv(this.factory);
        this.document.getStatementOrBundle().add(activity);
        WasAssociatedWith waw = this.factory.newWasAssociatedWith((QualifiedName)null, agent.getId(), activity.getId());
        this.document.getStatementOrBundle().add(waw);
        List<Entity> inputEntities = new ArrayList();
        Iterator var9 = inputItems.iterator();

        while(var9.hasNext()) {
            IWidget item = (IWidget)var9.next();
            Entity entity = (Entity)item.asProv(this.factory);
            inputEntities.add(entity);
            this.document.getStatementOrBundle().add(entity);
            Used u = this.factory.newUsed((QualifiedName)null, activity.getId(), entity.getId());
            this.document.getStatementOrBundle().add(u);
        }

        List<Entity> outputEntities = new ArrayList();
        Iterator var19 = outputItems.iterator();

        while(var19.hasNext()) {
            IWidget outputItem = (IWidget)var19.next();
            Entity entity = (Entity)outputItem.asProv(this.factory);
            outputEntities.add(entity);
            this.document.getStatementOrBundle().add(entity);
            WasAttributedTo wat = this.factory.newWasAttributedTo((QualifiedName)null, entity.getId(), agent.getId());
            this.document.getStatementOrBundle().add(wat);
            WasGeneratedBy wgb = this.factory.newWasGeneratedBy((QualifiedName)null, entity.getId(), activity.getId());
            this.document.getStatementOrBundle().add(wgb);
            Iterator var15 = inputEntities.iterator();

            while(var15.hasNext()) {
                Entity inputEntity = (Entity)var15.next();
                WasDerivedFrom wdf = this.factory.newWasDerivedFrom((QualifiedName)null, entity.getId(), inputEntity.getId());
                this.document.getStatementOrBundle().add(wdf);
            }
        }

        this.document.getStatementOrBundle().addAll(outputEntities);
    }

    protected void updateProv(List<Item> inputItems, Worker worker, Task task, List<Item> outputItems) {
        Agent agent = worker.asProv(this.factory);
        this.document.getStatementOrBundle().add(agent);
        Activity activity = task.asProv(this.factory);
        this.document.getStatementOrBundle().add(activity);
        WasAssociatedWith waw = this.factory.newWasAssociatedWith((QualifiedName)null, agent.getId(), activity.getId());
        this.document.getStatementOrBundle().add(waw);
        List<Entity> inputEntities = new ArrayList();
        Iterator var9 = inputItems.iterator();

        while(var9.hasNext()) {
            Item item = (Item)var9.next();
            Entity entity = item.asProv(this.factory);
            inputEntities.add(entity);
            this.document.getStatementOrBundle().add(entity);
            Used u = this.factory.newUsed((QualifiedName)null, activity.getId(), entity.getId());
            this.document.getStatementOrBundle().add(u);
        }

        List<Entity> outputEntities = new ArrayList();
        Iterator var19 = outputItems.iterator();

        while(var19.hasNext()) {
            Item outputItem = (Item)var19.next();
            Entity entity = outputItem.asProv(this.factory);
            outputEntities.add(entity);
            this.document.getStatementOrBundle().add(entity);
            WasAttributedTo wat = this.factory.newWasAttributedTo((QualifiedName)null, entity.getId(), agent.getId());
            this.document.getStatementOrBundle().add(wat);
            WasGeneratedBy wgb = this.factory.newWasGeneratedBy((QualifiedName)null, entity.getId(), activity.getId());
            this.document.getStatementOrBundle().add(wgb);
            Iterator var15 = inputEntities.iterator();

            while(var15.hasNext()) {
                Entity inputEntity = (Entity)var15.next();
                WasDerivedFrom wdf = this.factory.newWasDerivedFrom((QualifiedName)null, entity.getId(), inputEntity.getId());
                this.document.getStatementOrBundle().add(wdf);
            }
        }

        this.document.getStatementOrBundle().addAll(outputEntities);
    }
}
