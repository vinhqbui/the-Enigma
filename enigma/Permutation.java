package enigma;

import java.util.ArrayList;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author vinhbui
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        cycles = cycles.trim();
        _alphabet = alphabet;
        _cycles = new ArrayList<>();
        if (cycles.equals("")) {
            return;
        }
        int count = 0;
        for (int i = 0; i < cycles.length(); i++) {
            char c = cycles.charAt(i);
            if (c == '(') {
                count++;
            } else if (c == ')') {
                count--;
            }
        }
        if (count != 0) {
            throw new EnigmaException("Invalid Perm");
        }
        String [] splitCycles = cycles.split("[\\s+()]");
        int n = splitCycles.length;
        for (int i = 0; i < n; ++i) {
            String cycle = splitCycles[i].replaceAll("[()]", "");
            if (!cycle.equals("")) {
                addCycle(cycle);
            }
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        ArrayList<Integer> permutation = new ArrayList<>();
        for (int i = 0; i < cycle.length(); i++) {
            char input = cycle.charAt(i);
            int alphabetIndex = _alphabet.toInt(input);
            permutation.add(alphabetIndex);
        }
        _cycles.add(permutation);
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int moduloP = wrap(p);
        if (_cycles.size() == 0) {
            return p;
        }
        for (int i = 0; i < _cycles.size(); ++i) {
            ArrayList<Integer> permutation = _cycles.get(i);
            for (int j = 0; j < permutation.size(); j++) {
                if (permutation.get(j).intValue() == moduloP) {
                    if (permutation.size() == 1) {
                        return p;
                    } else if (j < permutation.size() - 1) {
                        return permutation.get(j + 1);
                    } else {
                        return permutation.get(0);
                    }
                }
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int moduloP = wrap(c);
        if (_cycles.size() == 0) {
            return c;
        }
        for (ArrayList<Integer> permutation: _cycles) {
            for (int i = permutation.size() - 1; i >= 0; i--) {
                if (permutation.get(i).intValue() == moduloP) {
                    if (permutation.size() == 1) {
                        return c;
                    } else if (i > 0) {
                        return permutation.get(i - 1);
                    } else {
                        return permutation.get(permutation.size() - 1);
                    }
                }
            }
        }
        return c;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (!_alphabet.contains(p)) {
            return p;
        } else if (!alphabet().contains(p)) {
            throw new EnigmaException("Invalid char.");
        }
        int alphabetIndex = _alphabet.toInt(p);
        int newIndex = permute(alphabetIndex);
        return _alphabet.toChar(newIndex);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        return _alphabet.toChar(_alphabet.toInt(c));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < _cycles.size(); ++i) {
            if (_cycles.get(i).size() == 1) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Hold a list of permutation cycles. */
    private ArrayList<ArrayList<Integer>> _cycles;
}
