package jaspr.provfsm.core;

import org.openprovenance.prov.model.Element;
import org.openprovenance.prov.model.ProvFactory;

public interface ITask {
    Element asProv(ProvFactory factory);
}
