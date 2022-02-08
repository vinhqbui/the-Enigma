package enigma;

import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Set;
import java.util.Iterator;

/** Class that represents a complete enigma machine.
 *  @author Vinh Bui
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        if (numRotors <= 1) {
            throw new EnigmaException("Number of rotors must > 1");
        }
        _alphabet = alpha;
        _numRotors = numRotors;
        _numPawls = pawls;
        _allRotors = new HashMap<>();
        Iterator i = allRotors.iterator();
        while (i.hasNext()) {
            Rotor rotor = (Rotor) i.next();
            _allRotors.put(rotor.name(), rotor);
        }
        _plugBoard = null;
        _workingRotors = new ArrayList<>();
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _numPawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _workingRotors.clear();
        if (rotors.length != _numRotors) {
            throw new EnigmaException("This machine has exactly " + _numRotors);
        }
        Rotor firstRotor = _allRotors.get(rotors[0]);
        if (!firstRotor.reflecting()) {
            throw new EnigmaException("First rotor must be reflector.");
        } else {
            _workingRotors.add(firstRotor);
        }
        int countNumMovingRotors = 0;
        for (int i = 0; i < rotors.length; i++) {
            if (!_allRotors.containsKey(rotors[i])) {
                throw new EnigmaException("This rotors not available.");
            }
            Rotor rotor = _allRotors.get(rotors[i]);
            if (rotor.rotates()) {
                countNumMovingRotors++;
            }
        }
        if (countNumMovingRotors != numPawls()) {
            throw new EnigmaException("Must insert "
                    + numPawls() + " moving rotor(s)");
        }

        for (int i = 1; i < _numRotors; i++) {
            Rotor rotor = _allRotors.get(rotors[i]);
            rotor.set(0);
            _workingRotors.add(rotor);
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (_workingRotors.isEmpty()) {
            throw new EnigmaException("There is no rotors to set.");
        }
        if (setting.length() != _numRotors - 1) {
            throw new EnigmaException("Must be equal to " + (_numRotors - 1));
        }
        for (int i = 0; i < setting.length(); i++) {
            char c = setting.charAt(i);
            _workingRotors.get(i + 1).set(c);
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugBoard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        char temp = _alphabet.toChar(c);
        Set<Integer> index = new TreeSet<>();
        int rightMost = _workingRotors.size() - 1;
        index.add(rightMost);
        for (int i = _workingRotors.size() - 1; i > 0; i--) {
            if (_workingRotors.get(i).atNotch()) {
                index.add(i - 1);
                Rotor prevRotor = _workingRotors.get(i - 1);
                if (prevRotor.rotates()) {
                    index.add(i);
                }

            }
        }
        for (int i : index) {
            _workingRotors.get(i).advance();
        }

        int convertInt = _plugBoard.permute(c);
        for (int i = rightMost; i > 0; i--) {
            convertInt = _workingRotors.get(i).convertForward(convertInt);
        }
        for (int i = 0; i < _workingRotors.size(); i++) {
            convertInt = _workingRotors.get(i).convertBackward(convertInt);
        }
        convertInt = _plugBoard.permute(convertInt);
        return convertInt;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String encryptedMsg = "";
        for (int i = 0; i < msg.length(); i++) {
            char c = msg.charAt(i);
            if (c == '\n' || c == ',' || c == '\t' || c == ' ') {
                encryptedMsg += String.valueOf(c);
            } else if (_alphabet.contains(c)) {
                int index = _alphabet.toInt(c);
                int encryptedIndex = convert(index);
                String e = String.valueOf(_alphabet.toChar(encryptedIndex));
                encryptedMsg += e;
            } else {
                throw new EnigmaException("Invalid char.");
            }
        }
        return encryptedMsg;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** The number rotors inside the machine. */
    private final int _numRotors;

    /** Number of pawls, or number of moving rotors. */
    private final int _numPawls;

    /** Stores all rotors of the machine. */
    private HashMap<String, Rotor> _allRotors;

    /** Stores the rotors currently in the machine. */
    private ArrayList<Rotor> _workingRotors;

    /** Plug board of the machine. */
    private Permutation _plugBoard;
}
