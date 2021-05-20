package at.ac.tuwien.kr.alpha.grounder.transformation;

import at.ac.tuwien.kr.alpha.common.program.InputProgram;
import at.ac.tuwien.kr.alpha.common.program.NormalProgram;

import at.ac.tuwien.kr.alpha.common.Predicate;
import at.ac.tuwien.kr.alpha.common.atoms.Atom;
import at.ac.tuwien.kr.alpha.common.atoms.BasicAtom;
import at.ac.tuwien.kr.alpha.common.atoms.Literal;
import at.ac.tuwien.kr.alpha.common.atoms.BasicLiteral;
import at.ac.tuwien.kr.alpha.common.Literals;
import at.ac.tuwien.kr.alpha.common.program.InputProgram;
import at.ac.tuwien.kr.alpha.common.rule.BasicRule;
import at.ac.tuwien.kr.alpha.common.rule.head.ChoiceHead;
import at.ac.tuwien.kr.alpha.common.rule.head.Head;
import at.ac.tuwien.kr.alpha.common.rule.head.NormalHead;
import at.ac.tuwien.kr.alpha.common.rule.NormalRule;
import at.ac.tuwien.kr.alpha.common.terms.ConstantTerm;
import at.ac.tuwien.kr.alpha.common.terms.FunctionTerm;
import at.ac.tuwien.kr.alpha.common.terms.IntervalTerm;
import at.ac.tuwien.kr.alpha.common.terms.VariableTerm;
import at.ac.tuwien.kr.alpha.common.terms.Term;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Deque;
import java.util.ArrayDeque;

public class MetaProgramSyntaxCheckTransformation extends ProgramTransformation<InputProgram, InputProgram> {

	private static class FunctionTermLogEntry {
		private final Integer id;
		private final FunctionTerm term;
		protected FunctionTermLogEntry(Integer id, FunctionTerm term) {
			this.id = id;
			this.term = term;
		}
		protected Integer getId() {
			return this.id;
		}
		protected FunctionTerm getTerm() {
			return this.term;
		}
	}

	private class FunctionTermLog {
		private Deque<FunctionTermLogEntry> funcTerms = new ArrayDeque<>();
		private Integer nextId = 0;
		protected Integer append(FunctionTerm term) {
			Integer funcId = this.nextId++;
			this.funcTerms.addLast(new FunctionTermLogEntry(funcId,term));
			return funcId;
		}
		private List<Atom> generateOne (FunctionTermLogEntry p, String[] names) {
			List<Atom> generated = new ArrayList<>();
			generated.add(new BasicAtom(
				Predicate.getInstance(names[0],1),
				ConstantTerm.getInstance(p.getId())
			));
			generated.add(new BasicAtom(
				Predicate.getInstance(names[1],2),
				ConstantTerm.getInstance(p.getId()),
				FunctionTerm.getInstance("symbol",
					ConstantTerm.getSymbolicInstance(p.getTerm().getSymbol()),
					ConstantTerm.getInstance(p.getTerm().getTerms().size())
				)
			));
			Term idTerm = ConstantTerm.getInstance(p.getId());
			generated.addAll(generateTermDescriptions(p.getTerm().getTerms(), idTerm, names));
			return generated;
		}
		protected List<Atom> generateAll(String[] names) {
			names = new String[] {"func", "func_symbol", "func_argument"};
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
		FunctionTermLog funcLog = new FunctionTermLog();

		//TODO: convert into  basic rules mit normal heads?
		System.out.println("================ ================ META FACTS ================ ================");
		Integer metaFactCount = 0;
		List<Atom> srcFacts = new ArrayList<>(inputProgram.getFacts());
		for (Atom srcFact : srcFacts) {
			String[] names = new String[] {"fact", "fact_predicate", "fact_argument"};
			Predicate factPredicate = srcFact.getPredicate();
			metaFacts.add(new BasicAtom(
				Predicate.getInstance(names[0],1),
				ConstantTerm.getInstance(metaFactCount)
			));
			metaFacts.add(new BasicAtom(
				Predicate.getInstance(names[1],2),
				ConstantTerm.getInstance(metaFactCount),
				FunctionTerm.getInstance("pred",
					ConstantTerm.getSymbolicInstance(factPredicate.getName()),
					ConstantTerm.getInstance(factPredicate.getArity())
				)
			));
			Term idTerm = ConstantTerm.getInstance(metaFactCount);
			metaFacts.addAll(generateTermDescriptions(srcFact.getTerms(), idTerm, names));
			metaFactCount++;
		}
		System.out.println("================ ================ ================ ================");

		System.out.println("================ ================ META RULES ================ ================");
		Integer metaRuleCount = 0;
		List<BasicRule> srcRules = new ArrayList<>(inputProgram.getRules());
		for (BasicRule srcRule : srcRules) {
			String[] names = new String[] {"rule", "rule_head_predicate", "rule_head_argument"};
			Head ruleHead = srcRule.getHead();
			Predicate headPredicate = null;
			ConstantTerm headType = null;
			List<Term> headTerms = null;
			if (ruleHead instanceof NormalHead) {
				headPredicate = ((NormalHead)ruleHead).getAtom().getPredicate();
				headTerms = ((NormalHead)ruleHead).getAtom().getTerms();
				headType = ConstantTerm.getSymbolicInstance("normal");
			} else {
				throw new RuntimeException("unhandled head type: "+ruleHead.getClass().getSimpleName());
			}
			metaFacts.add(new BasicAtom(
				Predicate.getInstance(names[0],1),
				ConstantTerm.getInstance(metaRuleCount)
			));
			metaFacts.add(new BasicAtom(
				Predicate.getInstance("rule_head_type",2),
				ConstantTerm.getInstance(metaRuleCount),
				headType
			));
			metaFacts.add(new BasicAtom(
				Predicate.getInstance(names[1],2),
				ConstantTerm.getInstance(metaRuleCount),
				FunctionTerm.getInstance("pred",
					ConstantTerm.getSymbolicInstance(headPredicate.getName()),
					ConstantTerm.getInstance(headPredicate.getArity())
				)
			));
			Integer argumentCount = 0;
			Term idTerm = ConstantTerm.getInstance(metaRuleCount);
			metaFacts.addAll(generateTermDescriptions(headTerms,idTerm,names));
			//
			Set<Literal> ruleLiterals = srcRule.getBody();
			Integer metaLiteralCount = 0;
			for (Literal literal : ruleLiterals) {
				String[] names2 = new String[] {"literal", "literal_predicate", "literal_argument"};
				String literalType = null;
				Predicate literalPredicate = null;
				if (literal instanceof BasicLiteral) {
					literalType = "basic";
					literalPredicate = ((BasicLiteral)literal).getAtom().getPredicate();
				} else {
					throw new RuntimeException("unhandled literal type: "+literal.getClass().getSimpleName());
				}
				metaFacts.add(new BasicAtom(
					Predicate.getInstance("literal_type",2),
					FunctionTerm.getInstance("id",
						ConstantTerm.getInstance(metaRuleCount),
						ConstantTerm.getInstance(metaLiteralCount)
					),
					ConstantTerm.getInstance(literalType)
				));
				metaFacts.add(new BasicAtom(
					Predicate.getInstance(names[1],2),
					FunctionTerm.getInstance("id",
						ConstantTerm.getInstance(metaRuleCount),
						ConstantTerm.getInstance(metaLiteralCount)
					),
					FunctionTerm.getInstance("pred",
						ConstantTerm.getSymbolicInstance(literalPredicate.getName()),
						ConstantTerm.getInstance(literalPredicate.getArity())
					)
				));
				Term idTerm2 = FunctionTerm.getInstance("id",
					ConstantTerm.getInstance(metaRuleCount),
					ConstantTerm.getInstance(metaLiteralCount)
					);
				metaFacts.addAll(generateTermDescriptions(literal.getTerms(), idTerm2, names2));
				metaLiteralCount++;
			}
			metaRuleCount++;
		}
		System.out.println("================ ================ ================ ================");

		InputProgram.Builder programBuilder = InputProgram.builder();
		InputProgram metaProgram = programBuilder
			.addRules(metaRules)
			.addFacts(metaFacts)
			.build()
			;

		System.out.println("================ ================ META PROGRAM ================ ================");
		System.out.println(metaProgram.toString());
		System.out.println("================ ================ ================ ================");

		return null;
	}

	protected List<Atom> generateTermDescriptions(List<Term> terms, Term idTerm, String[] names) {
		List<Atom> generated = new ArrayList<>();
		Integer idx = 0;
		for (Term term : terms) {
			generated.addAll(generateTermDescription(term, idTerm, idx++, names));
			generated.addAll(funcLog.generateAll(names));
		}
		return generated;
	}

	protected List<Atom> generateTermDescription(Term term, Term idTerm, Integer argId, String[] names) {
		List<Atom> generated = new ArrayList<>();
		Term termArg = null;
		if (term instanceof ConstantTerm) {
			termArg = FunctionTerm.getInstance(
				((ConstantTerm<?>)term).isSymbolic()?
					"const":
					((ConstantTerm<?>)term).getObject().getClass().getSimpleName().toLowerCase(),
				ConstantTerm.getInstance(term)
				);
		} else if (term instanceof FunctionTerm) {
			Integer funcId = this.funcLog.append((FunctionTerm)term);
			termArg = FunctionTerm.getInstance(
				"func",
				ConstantTerm.getInstance(funcId)
				);
		} else if (term instanceof VariableTerm) {
			termArg = FunctionTerm.getInstance(
				"var",
				ConstantTerm.getInstance(((VariableTerm)term).toString())
			);
		} else {
			throw new RuntimeException("unhandled term type: "+term.getClass().getSimpleName());
		}
		generated.add(new BasicAtom(
			Predicate.getInstance(names[2],2),
			idTerm,
			FunctionTerm.getInstance("arg",	ConstantTerm.getInstance(argId), termArg)
		));
		return generated;
	}

}
