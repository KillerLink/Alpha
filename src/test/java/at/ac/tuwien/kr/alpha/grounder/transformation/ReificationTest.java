package at.ac.tuwien.kr.alpha.grounder.transformation;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import at.ac.tuwien.kr.alpha.common.AnswerSet;
import at.ac.tuwien.kr.alpha.common.AtomStore;
import at.ac.tuwien.kr.alpha.common.AtomStoreImpl;
import at.ac.tuwien.kr.alpha.common.program.InputProgram;
import at.ac.tuwien.kr.alpha.common.program.InternalProgram;
import at.ac.tuwien.kr.alpha.config.SystemConfig;
import at.ac.tuwien.kr.alpha.grounder.Grounder;
import at.ac.tuwien.kr.alpha.grounder.GrounderFactory;
import at.ac.tuwien.kr.alpha.grounder.parser.ProgramParser;
import at.ac.tuwien.kr.alpha.solver.SolverFactory;
import at.ac.tuwien.kr.alpha.test.util.TestUtils;

public class ReificationTest {

	private final ProgramParser parser = new ProgramParser();

	/*
	 * Note: reified program should only contain facts. Return answer set here so that test methods can use
	 * assertAnswerSets rather than hand-crafting atoms for checks
	 */
	private AnswerSet reify(String asp) {
		InputProgram prog = parser.parse(asp);
		InputProgram reified = new Reification().apply(prog);
		Assert.assertEquals(0, reified.getRules().size());
		InternalProgram internalProg = InternalProgram.fromNormalProgram(new NormalizeProgramTransformation(false).apply(reified));
		AtomStore atomStore = new AtomStoreImpl();
		Grounder grounder = GrounderFactory.getInstance("naive", internalProg, atomStore, false);
		List<AnswerSet> answerSets = SolverFactory.getInstance(new SystemConfig(), atomStore, grounder).collectList();
		Assert.assertEquals(1, answerSets.size());
		return answerSets.get(0);
	}

	@Test
	public void reifySimpleFact() {
		String asp = "p(x).";
		AnswerSet reified = reify(asp);
		TestUtils.assertAnswerSetsEqual(
				"fact(0), fact_predicate(0, pred(p, 1)), fact_argument(0, arg(0, const(x)))",
				Collections.singleton(reified));
	}

}
