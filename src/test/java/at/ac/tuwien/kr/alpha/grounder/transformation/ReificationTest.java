package at.ac.tuwien.kr.alpha.grounder.transformation;

import java.util.List;

import org.junit.Assert;

import at.ac.tuwien.kr.alpha.common.atoms.Atom;
import at.ac.tuwien.kr.alpha.common.program.InputProgram;
import at.ac.tuwien.kr.alpha.grounder.parser.ProgramParser;

public class ReificationTest {

	private static final String SIMPLE_FACT_ASP = "p(x).";
	private static final String INT_FACT_ASP = "q(1).";
	private static final String STRING_FACT_ASP = "q(\"bla\").";
	private static final String FUNCTERM_FACT_ASP = "r(f(x)).";
	private static final String COMPLEX_FACT_ASP = "";
	
	private final ProgramParser parser = new ProgramParser();

	/*
	 * Note: reified program should only contain facts.
	 */
	private List<Atom> reify(String asp) {
		InputProgram prog = parser.parse(asp);
		InputProgram reified = new Reification().apply(prog);
		Assert.assertEquals(0, reified.getRules().size());
		return reified.getFacts();
	}
	
//	@Test
//	public void reifySimpleFact() {
//		String asp = "p(x).";
//		AnswerSet reified = reify(asp);
//		TestUtils.assertAnswerSetsEqual(
//				"fact(0), fact_argument(0, arg(0, const(x))), fact_predicate(0, pred(p, 1))",
//				Collections.singleton(reified));
//	}
//
//	@Test
//	public void reifyIntegerFact() {
//		String asp = "q(1).";
//		AnswerSet reified = reify(asp);
//		TestUtils.assertAnswerSetsEqual(
//				"fact(0), fact_argument(0, arg(0, integer(1))), fact_predicate(0, pred(q, 1))",
//				Collections.singleton(reified));
//	}
//
//	@Test
//	public void reifyStringFact() {
//		String asp = "q(\"bla\").";
//		AnswerSet reified = reify(asp);
//		TestUtils.assertAnswerSetsEqual(
//				"fact(0), fact_argument(0, arg(0, string(\"bla\"))), fact_predicate(0, pred(q, 1))",
//				Collections.singleton(reified));
//	}
//
//	@Test
//	public void reifyFuncTermFact() {
//		String asp = "r(f(x)).";
//		AnswerSet reified = reify(asp);
//		TestUtils.assertAnswerSetsEqual("fact(0), fact_argument(0, arg(0, func(0))), fact_predicate(0, pred(r, 1)), func(0), "
//				+ "func_symbol(0, symbol(f, 1)), func_argument(0, arg(0, const(x)))",
//				Collections.singleton(reified));
//	}
//
//	@Test
//	public void reifyComplexFact() {
//		String asp = "complex(1, a, f(\"bla\", g(\"blubb\")), 3).";
//		AnswerSet reified = reify(asp);
//		TestUtils.assertAnswerSetsEqual("fact(0), fact_predicate(0, pred(complex, 4)), fact_argument(0, arg(0, integer(1))), "
//				+ "fact_argument(0, arg(1, const(a))), fact_argument(0, arg(2, func(0))), fact_argument(0, arg(3, integer(3))), "
//				+ "func(0), func_symbol(0, symbol(f, 2)), func_argument(0, arg(0, string(\"bla\"))), func_argument(0, arg(1, func(1))), "
//				+ "func(1), func_symbol(1, symbol(g, 1)), func_argument(1, arg(0, string(\"blubb\")))",
//				Collections.singleton(reified));
//	}
//
//	@Test
//	public void reifyPropositionalConstant() {
//		String asp = "a.";
//		AnswerSet reified = reify(asp);
//		TestUtils.assertAnswerSetsEqual("fact(0), fact_predicate(0, pred(a, 0))", Collections.singleton(reified));
//	}
//
//	@Test
//	public void reifyChoiceRuleHeadless() {
//		String asp = "{ either; or }.";
//		AnswerSet reified = reify(asp);
//		TestUtils.assertAnswerSetsEqual("haha", Collections.singleton(reified));
//	}

}
