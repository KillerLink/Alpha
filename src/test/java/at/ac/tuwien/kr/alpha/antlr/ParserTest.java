package at.ac.tuwien.kr.alpha.antlr;

import at.ac.tuwien.kr.alpha.common.BasicPredicate;
import at.ac.tuwien.kr.alpha.common.Program;
import at.ac.tuwien.kr.alpha.common.atoms.BasicAtom;
import at.ac.tuwien.kr.alpha.common.atoms.BuiltinAtom;
import at.ac.tuwien.kr.alpha.common.atoms.ChoiceHead;
import at.ac.tuwien.kr.alpha.common.atoms.Literal;
import at.ac.tuwien.kr.alpha.common.terms.ConstantTerm;
import at.ac.tuwien.kr.alpha.common.terms.FunctionTerm;
import at.ac.tuwien.kr.alpha.common.terms.IntervalTerm;
import at.ac.tuwien.kr.alpha.common.terms.VariableTerm;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static at.ac.tuwien.kr.alpha.Main.parseVisit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Copyright (c) 2016, the Alpha Team.
 */
public class ParserTest {
	@Test
	public void parseFact() throws IOException {
		Program parsedProgram = parseVisit("p(a,b).");

		assertEquals("Program contains one fact.", 1, parsedProgram.getFacts().size());
		assertEquals("Predicate name of fact is p.", "p", parsedProgram.getFacts().get(0).getPredicate().getPredicateName());
		assertEquals("Fact has two terms.", 2, parsedProgram.getFacts().get(0).getPredicate().getArity());
		assertEquals("First term is a.", "a", ((ConstantTerm)parsedProgram.getFacts().get(0).getTerms().get(0)).toString());
		assertEquals("Second term is b.", "b", ((ConstantTerm)parsedProgram.getFacts().get(0).getTerms().get(1)).toString());
	}

	@Test
	public void parseFactWithFunctionTerms() throws IOException {
		Program parsedProgram = parseVisit("p(f(a),g(h(Y))).");

		assertEquals("Program contains one fact.", 1, parsedProgram.getFacts().size());
		assertEquals("Predicate name of fact is p.", "p", parsedProgram.getFacts().get(0).getPredicate().getPredicateName());
		assertEquals("Fact has two terms.", 2, parsedProgram.getFacts().get(0).getPredicate().getArity());
		assertEquals("First term is function term f.", "f", ((FunctionTerm)parsedProgram.getFacts().get(0).getTerms().get(0)).getSymbol().getSymbol());
		assertEquals("Second term is function term g.", "g", ((FunctionTerm)parsedProgram.getFacts().get(0).getTerms().get(1)).getSymbol().getSymbol());
	}

	@Test
	public void parseSmallProgram() throws IOException {
		Program parsedProgram = parseVisit("a :- b, not d.\n" +
			"c(X) :- p(X,a,_), q(Xaa,xaa)." +
				":- f(Y).");

		assertEquals("Program contains three rules.", 3, parsedProgram.getRules().size());
	}

	@Test(expected = RecognitionException.class)
	public void parseBadSyntax() throws IOException {
		parseVisit("Wrong Syntax.");
	}

	@Test
	public void parseBuiltinAtom() throws IOException {
		Program parsedProgram = parseVisit("a :- p(X), X != Y, q(Y).");
		assertEquals(1, parsedProgram.getRules().size());
		assertEquals(3, parsedProgram.getRules().get(0).getBody().size());
	}

	@Test(expected = UnsupportedOperationException.class)
	// Change expected after Alpha can deal with disjunction.
	public void parseProgramWithDisjunctionInHead() throws IOException {
		parseVisit("r(X) | q(X) :- q(X).\nq(a).\n");
	}

	@Test
	public void parseInterval() throws IOException {
		Program parsedProgram = parseVisit("fact(2..5). p(X) :- q(a, 3 .. X).");
		IntervalTerm factInterval = (IntervalTerm) parsedProgram.getFacts().get(0).getTerms().get(0);
		assertTrue(factInterval.equals(IntervalTerm.getInstance(ConstantTerm.getInstance("2"), ConstantTerm.getInstance("5"))));
		IntervalTerm bodyInterval = (IntervalTerm) parsedProgram.getRules().get(0).getBody().get(0).getTerms().get(1);
		assertTrue(bodyInterval.equals(IntervalTerm.getInstance(ConstantTerm.getInstance("3"), VariableTerm.getInstance("X"))));
	}

	@Test
	public void parseChoiceRule() throws IOException {
		Program parsedProgram = parseVisit("dom(1). dom(2). { a ; b } :- dom(X).");
		ChoiceHead choiceHead = (ChoiceHead) parsedProgram.getRules().get(0).getHead();
		BasicAtom atomA = new BasicAtom(new BasicPredicate("a", 0));
		assertEquals(2, choiceHead.getChoiceElements().size());
		assertTrue(choiceHead.getChoiceElements().get(0).getKey().toString().equals("a"));
		assertTrue(choiceHead.getChoiceElements().get(1).getKey().toString().equals("b"));
		assertEquals(null, choiceHead.getLowerBound());
		assertEquals(null, choiceHead.getUpperBound());
	}

	@Test
	public void parseChoiceRuleBounded() throws IOException {
		Program parsedProgram = parseVisit("dom(1). dom(2). 1 < { a: p(v,w), not r; b } <= 13 :- dom(X). foo.");
		ChoiceHead choiceHead = (ChoiceHead) parsedProgram.getRules().get(0).getHead();
		BasicAtom atomA = new BasicAtom(new BasicPredicate("a", 0));
		assertEquals(2, choiceHead.getChoiceElements().size());
		assertTrue(choiceHead.getChoiceElements().get(0).getKey().toString().equals("a"));
		assertTrue(choiceHead.getChoiceElements().get(1).getKey().toString().equals("b"));
		List<Literal> conditionalLiterals = choiceHead.getChoiceElements().get(0).getValue();
		assertEquals(2, conditionalLiterals.size());
		assertFalse(conditionalLiterals.get(0).isNegated());
		assertTrue(conditionalLiterals.get(1).isNegated());
		assertEquals(ConstantTerm.getInstance("1"), choiceHead.getLowerBound());
		assertEquals(BuiltinAtom.BINOP.LT, choiceHead.getLowerOp());
		assertEquals(ConstantTerm.getInstance("13"), choiceHead.getUpperBound());
		assertEquals(BuiltinAtom.BINOP.LE, choiceHead.getUpperOp());
	}
}