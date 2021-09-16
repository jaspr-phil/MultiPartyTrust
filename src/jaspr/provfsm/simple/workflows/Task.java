package jaspr.provfsm.simple.workflows;

import jaspr.provfsm.core.NamedObject;
import jaspr.provfsm.core.ProvUtil;
import jaspr.provfsm.core.Util;
import org.openprovenance.prov.model.Activity;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.QualifiedName;

public class Task extends NamedObject {

    public static final String PROV_NAMESPACE = "http://www.jaspr.org/task";
    public static final String PROV_PREFIX = "task";


    private String name;
    public double priority;
    public boolean isHighPriority() {
        return priority > 0.5;
    }

    public Task(String name, double priority) {
        this.name = name;
        this.priority = priority;
    }

    public Activity asProv(ProvFactory factory) {
        QualifiedName qualifiedName = ProvUtil.namespace.qualifiedName(this.provPrefix(), this.name, factory);
        return factory.newActivity(qualifiedName, (String)null);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String provNamespace() {
        return PROV_NAMESPACE;
    }

    @Override
    public String provPrefix() {
        return PROV_PREFIX;
    }

    public String toString() {
        return super.toString()+"("+ Util.format(priority)+")";
    }
}

