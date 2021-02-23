package at.ac.tuwien.kr.alpha.api;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import at.ac.tuwien.kr.alpha.api.common.fixedinterpretations.PredicateInterpretation;
import at.ac.tuwien.kr.alpha.api.config.InputConfig;
import at.ac.tuwien.kr.alpha.api.config.SystemConfig;
import at.ac.tuwien.kr.alpha.api.program.ASPCore2Program;
import at.ac.tuwien.kr.alpha.api.program.CompiledProgram;
import at.ac.tuwien.kr.alpha.api.program.Predicate;
import at.ac.tuwien.kr.alpha.api.program.Program;
import at.ac.tuwien.kr.alpha.api.rules.NormalHead;
import at.ac.tuwien.kr.alpha.api.rules.Rule;

public interface Alpha {

	ASPCore2Program readProgram(InputConfig cfg) throws IOException;

	ASPCore2Program readProgramFiles(boolean literate, Map<String, PredicateInterpretation> externals, List<String> paths) throws IOException;

	ASPCore2Program readProgramFiles(boolean literate, Map<String, PredicateInterpretation> externals, Path... paths) throws IOException;

	ASPCore2Program readProgramString(String aspString, Map<String, PredicateInterpretation> externals);

	ASPCore2Program readProgramString(String aspString);

	Stream<AnswerSet> solve(ASPCore2Program program);

	Stream<AnswerSet> solve(ASPCore2Program program, java.util.function.Predicate<Predicate> filter);

	Program<Rule<NormalHead>> normalizeProgram(ASPCore2Program program);
	
	SystemConfig getConfig();
	
	CompiledProgram performProgramPreprocessing(CompiledProgram prog);
	
	Solver prepareSolverFor(CompiledProgram program, java.util.function.Predicate<Predicate> filter);
	
}