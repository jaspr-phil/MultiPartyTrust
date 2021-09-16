package jaspr.provfsm.core;

import org.openprovenance.prov.model.Element;
import org.openprovenance.prov.model.ProvFactory;

public interface IWorker {
    Element asProv(ProvFactory factory);
    String name();
}
