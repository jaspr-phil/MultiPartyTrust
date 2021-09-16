//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.core;

import org.openprovenance.prov.model.Document;

public class Execution {
    private Document document;
    private double utility;

    public Execution(Document document, double utility) {
        this.document = document;
        this.utility = utility;
    }

    public Document getDocument() {
        return this.document;
    }

    public double getUtility() {
        return this.utility;
    }

    public boolean getSuccess() {
        return this.utility > 0.0D;
    }

    public String toString() {
        return "" + this.utility;
    }
}
