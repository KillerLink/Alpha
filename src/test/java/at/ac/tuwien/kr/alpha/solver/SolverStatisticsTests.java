/**
 * Copyright (c) 2017 Siemens AG
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1) Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2) Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
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
package at.ac.tuwien.kr.alpha.solver;

import at.ac.tuwien.kr.alpha.common.AnswerSet;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class SolverStatisticsTests extends AbstractSolverTests {

	@Test
	public void checkStatsStringZeroChoices() {
		Solver solver = getInstance("a.");
		collectAnswerSetsAndCheckStats(solver, 1, 0, 0, 0, 0, 0);
	}

	@Test
	public void checkStatsStringOneChoice() {
		Solver solver = getInstance("a :- not b. b :- not a.");
		collectAnswerSetsAndCheckStats(solver, 2, 1, 1, 1, 1, 0);
	}

	private void collectAnswerSetsAndCheckStats(Solver solver, int expectedNumberOfAnswerSets, int expectedNumberOfGuesses, int expectedTotalNumberOfBacktracks,
			int expectedNumberOfBacktracksWithinBackjumps, int expectedNumberOfBackjumps, int expectedNumberOfMBTs) {
		Set<AnswerSet> answerSets = solver.collectSet();
		assertEquals(expectedNumberOfAnswerSets, answerSets.size());
		if (solver instanceof SolverMaintainingStatistics) {
			SolverMaintainingStatistics solverMaintainingStatistics = (SolverMaintainingStatistics) solver;
			assertEquals(
					String.format("g=%d, bt=%d, bj=%d, bt_within_bj=%d, mbt=%d", expectedNumberOfGuesses, expectedTotalNumberOfBacktracks, expectedNumberOfBackjumps,
							expectedNumberOfBacktracksWithinBackjumps, expectedNumberOfMBTs),
					solverMaintainingStatistics.getStatisticsString());
		}
	}

}
