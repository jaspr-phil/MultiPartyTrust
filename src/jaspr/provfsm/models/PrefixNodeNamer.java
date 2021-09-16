//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.models;

import jaspr.provfsm.simple.workflows.Task;
import jaspr.provfsm.simple.workflows.Worker;
import jaspr.provfsm.treeworkflowsim.workflows.Widget;
import org.openprovenance.prov.model.QualifiedName;

public class PrefixNodeNamer extends NodeNamer {
    public PrefixNodeNamer() {
    }

    public String name(QualifiedName node) {
        if (node.getPrefix().equals(Task.class.getSimpleName())) {
            return node.getPrefix() + node.getLocalPart();
        } else if (node.getPrefix().equals(Worker.class.getSimpleName())) {
            return node.getPrefix() + node.getLocalPart();
        } else {
            return node.getPrefix().equals(Widget.class.getSimpleName()) ? node.getPrefix() : null;
        }
    }
}
