%%%%%%%%%% SAMPLE INPUT %%%%%%%%%%

passwd_delimiter(":").
%fact(0).
%fact_predicate(0, pred(passwd_delimiter, 1)).
%fact_argument(0, arg(0, string(":"))).

more_complex_fact(42,666,"truth","evil").

very_complex_fact(42,666,"truth","evil",calculate(0)).
%fact(2).
%fact_predicate(2, pred(a_very_complex_fact, 5)).
%fact_argument(2, arg(0, int(42))).
%fact_argument(2, arg(1, int(666))).
%fact_argument(2, arg(2, string("truth"))).
%fact_argument(2, arg(3, string("evil"))).
%fact_argument(2, arg(4, functioncall(0))).
%functioncall(0).
%functioncall_symbol(0,calculate).
%functioncall_argument(0,int(0)).

most_complex_fact(42,666,"truth","evil",foo(bar(21))).

mostest_complex_fact(42,666,"truth","evil",foo(bar(foo2),baz(bar2))).



%infile("/home/michael/advent_of_code/day_2_p1_input.txt").
%fact(1).
%fact_predicate(1, pred(infile, 1)).
%fact_argument(1, arg(0, string("/home/michael/advent_of_code/day_2_p1_input.txt"))).

%input_line(LINE) :- infile(PATH), &file_line[PATH](LINE).
%rule(0).
%rule_head_type(0, normal).
%rule_head_predicate(0, pred(input_line, 1)).
%rule_head_argument(0, arg(0, var("LINE"))).

%rule_body_literal_id(0, 0).
%literal_type(id(0, 0), basic).
%literal_positive(id(0, 0), true).
%literal_predicate(id(0, 0), pred(infile, 1)).
%literal_argument(id(0, 0), arg(0, var("PATH"))).

%rule_body_literal_id(0, 1).
%literal_type(id(0, 1), external).
%literal_positive(id(0, 1), true).
%literal_predicate(id(0, 1), pred(file_line, 2)).
%external_literal_input(id(0, 1), arg(0, var("PATH"))).
%external_literal_output(id(0, 1), arg(0, var("LINE"))).

first_simple_rule :- first_arg.
second_simple_rule :- second_arg(420).
third_simple_rule :- third_arg(first_arg).
first_okay_rule :- third_arg(first_arg, second_arg(420)).
second_okay_rule :- first_arg, second_arg(420), third_arg(first_arg, second_arg(420)).
first_hard_rule(A) :- first_property(A), second_proberty(A).
second_hard_rule(A,C) :- first_property(A,B), second_proberty(B,C).
third_hard_rule(A,B,C) :- first_property(A,first_arg), second_property(B,second_arg(420)), third_property(C, third_arg(first_arg, second_arg(420))).

% Helper Predicates
%predicate(NAME, ARITY) :- 
%    fact_predicate(_, pred(NAME, ARITY)).
%predicate(NAME, ARITY) :-
%    rule_head_predicate(_, pred(NAME, ARITY)).
%predicate(NAME, ARITY) :-
%    literal_predicate(_, pred(NAME, ARITY)).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


%% With facts representing an ASP program as input, determine:
%% - whether there are predicate names that occur with different arities
%% - predicate names that exist in no rule head

%%% Check duplicate predicate names
%warn("Predicate name occurs with different arities!", NAME) :- predicate(NAME, AR1), predicate(NAME, AR2), AR1 != AR2.

%%% Check predicates not existing in rule heads (including facts)
%fact_with_predicate(NAME, ARITY) :- predicate(NAME, ARITY), fact_predicate(_, pred(NAME, ARITY)).
%head_with_predicate(NAME, ARITY) :- predicate(NAME, ARITY), rule_head_predicate(_, pred(NAME, ARITY)).
%warn("Predicate does not occur in facts and isn't derived by any rule!", NAME) :- 
%    predicate(NAME, ARITY), 
%    not fact_with_predicate(NAME, ARITY),
%    not head_with_predicate(NAME, ARITY).
