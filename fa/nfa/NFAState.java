package fa.nfa;

import java.util.HashMap;
import java.util.HashSet;

import fa.State;

/**
 * This class is what defines a state for our NFA.
 * 
 * NFA States all contain some similar data
 * 
 * - All states have transitions for each letter of the alphabet. - All states
 * have a name which is it's unique id - All states are any of the following:
 * starting, final, both, neither
 * 
 * @author Mikey Krentz
 */
public class NFAState extends State {

    private HashMap<Character, HashSet<NFAState>> transitions;
    private boolean isFinal;
    private boolean isInitial;
    private String name;

    /**
     * Constructor that instantiates instace variables
     * 
     * @param name:         String
     * @param finalState:   boolean
     * @param initialState: boolean
     */
    public NFAState(String name, boolean finalState, boolean initialState) {
        isFinal = finalState;
        isInitial = initialState;
        this.name = name;
        transitions = new HashMap<Character, HashSet<NFAState>>();
    }

    /**
     * Returns the name (id) of the state
     * 
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the final status of the state
     * 
     * @return boolean
     */
    public boolean isFinal() {
        return isFinal;
    }

    /**
     * This sets the final status of a state
     * 
     * @param isFinal: boolean
     */
    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    /**
     * Returns the initial status of the state
     * 
     * @return boolean
     */
    public boolean isInitial() {
        return isInitial;
    }

    /**
     * This sets the initial status of a state
     * 
     * @param isInitial: boolean
     */
    public void setInitial(boolean isInitial) {
        this.isInitial = isInitial;
    }

    /**
     * Adds a transition to the state
     * 
     * @param c:     char
     * @param state: DFAState
     */
    public void addTransition(char c, NFAState state) {
        if (transitions.get(c) == null) {
            HashSet<NFAState> toAdd = new HashSet<NFAState>();
            toAdd.add(state);
            transitions.put(c, toAdd);
            return;
        }
        transitions.get(c).add(state);
    }

    /**
     * Removes a transition from the state
     * 
     * @param c: char
     */
    public void removeTransition(char c) {
        transitions.remove(c);
    }

    /**
     * Returns all transitions of a state.
     * 
     * @return HashMap<Character, DFAState>
     */
    public HashMap<Character, HashSet<NFAState>> getTransitions() {
        return transitions;
    }

    /**
     * Retreives a list of states from a given character (performs transition)
     * 
     * @return c: char
     */
    public HashSet<NFAState> transition(char c) {
        return transitions.get(c);
    }

    /**
     * String representation of a state..
     * 
     * @return String
     */
    public String toString() {
        return name;
    }

    /**
     * Checks equality by unique identifier
     * 
     * @param s
     * @return isEqual?
     */
    public boolean equals(NFAState s) {
        return s.name.equals(this.name);
    }
}