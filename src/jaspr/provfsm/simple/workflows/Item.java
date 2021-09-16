package jaspr.provfsm.simple.workflows;

import jaspr.provfsm.core.NamedObject;
import jaspr.provfsm.core.ProvUtil;
import jaspr.provfsm.core.Util;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.QualifiedName;

public class Item extends NamedObject {

    public static final String PROV_NAMESPACE = "http://www.jaspr.org/item";
    public static final String PROV_PREFIX = "item";

    private String name;
    public double utility;

    public Item(String name, double utility) {
        this.name = name;
        this.utility = utility;
    }

    public Entity asProv(ProvFactory factory) {
        QualifiedName qualifiedName = ProvUtil.namespace.qualifiedName(this.provPrefix(), this.name(), factory);
        return factory.newEntity(qualifiedName, (String)null);
    }

    public String toString() {
        return super.toString()+"("+ Util.format(utility, 1)+")";
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
