//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package jaspr.provfsm.core;

import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Namespace;
import org.openprovenance.prov.model.ProvFactory;

public class ProvUtil {
    public static final Namespace namespace = new Namespace();
    public static final ProvFactory factory = InteropFramework.newXMLProvFactory();

    public ProvUtil() {
    }

    public static void init() {
        namespace.register("item", "http://www.jaspr.org/item");
        namespace.register("task", "http://www.jaspr.org/task");
        namespace.register("worker", "http://www.jaspr.org/worker");
        namespace.register("Widget", "http://www.jaspr.org/Widget");
        namespace.register("Task", "http://www.jaspr.org/Task");
        namespace.register("Worker", "http://www.jaspr.org/Worker");
    }

    public static void writeDocument(String file, Document document) {
        InteropFramework intF = new InteropFramework();
        System.out.println(document);
        System.out.println(file);
        intF.writeDocument(file, document);
    }
}
