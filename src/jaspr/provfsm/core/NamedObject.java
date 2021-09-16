package jaspr.provfsm.core;

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

    public QualifiedName getQualifiedName(ProvFactory factory) {
//        System.out.println(this.provPrefix()+"  "+this.name());
        return ProvUtil.namespace.qualifiedName(this.provPrefix(), this.name(), factory);
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
