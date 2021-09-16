package jaspr.provfsm.core;

import org.openprovenance.prov.model.Element;
import org.openprovenance.prov.model.ProvFactory;

public interface IWidget {
    Element asProv(ProvFactory factory);
}
