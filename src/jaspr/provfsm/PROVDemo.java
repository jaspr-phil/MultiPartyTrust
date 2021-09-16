package jaspr.provfsm;


import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.*;

import java.util.Arrays;

public class PROVDemo {

    public static final String PROVBOOK_NS = "http://www.provbook.org";
    public static final String PROVBOOK_PREFIX = "provbook";

    public static final String JIM_PREFIX = "jim";
    public static final String JIM_NS = "http://www.cs.rpi.edu/~hendler/";

    private final ProvFactory pFactory;
    private final Namespace ns;
    public PROVDemo(ProvFactory pFactory) {
        this.pFactory = pFactory;
        ns=new Namespace();
        ns.addKnownNamespaces();
        ns.register(PROVBOOK_PREFIX, PROVBOOK_NS);
        ns.register(JIM_PREFIX, JIM_NS);
    }

    public QualifiedName qn(String n) {
        return ns.qualifiedName(PROVBOOK_PREFIX, n, pFactory);
    }

    public Document makeDocument() {
        Entity quote = pFactory.newEntity(qn("a-little-provenance-goes-a-long-way"));
        quote.setValue(pFactory.newValue("A little provenance goes a long way",
                pFactory.getName().XSD_STRING));

        Entity original = pFactory.newEntity(ns.qualifiedName(JIM_PREFIX,"LittleSemanticsWeb.html",pFactory));

        Agent paul = pFactory.newAgent(qn("Paul"), "Paul Groth");
        Agent luc = pFactory.newAgent(qn("Luc"), "Luc Moreau");

        WasAttributedTo attr1 = pFactory.newWasAttributedTo(null,
                quote.getId(),
                paul.getId());
        WasAttributedTo attr2 = pFactory.newWasAttributedTo(null,
                quote.getId(),
                luc.getId());

        WasDerivedFrom wdf = pFactory.newWasDerivedFrom(quote.getId(),
                original.getId());

        Document document = pFactory.newDocument();
        document.getStatementOrBundle()
                .addAll(Arrays.asList(new StatementOrBundle[] { quote,
                        paul,
                        luc,
                        attr1,
                        attr2,
                        original,
                        wdf }));
        document.setNamespace(ns);
        return document;
    }

    public void doConversions(Document document, String file) {
        InteropFramework intF=new InteropFramework();
        intF.writeDocument(file, document);
        intF.writeDocument(System.out, InteropFramework.ProvFormat.PROVN, document);
    }

    public void closingBanner() {
        System.out.println("");
        System.out.println("*************************");
    }

    public void openingBanner() {
        System.out.println("*************************");
        System.out.println("* Converting document  ");
        System.out.println("*************************");
    }

    public static void main(String [] args) {
        String file="./test.svg";

        PROVDemo little=new PROVDemo(InteropFramework.newXMLProvFactory());
        little.openingBanner();
        Document document = little.makeDocument();
        little.doConversions(document, file);
        little.closingBanner();

    }
}
