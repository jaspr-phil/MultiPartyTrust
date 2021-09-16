package jaspr.provfsm.simple.workflows;

import jaspr.provfsm.core.NamedObject;
import jaspr.provfsm.core.ProvUtil;
import org.openprovenance.prov.model.Agent;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.QualifiedName;

import java.util.Map;

public class Worker extends NamedObject {

    public static final String PROV_NAMESPACE = "http://www.jaspr.org/worker";
    public static final String PROV_PREFIX = "worker";

    public String name;
    private Map<Task,Double> competencies;

    public Worker(String name, Map<Task, Double> competencies) {
        this.name = name;
        this.competencies = competencies;
    }

    public double taskCompetency(Task task) {
        return competencies.get(task);
    }

    public Agent asProv(ProvFactory factory) {
        QualifiedName qualifiedName = ProvUtil.namespace.qualifiedName(this.provPrefix(), this.name, factory);
        return factory.newAgent(qualifiedName, (String)null);
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
}
