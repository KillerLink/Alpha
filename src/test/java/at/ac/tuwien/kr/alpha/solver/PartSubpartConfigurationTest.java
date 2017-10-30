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

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTimeout;

/**
 * Tests {@link AbstractSolver} using some configuration test cases in which subparts are assigned to parts.
 *
 */
public class PartSubpartConfigurationTest extends AbstractSolverTests {
	@Tag("slow")
	@ParameterizedTest
	@ValueSource(ints = { 2, 4, 8, 18, 32, 60, 75, 100 })
	void testPartSubpart(int n) throws IOException {
		assertTimeout(Duration.ofMinutes(1), () -> {
			List<String> rules = new ArrayList<>();
			for (int i = 1; i <= n; i++) {
				rules.add(String.format("n(%d).", i));
			}

			rules.addAll(Arrays.asList(
				"part(N) :- n(N), not no_part(N).",
				"no_part(N) :- n(N), not part(N).",
				"subpartid(SP,ID) :- subpart(SP,P), n(ID), not no_subpartid(SP,ID).",
				"no_subpartid(SP,ID) :- subpart(SP,P), n(ID), not subpartid(SP,ID).",
				"subpart(SP,P) :- part(P), part(SP), P != SP, not no_subpart(SP,P).",
				"no_subpart(SP,P) :- part(P), part(SP), P != SP, not subpart(SP,P).",
				":- subpart(SP,P1), subpart(SP,P2), P1 != P2.",
				":- subpart(SP1,P), subpart(SP2, P), SP1!=SP2, subpartid(SP1,ID), subpartid(SP2,ID)."
			));

			assertFalse(collectSet(concat(rules)).isEmpty());
			// TODO: check correctness of answer set
		});
	}

	private String concat(List<String> rules) {
		String ls = System.lineSeparator();
		return rules.stream().collect(Collectors.joining(ls));
	}
}
