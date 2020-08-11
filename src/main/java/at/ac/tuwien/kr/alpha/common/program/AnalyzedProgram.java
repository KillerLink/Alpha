package at.ac.tuwien.kr.alpha.common.program;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;

import at.ac.tuwien.kr.alpha.common.atoms.Atom;
import at.ac.tuwien.kr.alpha.common.depgraph.ComponentGraph;
import at.ac.tuwien.kr.alpha.common.depgraph.DependencyGraph;
import at.ac.tuwien.kr.alpha.common.depgraph.SccResult;
import at.ac.tuwien.kr.alpha.common.depgraph.StronglyConnectedComponentsHelper;
import at.ac.tuwien.kr.alpha.common.rule.InternalRule;

public class AnalyzedProgram extends InternalProgram {

	private final DependencyGraph dependencyGraph;
	private final ComponentGraph componentGraph;

	public AnalyzedProgram(List<InternalRule> rules, List<Atom> facts) {
		super(rules, facts);
		this.dependencyGraph = DependencyGraph.buildDependencyGraph(this.getRulesById());
		this.componentGraph = this.buildComponentGraph(this.dependencyGraph);
	}

	public static AnalyzedProgram analyzeNormalProgram(NormalProgram prog) {
		ImmutablePair<List<InternalRule>, List<Atom>> rulesAndFacts = InternalProgram.internalizeRulesAndFacts(prog);
		return new AnalyzedProgram(rulesAndFacts.left, rulesAndFacts.right);
	}

	private ComponentGraph buildComponentGraph(DependencyGraph depGraph) {
		StronglyConnectedComponentsHelper sccHelper = new StronglyConnectedComponentsHelper();
		SccResult sccResult = sccHelper.findStronglyConnectedComponents(depGraph);
		return ComponentGraph.buildComponentGraph(depGraph, sccResult);
	}

	public ComponentGraph getComponentGraph() {
		return this.componentGraph;
	}

	public DependencyGraph getDependencyGraph() {
		return this.dependencyGraph;
	}

}
