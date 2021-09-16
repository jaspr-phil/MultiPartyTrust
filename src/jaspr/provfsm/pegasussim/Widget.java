//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.pegasussim;

import jaspr.provfsm.core.IWidget;
import jaspr.provfsm.core.NamedObject;
import jaspr.provfsm.core.Util;
import java.util.ArrayList;
import java.util.List;
import org.openprovenance.prov.model.Element;
import org.openprovenance.prov.model.ProvFactory;

public class Widget extends NamedObject implements IWidget {
    private String shortName;
    private List<Double> properties;

    public Widget(String name, List<Double> properties) {
        super(name);
        this.shortName = name;
        this.properties = properties;
    }

    public Widget(String name, double property) {
        super(name);
        this.shortName = name;
        this.properties = new ArrayList();
        this.properties.add(property);
    }

    public String shortName() {
        return this.shortName;
    }

    public List<Double> getProperties() {
        return this.properties;
    }

    public double meanProperty() {
        return Util.mean(this.properties);
    }

    public String toString() {
        return super.toString() + "(" + Util.format(this.properties) + ")";
    }

    public Element asProv(ProvFactory factory) {
        return factory.newEntity(this.getQualifiedName(factory), (String)null);
    }
}
