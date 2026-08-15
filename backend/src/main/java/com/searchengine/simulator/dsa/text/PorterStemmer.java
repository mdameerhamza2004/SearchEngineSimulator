package com.searchengine.simulator.dsa.text;

/**
 * Implementation of the Porter Stemming Algorithm for English words.
 *
 * <p>The Porter Stemmer reduces words to their root (stem) form by applying
 * a series of rules that strip common suffixes. This is the most widely cited
 * stemming algorithm in Information Retrieval.
 *
 * <p><b>Algorithm Steps:</b>
 * <ol>
 *   <li>Step 1a/1b: Handle plurals and past participles (SSES→SS, IES→I, -ED, -ING)</li>
 *   <li>Step 1c: Turn terminal Y to I when there is a vowel in stem</li>
 *   <li>Step 2: Map double suffixes (ATIONAL→ATE, TIONAL→TION, etc.)</li>
 *   <li>Step 3: Map more suffixes (ICATE→IC, ATIVE→, ALIZE→AL, etc.)</li>
 *   <li>Step 4: Remove suffixes in context of measure > 1 (AL, ANCE, ER, IC, etc.)</li>
 *   <li>Step 5a/5b: Final cleanup (remove trailing E, reduce double L)</li>
 * </ol>
 *
 * <p><b>Complexity:</b> O(n) where n is the length of the word.
 *
 * <p>Reference: Porter, M.F., "An algorithm for suffix stripping",
 * Program, 14(3), pp 130-137, July 1980.
 *
 * @author SearchEngineSimulator
 */
public class PorterStemmer {

    private char[] b;  // word buffer
    private int k;     // offset to the end of the string in b
    private int k0;    // offset to the start (always 0 in our case)
    private int j;     // general offset into the string

    /**
     * Stems a word using the Porter algorithm.
     *
     * @param word the word to stem (must be lowercase)
     * @return the stemmed word, or the original word if too short to stem
     */
    public String stem(String word) {
        if (word == null) return null;
        if (word.length() <= 2) return word;

        b = word.toCharArray();
        k = word.length() - 1;
        k0 = 0;

        // The Porter algorithm consists of 5 steps applied in sequence
        step1ab();
        step1c();
        step2();
        step3();
        step4();
        step5();

        return new String(b, 0, k + 1);
    }

    /**
     * Returns true if b[i] is a consonant.
     * A consonant is any letter other than A, E, I, O, U, and Y when preceded by a consonant.
     */
    private boolean isConsonant(int i) {
        switch (b[i]) {
            case 'a': case 'e': case 'i': case 'o': case 'u':
                return false;
            case 'y':
                return (i == k0) || !isConsonant(i - 1);
            default:
                return true;
        }
    }

    /**
     * Measures the number of consonant sequences between k0 and j.
     * if C is a consonant sequence and V a vowel sequence, then m is:
     *   <C>(VC)^m<V> → m
     * Examples: TR = 0, EE = 0, TREE = 0, Y = 0,
     *           BY = 1, TROUBLE = 1, OATS = 1, TREES = 1, IVY = 1,
     *           TROUBLES = 2, PRIVATE = 2, OATEN = 2
     */
    private int measure() {
        int n = 0;
        int i = k0;
        while (true) {
            if (i > j) return n;
            if (!isConsonant(i)) break;
            i++;
        }
        i++;
        while (true) {
            while (true) {
                if (i > j) return n;
                if (isConsonant(i)) break;
                i++;
            }
            i++;
            n++;
            while (true) {
                if (i > j) return n;
                if (!isConsonant(i)) break;
                i++;
            }
            i++;
        }
    }

    /** Returns true if k0,...,j contains a vowel. */
    private boolean vowelInStem() {
        for (int i = k0; i <= j; i++) {
            if (!isConsonant(i)) return true;
        }
        return false;
    }

    /** Returns true if j, j-1 are the same consonant (a "double consonant"). */
    private boolean doublec(int j) {
        if (j < k0 + 1) return false;
        if (b[j] != b[j - 1]) return false;
        return isConsonant(j);
    }

    /**
     * Returns true if i-2, i-1, i has the form consonant-vowel-consonant
     * and the last consonant is not W, X or Y. This is used when trying to
     * restore an E at the end of a short word.
     */
    private boolean cvc(int i) {
        if (i < k0 + 2 || !isConsonant(i) || isConsonant(i - 1) || !isConsonant(i - 2))
            return false;
        int ch = b[i];
        return ch != 'w' && ch != 'x' && ch != 'y';
    }

    /** Returns true if the word ends with the string s. Sets j appropriately. */
    private boolean ends(String s) {
        int l = s.length();
        int o = k - l + 1;
        if (o < k0) return false;
        for (int i = 0; i < l; i++) {
            if (b[o + i] != s.charAt(i)) return false;
        }
        j = k - l;
        return true;
    }

    /** Sets the suffix to s, adjusting k. Used after a successful ends() call. */
    private void setto(String s) {
        int l = s.length();
        int o = j + 1;
        for (int i = 0; i < l; i++) {
            b[o + i] = s.charAt(i);
        }
        k = j + l;
    }

    /** Calls setto only if the measure of the stem is > 0. */
    private void r(String s) {
        if (measure() > 0) setto(s);
    }

    /**
     * Step 1a and 1b: Deals with plurals, -ed, -ing.
     *
     * SSES -> SS    caresses -> caress
     * IES  -> I     ponies   -> poni, ties -> ti
     * SS   -> SS    caress   -> caress
     * S    ->       cats     -> cat
     *
     * (m>0) EED -> EE    feed -> feed, agreed -> agree
     * (*v*) ED  ->        plastered -> plaster, bled -> bled
     * (*v*) ING ->        motoring -> motor, sing -> sing
     *
     * If the second or third rule in step 1b succeeds, additional fixups:
     *   AT -> ATE    conflat(ed) -> conflate
     *   BL -> BLE    troubl(ed)  -> trouble
     *   IZ -> IZE    siz(ed)     -> size
     *   double consonant -> single (except LL, SS, ZZ)
     *   (m=1 and *o) -> E    fil(ing) -> file
     */
    private void step1ab() {
        // Step 1a
        if (b[k] == 's') {
            if (ends("sses")) {
                k -= 2;
            } else if (ends("ies")) {
                setto("i");
            } else if (b[k - 1] != 's') {
                k--;
            }
        }

        // Step 1b
        if (ends("eed")) {
            if (measure() > 0) k--;
        } else if ((ends("ed") || ends("ing")) && vowelInStem()) {
            k = j;
            if (ends("at")) setto("ate");
            else if (ends("bl")) setto("ble");
            else if (ends("iz")) setto("ize");
            else if (doublec(k)) {
                k--;
                int ch = b[k];
                if (ch == 'l' || ch == 's' || ch == 'z') k++;
            } else if (measure() == 1 && cvc(k)) {
                setto("e");
            }
        }
    }

    /**
     * Step 1c: Turns terminal Y to I when there is a vowel in the stem.
     * happy -> happi, sky -> sky
     */
    private void step1c() {
        if (ends("y") && vowelInStem()) {
            b[k] = 'i';
        }
    }

    /**
     * Step 2: Maps double suffixes to single ones.
     * So -ization (= -ize + -ation) maps to -ize, etc.
     */
    private void step2() {
        if (k < k0 + 1) return;
        switch (b[k - 1]) {
            case 'a':
                if (ends("ational")) { r("ate"); break; }
                if (ends("tional"))  { r("tion"); break; }
                break;
            case 'c':
                if (ends("enci")) { r("ence"); break; }
                if (ends("anci")) { r("ance"); break; }
                break;
            case 'e':
                if (ends("izer")) { r("ize"); break; }
                break;
            case 'l':
                if (ends("bli"))   { r("ble"); break; }
                if (ends("alli"))  { r("al"); break; }
                if (ends("entli")) { r("ent"); break; }
                if (ends("eli"))   { r("e"); break; }
                if (ends("ousli")) { r("ous"); break; }
                break;
            case 'o':
                if (ends("ization")) { r("ize"); break; }
                if (ends("ation"))   { r("ate"); break; }
                if (ends("ator"))    { r("ate"); break; }
                break;
            case 's':
                if (ends("alism"))    { r("al"); break; }
                if (ends("iveness")) { r("ive"); break; }
                if (ends("fulness")) { r("ful"); break; }
                if (ends("ousness")) { r("ous"); break; }
                break;
            case 't':
                if (ends("aliti"))  { r("al"); break; }
                if (ends("iviti"))  { r("ive"); break; }
                if (ends("biliti")) { r("ble"); break; }
                break;
            case 'g':
                if (ends("logi")) { r("log"); break; }
                break;
        }
    }

    /**
     * Step 3: Deals with -ic-, -full, -ness etc.
     */
    private void step3() {
        switch (b[k]) {
            case 'e':
                if (ends("icate")) { r("ic"); break; }
                if (ends("ative")) { r(""); break; }
                if (ends("alize")) { r("al"); break; }
                break;
            case 'i':
                if (ends("iciti")) { r("ic"); break; }
                break;
            case 'l':
                if (ends("ical")) { r("ic"); break; }
                if (ends("ful"))  { r(""); break; }
                break;
            case 's':
                if (ends("ness")) { r(""); break; }
                break;
        }
    }

    /**
     * Step 4: Takes off -ant, -ence etc., in context <c>vcvc<v>.
     * (Measure must be > 1)
     */
    private void step4() {
        if (k < k0 + 1) return;
        switch (b[k - 1]) {
            case 'a':
                if (ends("al")) break; return;
            case 'c':
                if (ends("ance")) break;
                if (ends("ence")) break; return;
            case 'e':
                if (ends("er")) break; return;
            case 'i':
                if (ends("ic")) break; return;
            case 'l':
                if (ends("able")) break;
                if (ends("ible")) break; return;
            case 'n':
                if (ends("ant")) break;
                if (ends("ement")) break;
                if (ends("ment")) break;
                if (ends("ent")) break; return;
            case 'o':
                if (ends("ion") && j >= k0 && (b[j] == 's' || b[j] == 't')) break;
                if (ends("ou")) break; return;
            case 's':
                if (ends("ism")) break; return;
            case 't':
                if (ends("ate")) break;
                if (ends("iti")) break; return;
            case 'u':
                if (ends("ous")) break; return;
            case 'v':
                if (ends("ive")) break; return;
            case 'z':
                if (ends("ize")) break; return;
            default: return;
        }
        if (measure() > 1) k = j;
    }

    /**
     * Step 5: Removes a final -e if measure > 1, and changes -ll to -l
     * if measure > 1.
     */
    private void step5() {
        j = k;
        // Step 5a
        if (b[k] == 'e') {
            int a = measure();
            if (a > 1 || (a == 1 && !cvc(k - 1))) {
                k--;
            }
        }
        // Step 5b
        if (b[k] == 'l' && doublec(k) && measure() > 1) {
            k--;
        }
    }
}
