package jaspr.provfsm.treeworkflowsim_jaamas.workflows;

import jaspr.provfsm.core.ProvUtil;
import org.openprovenance.prov.model.*;

import java.util.HashMap;
import java.util.Map;

public abstract class NamedObject {

    public NamedObject() {
        this.name();
    }

    public NamedObject(String name) {
        this._name = name;
    }

    public String toString() {
        return this.provPrefix()+":"+name();
    }

    private String _name = null;

    public String name() {
        if (_name == null) {
            _name = nextName();
        }
        return _name;
    }

    public String provNamespace() {
        return "http://www.jaspr.org/"+provPrefix();
    }

    public String provPrefix(){
        return this.getClass().getSimpleName();
    }

    public Element asProv(ProvFactory factory) {
        QualifiedName qualifiedName = ProvUtil.namespace.qualifiedName(this.provPrefix(), this.name(), factory);
        if (this.provPrefix().equals("Worker")) return factory.newAgent(qualifiedName, (String)null);
        else if (this.provPrefix().equals("Task")) return factory.newActivity(qualifiedName, (String)null);
        else if (this.provPrefix().equals("Widget")) return factory.newEntity(qualifiedName, (String)null);
        System.out.println("Didn't match: "+this.provPrefix());
        return null;
    }

    public QualifiedName provName(Namespace namespace, ProvFactory provFactory) {
        return namespace.qualifiedName(this.provNamespace(), this.name(), provFactory);
    }

    public void registerWithNamespace(Namespace ns) {
        ns.register(provPrefix(), provNamespace());
    }

    private static Map<String,Integer> currentNames = new HashMap<>();
    public String nextName() {
        String classname = this.getClass().getName();
        if (!currentNames.containsKey(classname)) {
            currentNames.put(classname, 0);
            return ""+0;
        } else {
            int current = currentNames.get(classname);
            currentNames.put(classname, current+1);
            return ""+current;
        }

    }
}
