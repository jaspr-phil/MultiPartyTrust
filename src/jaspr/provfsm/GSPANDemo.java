package jaspr.provfsm;

import de.parsemis.Miner;
import de.parsemis.graph.*;
import de.parsemis.miner.environment.LocalEnvironment;
import de.parsemis.miner.environment.Settings;
import de.parsemis.miner.general.Fragment;
import de.parsemis.miner.general.IntFrequency;
import de.parsemis.parsers.*;
import de.parsemis.strategy.RecursiveStrategy;

import java.util.ArrayList;
import java.util.Collection;

public class GSPANDemo {

    public static void main(String[] args) {

        GraphParser<String,String> parser = new DotGraphParser<>(new StringLabelParser(), new StringLabelParser());
        Settings<String,String> settings = new Settings<>();
        settings.serializer = parser;
        System.out.println(settings);
        LocalEnvironment.create(settings, 0, null, null, "NULLNODE", "NULLEDGE", null);

        HPMutableGraph<String, String> graph = new HPListGraph<>();
        graph.addNodeIndex("A");
        graph.addNodeIndex("B");
        graph.addNodeIndex("C");
        graph.addNodeIndex("A");
        graph.addNodeIndex("B");
        graph.addNodeIndex("C");
        graph.addEdgeIndex(0, 1, "a", Edge.OUTGOING);
        graph.addEdgeIndex(0, 2, "b", Edge.OUTGOING);
        graph.addEdgeIndex(0, 3, "a", Edge.OUTGOING);
        graph.addEdgeIndex(2, 2, "b", Edge.OUTGOING);
        graph.addEdgeIndex(2, 3, "a", Edge.OUTGOING);
        graph.addEdgeIndex(2, 4, "b", Edge.OUTGOING);
        graph.addEdgeIndex(3, 4, "a", Edge.OUTGOING);
        graph.addEdgeIndex(3, 5, "b", Edge.OUTGOING);
        graph.addEdgeIndex(4, 5, "a", Edge.OUTGOING);

        System.out.println(graph);

        Collection<Graph<String,String>> graphs = new ArrayList<>();
        graphs.add(graph.toGraph());
        graphs.add(graph.toGraph());
        graphs.add(graph.toGraph());
        graphs.add(graph.toGraph());

        settings.minFreq = new IntFrequency(1);
        settings.minNodes = 3;
        settings.maxNodes = Integer.MAX_VALUE;
//        settings.maxFreq = new IntFrequency(10);
        settings.factory = new HPListGraph.Factory<>(parser.getNodeParser(), parser.getEdgeParser());
        settings.algorithm = new de.parsemis.algorithms.gSpan.Algorithm<>();
        settings.strategy = new RecursiveStrategy<>();
        Collection<Fragment<String, String>> subgraphs = Miner.mine(graphs, settings);

        for (Fragment<String,String> sg : subgraphs) {
            System.out.println(sg.toGraph());
        }


        HPMutableGraph<String, String> graph2 = new HPListGraph<>();
        graph2.addNodeIndex("A");
        graph2.addNodeIndex("B");
        graph2.addNodeIndex("C");
        graph2.addNodeIndex("A");
        graph2.addNodeIndex("B");
        graph2.addNodeIndex("C");
//        graph2.addEdgeIndex(0, 1, "a", Edge.OUTGOING);
//        graph2.addEdgeIndex(0, 2, "b", Edge.OUTGOING);
//        graph2.addEdgeIndex(0, 3, "a", Edge.OUTGOING);
//        graph2.addEdgeIndex(2, 2, "b", Edge.OUTGOING);
//        graph2.addEdgeIndex(2, 3, "a", Edge.OUTGOING);
//        graph2.addEdgeIndex(2, 4, "b", Edge.OUTGOING);
//        graph2.addEdgeIndex(3, 4, "a", Edge.OUTGOING);
//        graph2.addEdgeIndex(3, 5, "b", Edge.OUTGOING);
//        graph2.addEdgeIndex(4, 5, "a", Edge.OUTGOING);

        graph2.addEdgeIndex(3, 1, "a", Edge.OUTGOING);
        graph2.addEdgeIndex(3, 2, "b", Edge.OUTGOING);
        graph2.addEdgeIndex(3, 0, "a", Edge.OUTGOING);
        graph2.addEdgeIndex(2, 2, "b", Edge.OUTGOING);
        graph2.addEdgeIndex(2, 0, "a", Edge.OUTGOING);
        graph2.addEdgeIndex(2, 4, "b", Edge.OUTGOING);
        graph2.addEdgeIndex(0, 4, "a", Edge.OUTGOING);
        graph2.addEdgeIndex(0, 5, "b", Edge.OUTGOING);
        graph2.addEdgeIndex(4, 5, "a", Edge.OUTGOING);
//        graph2.addEdgeIndex(0, 5, "a", Edge.OUTGOING);


        SubGraphComparator<String,String> subgraphComparator = new SubGraphComparator<>();
        boolean isSubgraph = subgraphComparator.compare(graph2.toGraph(), graph.toGraph()) == 0;

        System.out.println("Graph2 is subgraph of Graph: "+isSubgraph);

    }
}
