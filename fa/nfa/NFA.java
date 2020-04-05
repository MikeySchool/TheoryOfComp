package fa.nfa;

import java.util.HashSet;
import java.util.Set;
import java.util.Queue;
import java.util.Stack;

import fa.dfa.*;

public class NFA implements NFAInterface {

    private HashSet<NFAState> states; // States
    private HashSet<Character> alphabet; // Alphabet

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
        if (!alphabet.contains(onSymb)) {
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
    public Set<NFAState> eClosure(NFAState s) {
            TODO:: THIS
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

    public String toString() {
            // TODO: THIS
    }

    /**
     * Just a quick private method that I made to get a state by name.
     * 
     * @param name: String
     * @return NFAState
     */
    public NFAState getState(String name) {
        for (NFAState s : states) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }
}
