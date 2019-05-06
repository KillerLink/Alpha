package at.ac.tuwien.kr.alpha.common.depgraph.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import at.ac.tuwien.kr.alpha.common.depgraph.DependencyGraph;
import at.ac.tuwien.kr.alpha.common.depgraph.Edge;
import at.ac.tuwien.kr.alpha.common.depgraph.Node;

// TODO handle component graph separately
public class DependencyGraphWriter {

	private static final String DEFAULT_GRAPH_HEADING = "digraph dependencyGraph";

	private static final String DEFAULT_NODE_FORMAT = "n%d [label = \"%s\"]\n";
	private static final String DEFAULT_EDGE_FORMAT = "n%d -> n%d [xlabel=\"%s\" labeldistance=0.1]\n";

	public void writeAsDotfile(DependencyGraph graph, String path) throws IOException {
		this.writeAsDot(graph, new FileOutputStream(path));
	}

	public void writeAsDot(DependencyGraph graph, OutputStream out) throws IOException {
		this.writeAsDot(graph.getNodes(), out);
	}

	public void writeAsDot(Map<Node, List<Edge>> graph, OutputStream out) throws IOException {
		BiFunction<Node, Integer, String> nodeFormatter = this::buildNodeString;
		this.writeAsDot(graph, out, nodeFormatter);
	}

	private void writeAsDot(Map<Node, List<Edge>> graph, OutputStream out, BiFunction<Node, Integer, String> nodeFormatter) throws IOException {
		PrintStream ps = new PrintStream(out);
		this.startGraph(ps);

		Set<Map.Entry<Node, List<Edge>>> graphDataEntries = graph.entrySet();
		// first write all nodes
		int nodeCnt = 0;
		Map<Node, Integer> nodesToNumbers = new HashMap<>();
		for (Map.Entry<Node, List<Edge>> entry : graphDataEntries) {
			ps.print(nodeFormatter.apply(entry.getKey(), nodeCnt));
			nodesToNumbers.put(entry.getKey(), nodeCnt);
			nodeCnt++;
		}

		// now, write edges
		int fromNodeNum = -1;
		int toNodeNum = -1;
		for (Map.Entry<Node, List<Edge>> entry : graphDataEntries) {
			fromNodeNum = nodesToNumbers.get(entry.getKey());
			for (Edge edge : entry.getValue()) {
				toNodeNum = nodesToNumbers.get(edge.getTarget());
				ps.printf(DependencyGraphWriter.DEFAULT_EDGE_FORMAT, fromNodeNum, toNodeNum, edge.getSign() ? "+" : "-");
			}
		}

		this.finishGraph(ps);
		ps.close();
	}

	private void startGraph(PrintStream ps) {
		ps.println(DependencyGraphWriter.DEFAULT_GRAPH_HEADING);
		ps.println("{");
		ps.println("splines=false;");
		ps.println("ranksep=4.0;");
	}

	private void finishGraph(PrintStream ps) {
		ps.println("}");
	}

	private String buildNodeString(Node n, int nodeNum) {
		return String.format(DependencyGraphWriter.DEFAULT_NODE_FORMAT, nodeNum, n.getLabel());
	}

}
