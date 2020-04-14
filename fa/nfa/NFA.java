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
        DFA retDFA = new DFA();
        HashMap<Character, HashMap<NFAState, HashSet<NFAState>>> possible_states = getAllPossibleStates();
        HashSet<NFAState> start_state = eClosure(getStartState());
        retDFA = getDFABFS(start_state, possible_states, retDFA, true, new HashSet<String>(), new HashSet<String>(),
                "");
        return retDFA;
    }

    /**
     * This is our breadth first approach to getting our DFA. It was written as a
     * helper function initially with the intention of breaking our function down to
     * multiple pieces. Ultimately, we Add a state to the theoretical queue
     * (dequeued with a recursive call to this function), and parse all potential
     * states associated with each character for this state. If the state is already
     * existant in our DFA states, we ignore it! A dead state is added in the case
     * that a transition does not exist in the NFA for the requested state/character
     * relationship. This ensures that our DFA is complete.
     * 
     * @param states
     * @param possible_states
     * @param dfa
     * @param initial
     * @param seen
     * @param states_visited
     * @return DFA
     */
    private DFA getDFABFS(HashSet<NFAState> states,
            HashMap<Character, HashMap<NFAState, HashSet<NFAState>>> possible_states, DFA dfa, boolean initial,
            HashSet<String> seen, HashSet<String> states_visited, String startState) {
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
            startState = name;
        }

        if (states_visited.contains(name) || name.equals("")) {
            return dfa;
        } else {
            seen.add(name);
            states_visited.add(name);
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

            if (name.equals(startState)) {
                if (finalFlag && name.equals(newName)) {
                    dfa.addFinalState(name);
                    dfa.addStartState(name);
                } else {
                    dfa.addStartState(name);
                }
            }

            if (newName.equals("")) {
                String dead = "dead";
                newName = dead;
                if (!seen.contains(dead)) {
                    seen.add(dead);
                    dfa.addState(dead);
                    for (Character c_ : alphabet) {
                        dfa.addTransition(dead, c_, dead);
                    }
                }
                dfa.addTransition(name, c, newName);
                continue;
            }

            if (finalFlag) {
                if (!seen.contains(newName)) {
                    seen.add(newName);
                    dfa.addFinalState(newName);
                }
            } else {
                if (!seen.contains(newName)) {
                    seen.add(newName);
                    dfa.addState(newName);
                }
            }
            dfa.addTransition(name, c, newName);
            dfa = getDFABFS(transitions_new, possible_states, dfa, false, seen, states_visited, startState);
        }
        return dfa;
    }

    /**
     * Constructs and returns an association of Character to State to Set of States.
     * Think of this as our first step to converting our NFA to a DFA. We need a
     * table of state character relationship to use for unioning when building.
     * 
     * @return HashMap<Character, HashMap<NFAState, HashSet<NFAState>>>
     */
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

    /**
     * Gets all potential transitions from a specific state on a given character,
     * taking eclosure values into account.
     * 
     * @param character
     * @param state
     * @param checked
     * @return HashSet<NFAState>
     */
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

    /**
     * This is the recursive helper to eClosure to allow us to take a depth-frist
     * approach to finding the eclosure of the given NFAState.
     * 
     * @param NFAstate s, HashSet<NFAState visited
     * @return HashSet<NFAState>
     */
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

    /**
     * Returns a string representation of the NFA class
     * 
     * @return String
     */
    public String toString() {
        String s = "Q = {";
        for (NFAState st : states) {
            s += st.getName();
            s += " ";
        }
        s += "}\n";
        s += "Sigma = {";

        for (Character c : alphabet) {

            s += c;
            s += " ";
        }

        s += "}" + '\n' + "delta = " + '\n' + '\t' + '\t';
        for (Character c : alphabet) {
            s += c;
            s += '\t';
        }
        s += '\n';
        HashMap<Character, HashMap<NFAState, HashSet<NFAState>>> possStates = getAllPossibleStates();
        for (NFAState st : states) {
            s += '\t' + st.getName();
            for (Character c : alphabet) {
                s += '\t';
                s += possStates.get(c).get(st).toString();
            }
            s += '\n';
        }
        s += "q0 = " + getStartState() + "\n";
        s += "F = {" + getFinalStates() + "}\n";

        return s;
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
