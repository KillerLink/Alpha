/**
 * Copyright (c) 2018 Siemens AG
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1) Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package at.ac.tuwien.kr.alpha.solver.heuristics;

import at.ac.tuwien.kr.alpha.common.Assignment;
import at.ac.tuwien.kr.alpha.common.NoGood;
import at.ac.tuwien.kr.alpha.common.atoms.HeuristicAtom;
import at.ac.tuwien.kr.alpha.solver.ChoiceManager;
import at.ac.tuwien.kr.alpha.solver.ThriceTruth;
import at.ac.tuwien.kr.alpha.solver.heuristics.domspec.DomainSpecificHeuristicsStore;
import at.ac.tuwien.kr.alpha.solver.learning.GroundConflictNoGoodLearner.ConflictAnalysisResult;
import org.apache.commons.collections4.SetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static at.ac.tuwien.kr.alpha.solver.ThriceTruth.FALSE;
import static at.ac.tuwien.kr.alpha.solver.ThriceTruth.TRUE;

/**
 * Analyses {@link HeuristicAtom}s to follow domain-specific heuristics specified within the input program.
 * 
 * Each rule can contain a negative {@code _h(W,L)} atom where {@code W} denotes the rule's weight and {@code L} denotes the rule's level.
 * Both values default to 1.
 * When asked for a choice, the domain-specific heuristics will choose to fire from the applicable rules with the highest weight one of those with the highest
 * level.
 */
public class DomainSpecific implements BranchingHeuristic {
	private static final Logger LOGGER = LoggerFactory.getLogger(DomainSpecific.class);
	static final int DEFAULT_CHOICE_ATOM = 0;

	private final Assignment assignment;
	private final ChoiceManager choiceManager;
	private final BranchingHeuristic fallbackHeuristic;

	DomainSpecific(Assignment assignment, ChoiceManager choiceManager, BranchingHeuristic fallbackHeuristic) {
		this.assignment = assignment;
		this.choiceManager = choiceManager;
		this.fallbackHeuristic = fallbackHeuristic;
	}

	@Override
	public void violatedNoGood(NoGood violatedNoGood) {
	}

	@Override
	public void analyzedConflict(ConflictAnalysisResult analysisResult) {
	}

	@Override
	public void newNoGood(NoGood newNoGood) {
	}

	@Override
	public void newNoGoods(Collection<NoGood> newNoGoods) {
	}
	
	@Override
	public int chooseAtom() {
		Optional<Integer> chosenAtom = choiceManager.getDomainSpecificHeuristics().streamRuleAtomsOrderedByDecreasingPriority().map(this::chooseFromEquallyWeighted)
				.filter(a -> a != DEFAULT_CHOICE_ATOM).findFirst();
		return chooseOrFallback(chosenAtom);
	}

	@Override
	public int chooseAtom(Set<Integer> admissibleChoices) {
		Optional<Integer> chosenAtom = choiceManager.getDomainSpecificHeuristics().streamRuleAtomsOrderedByDecreasingPriority()
				.map(s -> SetUtils.intersection(s, admissibleChoices)).map(this::chooseFromEquallyWeighted)
				.filter(a -> a != DEFAULT_CHOICE_ATOM).findFirst();
		return chooseOrFallback(chosenAtom);
	}

	private int chooseFromEquallyWeighted(Set<Integer> possibleChoices) {
		DomainSpecificHeuristicsStore domainSpecificHeuristics = choiceManager.getDomainSpecificHeuristics();
		int chosenAtom;
		Set<Integer> filteredChoices = possibleChoices.stream()
				.filter(choiceManager::isActiveChoiceAtom)
				.filter(this::isUnassigned)
				.filter(a -> domainSpecificHeuristics.isConditionSatisfied(a, assignment))
				.collect(Collectors.toSet());
		if (filteredChoices.isEmpty()) {
			chosenAtom = DEFAULT_CHOICE_ATOM;
		} else if (filteredChoices.size() == 1) {
			chosenAtom = filteredChoices.iterator().next();
			LOGGER.debug("Unique best choice in terms of domain-specific heuristics: " + chosenAtom);
		} else {
			LOGGER.debug("Using fallback heuristics to choose from " + filteredChoices);
			chosenAtom = fallbackHeuristic.chooseAtom(filteredChoices);
		}
		// TODO: also log weight and level? (currently not known at this point)
		return chosenAtom;
	}

	private int chooseOrFallback(Optional<Integer> chosenAtom) {
		if (chosenAtom.isPresent()) {
			return chosenAtom.get();
		} else {
			return fallbackHeuristic.chooseAtom();
		}
	}

	@Override
	public boolean chooseSign(int atom) {
		// TODO: return true (except if falling back to other heuristic)
		return true;
	}
	
	protected boolean isUnassigned(int atom) {
		ThriceTruth truth = assignment.getTruth(atom);
		return truth != FALSE && truth != TRUE; // do not use assignment.isAssigned(atom) because we may also choose MBTs
	}
}