package fa.nfa;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Queue;
import java.util.Stack;

import fa.dfa.*;

public class NFA implements NFAInterface {

    private HashSet<NFAState> states; // States
    private HashSet<Character> alphabet; // Alphabet
    private final char EMPTY_CHAR = 'e';

    /**
     * Constructor, just instantiates our two instance variables.
     */
    public NFA() {
        states = new HashSet<NFAState>();
        alphabet = new HashSet<Character>();
    }

    /**
     * Note that this will either add a new NFA state or change the state of an
     * existing NFA to initial.
     * 
     * @param name: String
     */
    public void addStartState(String name) {
        if (getState(name) == null) {
            states.add(new NFAState(name, false, true));
        } else {
            getState(name).setInitial(true);
        }
    }

    /**
     * Note that this will either add a new NFA state or break out of the method.
     * 
     * @param name: String
     */
    public void addState(String name) {
        if (getState(name) != null)
            return;
        states.add(new NFAState(name, false, false));
    }

    /**
     * Note that this will either add a new NFA state or change the state of an
     * existing NFA to final.
     * 
     * @param name: String
     */
    public void addFinalState(String name) {
        if (getState(name) == null) {
            states.add(new NFAState(name, true, false));
        } else {
            getState(name).setFinal(true);
        }
    }

    /**
     * Adds the transition to the NFA's delta data structure
     * 
     * @param fromState is the label of the state where the transition starts
     * @param onSymb    is the symbol from the NFA's alphabet.
     * @param toState   is the label of the state where the transition ends
     */
    public void addTransition(String fromState, char onSymb, String toState) {
        if (!alphabet.contains(onSymb) && onSymb != 'e') {
            alphabet.add(onSymb);
        }
        getState(fromState).addTransition(onSymb, getState(toState));
    }

    /**
     * Getter for Q
     * 
     * @return a set of states that FA has
     */
    public Set<NFAState> getStates() {
        return states;
    }

    /**
     * Getter for F
     * 
     * @return a set of final states that FA has
     */
    public Set<NFAState> getFinalStates() {
        HashSet<NFAState> finalStates = new HashSet<NFAState>();
        for (NFAState s : states) {
            if (s.isFinal()) {
                finalStates.add(s);
            }
        }
        return finalStates;
    }

    /**
     * Getter for q0
     * 
     * @return the start state of FA
     */
    public NFAState getStartState() {
        for (NFAState s : states) {
            if (s.isInitial()) {
                return s;
            }
        }
        return null;
    }

    /**
     * Getter for the alphabet Sigma
     * 
     * @return the alphabet of FA
     */
    public Set<Character> getABC() {
        return alphabet;
    }

    /**
     * 
     * @return equivalent DFA
     */
    public DFA getDFA() {
        // TODO: THIS
        DFA retDFA = new DFA();
        HashMap<Character, HashMap<NFAState, HashSet<NFAState>>> possible_states = getAllPossibleStates();
        HashSet<NFAState> start_state = eClosure(getStartState());
        for (Character c : alphabet) {
            for (NFAState s : states) {
                System.out.println("State: " + s + ", Character: " + c + " : ");
                for (NFAState st : possible_states.get(c).get(s)) {
                    System.out.print(st);
                }
                System.out.println();
            }
        }
        retDFA = getDFAInitialState(start_state, possible_states, retDFA, true, new HashSet<String>(),
                new HashSet<String>());
        return retDFA;
    }

    private DFA getDFAInitialState(HashSet<NFAState> states,
            HashMap<Character, HashMap<NFAState, HashSet<NFAState>>> possible_states, DFA dfa, boolean initial,
            HashSet<String> seen, HashSet<String> states_visited) {
        String name = "";
        HashMap<Character, HashSet<NFAState>> transitions = new HashMap<Character, HashSet<NFAState>>();

        for (NFAState s : states) {
            name += s;
            for (Character c : alphabet) {
                if (transitions.get(c) == null) {
                    transitions.put(c, new HashSet<NFAState>());
                }
                transitions.get(c).addAll(possible_states.get(c).get(s));
            }
        }

        if (initial) {
            dfa.addStartState(name);
        }

        if (!states_visited.contains(name) && !name.equals("")) {
            seen.add(name);
            states_visited.add(name);
        } else {
            return dfa;
        }

        for (Character c : transitions.keySet()) {
            String newName = "";
            boolean finalFlag = false;
            HashSet<NFAState> transitions_new = transitions.get(c);

            for (NFAState ss : transitions_new) {
                newName += ss;
                if (ss.isFinal()) {
                    finalFlag = true;
                }
            }

            if (newName.equals("")) {
                String dead = "dead";
                if (!seen.contains(dead)) {
                    seen.add(dead);
                    dfa.addState(dead);
                    for (Character c_ : alphabet) {
                        dfa.addTransition(dead, c_, dead);
                    }
                }
                dfa.addTransition(name, c, "dead");

                continue;
            }
            if (finalFlag) {
                if (!seen.contains(newName)) {
                    seen.add(newName);
                    dfa.addFinalState(newName);
                }
                dfa.addTransition(name, c, newName);
            } else {
                if (!seen.contains(newName)) {
                    seen.add(newName);
                    dfa.addState(newName);
                }
                dfa.addTransition(name, c, newName);
            }

            dfa = getDFAInitialState(transitions_new, possible_states, dfa, false, seen, states_visited);
        }
        return dfa;
    }

    public DFAState getDFAState(NFAState state, HashSet<NFAState> transitions) {
        DFAState newState = new DFAState(""); // IS FINAL?

        return newState;
    }

    public DFAState getDFAFinalState(NFAState s, HashSet<NFAState> transitions) {
        DFAState newState = new DFAState(""); // IS FINAL?

        return newState;
    }

    private HashMap<Character, HashMap<NFAState, HashSet<NFAState>>> getAllPossibleStates() {
        HashMap<Character, HashMap<NFAState, HashSet<NFAState>>> possible_states = new HashMap<Character, HashMap<NFAState, HashSet<NFAState>>>();
        for (NFAState state : states) {
            for (Character char_ : alphabet) {
                HashSet<NFAState> possible_init = getStatesForChar(char_, state, new HashSet<NFAState>());
                if (possible_states.get(char_) == null) {
                    possible_states.put(char_, new HashMap<NFAState, HashSet<NFAState>>());
                }
                possible_states.get(char_).put(state, possible_init);
            }
        }
        return possible_states;
    }

    private HashSet<NFAState> getStatesForChar(Character character, NFAState state, HashSet<NFAState> checked) {
        HashSet<NFAState> from_state = new HashSet<NFAState>();
        HashSet<NFAState> e_closed = eClosure(state);
        for (NFAState s : e_closed) {
            if (!checked.contains(s)) {
                HashSet<NFAState> transits_char = s.transition(character);
                if (transits_char != null) {
                    for (NFAState state_char : transits_char) {
                        if (!from_state.contains(state_char)) {
                            from_state.addAll(eClosure(state_char));
                        }
                    }
                    checked.add(s);
                }
            }
        }

        return from_state;
    }

    /**
     * Return delta entries
     * 
     * @param from   - the source state
     * @param onSymb - the label of the transition
     * @return a set of sink states
     */
    public HashSet<NFAState> getToState(NFAState from, char onSymb) {
        return from.transition(onSymb);
    }

    /**
     * Traverses all epsilon transitions and determine what states can be reached
     * from s through e
     * 
     * @param s
     * @return set of states that can be reached from s on epsilon trans.
     */
    public HashSet<NFAState> eClosure(NFAState s) {
        HashSet<NFAState> visited = new HashSet<NFAState>();
        HashSet<NFAState> eClosed = eClosureRecurse(s, visited);
        return eClosed;
    }

    private HashSet<NFAState> eClosureRecurse(NFAState s, HashSet<NFAState> visited) {
        HashSet<NFAState> eClosed = new HashSet<NFAState>();
        visited.add(s);
        eClosed.add(s);
        HashSet<NFAState> toVisit = s.transition('e') == null ? new HashSet<NFAState>() : s.transition('e');
        for (NFAState state : toVisit) {
            if (!visited.contains(state)) {
                eClosed.addAll(eClosureRecurse(state, visited));
            }
        }
        return eClosed;
    }

    public String toString() {
        // TODO: THIS
        return "";
    }

    /**
     * This checks if a string is the empty string. It does so with very simple
     * ascii comparison.
     * 
     * 
     * @param c: char
     * @return boolean
     */
    private boolean isEmptyString(char c) {
        return c == 'e';
    }

    /**
     * Just a quick private method that I made to get a state by name.
     * 
     * @param name: String
     * @return NFAState
     */
    private NFAState getState(String name) {
        for (NFAState s : states) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }
}
