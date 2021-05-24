package at.ac.tuwien.kr.alpha.grounder.transformation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Set;

import at.ac.tuwien.kr.alpha.common.Predicate;
import at.ac.tuwien.kr.alpha.common.atoms.Atom;
import at.ac.tuwien.kr.alpha.common.atoms.BasicAtom;
import at.ac.tuwien.kr.alpha.common.atoms.BasicLiteral;
import at.ac.tuwien.kr.alpha.common.atoms.Literal;
import at.ac.tuwien.kr.alpha.common.program.InputProgram;
import at.ac.tuwien.kr.alpha.common.rule.BasicRule;
import at.ac.tuwien.kr.alpha.common.rule.head.Head;
import at.ac.tuwien.kr.alpha.common.rule.head.NormalHead;
import at.ac.tuwien.kr.alpha.common.terms.ConstantTerm;
import at.ac.tuwien.kr.alpha.common.terms.FunctionTerm;
import at.ac.tuwien.kr.alpha.common.terms.Term;
import at.ac.tuwien.kr.alpha.common.terms.VariableTerm;

public class Reification extends ProgramTransformation<InputProgram, InputProgram> {

	private static class FunctionTermLogEntry {
		private final int id;
		private final FunctionTerm term;

		protected FunctionTermLogEntry(int id, FunctionTerm term) {
			this.id = id;
			this.term = term;
		}

		protected int getId() {
			return this.id;
		}

		protected FunctionTerm getTerm() {
			return this.term;
		}
	}

	private class FunctionTermLog {
		private Deque<FunctionTermLogEntry> funcTerms = new ArrayDeque<>();
		private int nextId;

		protected int append(FunctionTerm term) {
			int funcId = this.nextId++;
			this.funcTerms.addLast(new FunctionTermLogEntry(funcId, term));
			return funcId;
		}

		private List<Atom> generateOne(FunctionTermLogEntry p, String[] names) {
			List<Atom> generated = new ArrayList<>();
			generated.add(new BasicAtom(
					Predicate.getInstance(names[0], 1),
					ConstantTerm.getInstance(p.getId())));
			generated.add(new BasicAtom(
					Predicate.getInstance(names[1], 2),
					ConstantTerm.getInstance(p.getId()),
					FunctionTerm.getInstance("symbol",
							ConstantTerm.getSymbolicInstance(p.getTerm().getSymbol()),
							ConstantTerm.getInstance(p.getTerm().getTerms().size()))));
			Term idTerm = ConstantTerm.getInstance(p.getId());
			generated.addAll(generateTermDescriptions(p.getTerm().getTerms(), idTerm, names));
			return generated;
		}

		protected List<Atom> generateAll(String[] names) {
			names = new String[] {"func", "func_symbol", "func_argument" };
			List<Atom> generated = new ArrayList<>();
			while (!funcTerms.isEmpty()) {
				generated.addAll(generateOne(this.funcTerms.removeFirst(), names));
			}
			return generated;
		}
	}

	private final FunctionTermLog funcLog = new FunctionTermLog();

	@Override
	public InputProgram apply(InputProgram inputProgram) {

		List<Atom> metaFacts = new ArrayList<>();
		List<BasicRule> metaRules = new ArrayList<>();

		// TODO: convert into basic rules mit normal heads?
		int metaFactCount = 0;
		List<Atom> srcFacts = new ArrayList<>(inputProgram.getFacts());
		for (Atom srcFact : srcFacts) {
			String[] names = new String[] {"fact", "fact_predicate", "fact_argument" };
			Predicate factPredicate = srcFact.getPredicate();
			Term idTerm = ConstantTerm.getInstance(metaFactCount);
			metaFacts.add(new BasicAtom(Predicate.getInstance(names[0], 1), idTerm));
			metaFacts.add(new BasicAtom(
					Predicate.getInstance(names[1], 2),
					idTerm,
					FunctionTerm.getInstance("pred",
							ConstantTerm.getSymbolicInstance(factPredicate.getName()),
							ConstantTerm.getInstance(factPredicate.getArity()))));
			metaFacts.addAll(generateTermDescriptions(srcFact.getTerms(), idTerm, names));
			metaFactCount++;
		}

		int metaRuleCount = 0;
		List<BasicRule> srcRules = new ArrayList<>(inputProgram.getRules());
		for (BasicRule srcRule : srcRules) {
			String[] names = new String[] {"rule", "rule_head_predicate", "rule_head_argument" };
			Head ruleHead = srcRule.getHead();
			Predicate headPredicate = null;
			ConstantTerm<?> headType = null;
			List<Term> headTerms = null;
			if (ruleHead instanceof NormalHead) {
				headPredicate = ((NormalHead) ruleHead).getAtom().getPredicate();
				headTerms = ((NormalHead) ruleHead).getAtom().getTerms();
				headType = ConstantTerm.getSymbolicInstance("normal");
			} else {
				throw new RuntimeException("unhandled head type: " + ruleHead.getClass().getSimpleName());
			}
			Term idTerm = ConstantTerm.getInstance(metaRuleCount);
			metaFacts.add(new BasicAtom(Predicate.getInstance(names[0], 1), idTerm));
			metaFacts.add(new BasicAtom(Predicate.getInstance("rule_head_type", 2), idTerm, headType));
			metaFacts.add(new BasicAtom(
					Predicate.getInstance(names[1], 2),
					idTerm,
					FunctionTerm.getInstance("pred",
							ConstantTerm.getSymbolicInstance(headPredicate.getName()),
							ConstantTerm.getInstance(headPredicate.getArity()))));
			metaFacts.addAll(generateTermDescriptions(headTerms, idTerm, names));
			//
			Set<Literal> ruleLiterals = srcRule.getBody();
			int metaLiteralCount = 0;
			for (Literal literal : ruleLiterals) {
				String[] names2 = new String[] {"literal", "literal_predicate", "literal_argument" };
				String literalType = null;
				Predicate literalPredicate = null;
				if (literal instanceof BasicLiteral) {
					literalType = "basic";
					literalPredicate = ((BasicLiteral) literal).getAtom().getPredicate();
				} else {
					throw new RuntimeException("unhandled literal type: " + literal.getClass().getSimpleName());
				}
				Term idTerm2 = FunctionTerm.getInstance("id",
						ConstantTerm.getInstance(metaRuleCount),
						ConstantTerm.getInstance(metaLiteralCount));
				metaFacts.add(new BasicAtom(Predicate.getInstance("literal_type", 2), idTerm2, ConstantTerm.getInstance(literalType)));
				metaFacts.add(new BasicAtom(
						Predicate.getInstance(names[1], 2),
						idTerm2,
						FunctionTerm.getInstance("pred",
								ConstantTerm.getSymbolicInstance(literalPredicate.getName()),
								ConstantTerm.getInstance(literalPredicate.getArity()))));
				metaFacts.addAll(generateTermDescriptions(literal.getTerms(), idTerm2, names2));
				metaLiteralCount++;
			}
			metaRuleCount++;
		}

		InputProgram.Builder programBuilder = InputProgram.builder();
		InputProgram metaProgram = programBuilder
				.addRules(metaRules)
				.addFacts(metaFacts)
				.build();

		return metaProgram;
	}

	protected List<Atom> generateTermDescriptions(List<Term> terms, Term idTerm, String[] names) {
		List<Atom> generated = new ArrayList<>();
		int idx = 0;
		for (Term term : terms) {
			generated.addAll(generateTermDescription(term, idTerm, idx++, names));
			generated.addAll(funcLog.generateAll(names));
		}
		return generated;
	}

	protected List<Atom> generateTermDescription(Term term, Term idTerm, int argId, String[] names) {
		List<Atom> generated = new ArrayList<>();
		String argType = null;
		Term argValue = null;
		if (term instanceof ConstantTerm) {
			ConstantTerm<?> constTerm = (ConstantTerm<?>) term;
			if (constTerm.isSymbolic()) {
				argType = "const";
				argValue = ConstantTerm.getSymbolicInstance(constTerm.getObject().toString());
			} else {
				argType = constTerm.getObject().getClass().getSimpleName().toLowerCase();
				argValue = constTerm;
			}
		} else if (term instanceof FunctionTerm) {
			int funcId = this.funcLog.append((FunctionTerm) term);
			argType = "func";
			argValue = ConstantTerm.getInstance(funcId);
		} else if (term instanceof VariableTerm) {
			argType = "var";
			argValue = ConstantTerm.getInstance(((VariableTerm) term).toString());
		} else {
			throw new RuntimeException("unhandled term type: " + term.getClass().getSimpleName());
		}
		generated.add(new BasicAtom(
				Predicate.getInstance(names[2], 2),
				idTerm,
				FunctionTerm.getInstance("arg", ConstantTerm.getInstance(argId), FunctionTerm.getInstance(argType, argValue))));
		return generated;
	}

}
