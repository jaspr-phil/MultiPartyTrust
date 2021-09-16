//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.models;

import org.openprovenance.prov.model.QualifiedName;

public class FullNodeNamer extends NodeNamer {
    public FullNodeNamer() {
    }

    public String name(QualifiedName node) {
        String name = node.getPrefix() + node.getLocalPart();
        return name;
    }
}
