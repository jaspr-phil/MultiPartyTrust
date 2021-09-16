package jaspr.provfsm.epigenicssim;

import jaspr.provfsm.core.IWidget;
import jaspr.provfsm.core.NamedObject;
import jaspr.provfsm.core.Util;
import org.openprovenance.prov.model.Element;
import org.openprovenance.prov.model.ProvFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phil on 08/03/18.
 */
public class Widget extends NamedObject implements IWidget {

    private String shortName;

    private List<Double> properties;

    public Widget(String name, List<Double> properties) {
        super(name);
        shortName = name;
        this.properties = properties;
    }

    public Widget(String name, double property) {
        super(name);
        shortName = name;
        this.properties = new ArrayList<>();
        this.properties.add(property);
    }


    public String shortName() {
        return shortName;
    }

    public List<Double> getProperties() {
        return properties;
    }

    public String toString() {
        return super.toString()+"("+Util.format(properties)+")";
    }

    public Element asProv(ProvFactory factory) {
        return factory.newEntity(getQualifiedName(factory), (String)null);
    }
}
