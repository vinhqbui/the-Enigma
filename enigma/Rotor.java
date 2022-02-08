package enigma;

import java.util.ArrayList;

/** Superclass that represents a rotor in the enigma machine.
 *  @author vinhbui
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _currentPosition = 0;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.alphabet().size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _currentPosition;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        _currentPosition = posn;
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        if (!_permutation.alphabet().contains(cposn)) {
            throw new EnigmaException("Invalid char to set.");
        }
        _currentPosition = _permutation.alphabet().toInt(cposn);
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        int permute = _permutation.permute(p + _currentPosition);
        int result = (permute - _currentPosition) % size();
        if (result < 0) {
            result += size();
        }
        return result;
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        int invert = _permutation.invert(e + _currentPosition);
        int result = (invert - _currentPosition) % size();
        if (result < 0) {
            result += size();
        }
        return result;
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    /** Current position of rotor. */
    protected int _currentPosition;

    /** This stores the values of the notches. */
    protected ArrayList<Integer> _notchesIndex;
}
