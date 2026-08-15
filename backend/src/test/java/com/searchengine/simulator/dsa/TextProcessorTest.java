package com.searchengine.simulator.dsa;

import com.searchengine.simulator.dsa.text.PorterStemmer;
import com.searchengine.simulator.dsa.text.StopWordFilter;
import com.searchengine.simulator.dsa.text.TextProcessor;
import com.searchengine.simulator.dsa.text.Tokenizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for TextProcessing components.
 */
public class TextProcessorTest {

    private Tokenizer tokenizer;
    private StopWordFilter stopWordFilter;
    private PorterStemmer stemmer;

    @BeforeEach
    void setUp() {
        tokenizer = new Tokenizer();
        stopWordFilter = new StopWordFilter();
        stemmer = new PorterStemmer();
    }

    // Tokenizer Tests
    @Test
    @DisplayName("Tokenizer: Basic Tokenization")
    void testTokenizerBasic() {
        List<String> tokens = tokenizer.tokenize("Hello World");
        assertEquals(Arrays.asList("hello", "world"), tokens);
    }

    @Test
    @DisplayName("Tokenizer: Punctuation Removal")
    void testTokenizerPunctuation() {
        List<String> tokens = tokenizer.tokenize("Hello, world! This is a test.");
        assertEquals(Arrays.asList("hello", "world", "this", "is", "a", "test"), tokens);
    }

    @Test
    @DisplayName("Tokenizer: Empty or Null Input")
    void testTokenizerEmptyNull() {
        assertTrue(tokenizer.tokenize("").isEmpty());
        assertTrue(tokenizer.tokenize(null).isEmpty());
        assertTrue(tokenizer.tokenize("   ").isEmpty());
    }

    @Test
    @DisplayName("Tokenizer: Case Normalization")
    void testTokenizerCase() {
        List<String> tokens = tokenizer.tokenize("MIXED CaSe TeXT");
        assertEquals(Arrays.asList("mixed", "case", "text"), tokens);
    }

    @Test
    @DisplayName("Tokenizer: Special Characters")
    void testTokenizerSpecialChars() {
        List<String> tokens = tokenizer.tokenize("email@domain.com");
        assertEquals(Arrays.asList("email", "domain", "com"), tokens);
    }

    // StopWordFilter Tests
    @Test
    @DisplayName("StopWordFilter: Filters Common Words")
    void testStopWordFilterCommon() {
        List<String> tokens = Arrays.asList("the", "quick", "brown", "fox", "is", "jumping");
        List<String> filtered = stopWordFilter.filter(tokens);
        assertEquals(Arrays.asList("quick", "brown", "fox", "jumping"), filtered);
    }

    @Test
    @DisplayName("StopWordFilter: Preserves Content Words")
    void testStopWordFilterContentWords() {
        List<String> tokens = Arrays.asList("java", "programming", "language");
        List<String> filtered = stopWordFilter.filter(tokens);
        assertEquals(Arrays.asList("java", "programming", "language"), filtered);
    }

    @Test
    @DisplayName("StopWordFilter: Empty Input")
    void testStopWordFilterEmpty() {
        assertTrue(stopWordFilter.filter(Collections.emptyList()).isEmpty());
        assertTrue(stopWordFilter.filter(null).isEmpty());
    }
    
    @Test
    @DisplayName("StopWordFilter: isStopWord Check")
    void testIsStopWord() {
        assertTrue(stopWordFilter.isStopWord("the"));
        assertTrue(stopWordFilter.isStopWord("THE")); // Case-insensitive
        assertFalse(stopWordFilter.isStopWord("computer"));
        assertFalse(stopWordFilter.isStopWord(null));
    }

    // PorterStemmer Tests
    @Test
    @DisplayName("PorterStemmer: Common Suffixes")
    void testStemmerCommonSuffixes() {
        assertEquals("run", stemmer.stem("running"));
        assertEquals("studi", stemmer.stem("studies"));
        assertEquals("caress", stemmer.stem("caresses"));
        assertEquals("cat", stemmer.stem("cats"));
        assertEquals("agre", stemmer.stem("agreed"));
    }

    @Test
    @DisplayName("PorterStemmer: Already Stemmed")
    void testStemmerAlreadyStemmed() {
        assertEquals("run", stemmer.stem("run"));
        assertEquals("cat", stemmer.stem("cat"));
    }

    @Test
    @DisplayName("PorterStemmer: Short Words")
    void testStemmerShortWords() {
        assertEquals("it", stemmer.stem("it"));
        assertEquals("a", stemmer.stem("a"));
        assertEquals("be", stemmer.stem("be"));
    }
    
    @Test
    @DisplayName("PorterStemmer: Null Input")
    void testStemmerNull() {
        assertNull(stemmer.stem(null));
    }

    // TextProcessor Tests
    @Test
    @DisplayName("TextProcessor: Full Pipeline Integration")
    void testProcessorFullPipeline() {
        TextProcessor processor = new TextProcessor();
        List<String> result = processor.process("The quick brown foxes are jumping!");
        assertEquals(Arrays.asList("quick", "brown", "fox", "jump"), result);
    }

    @Test
    @DisplayName("TextProcessor: Disabled Stemming")
    void testProcessorDisabledStemming() {
        TextProcessor processor = new TextProcessor(true, false);
        List<String> result = processor.process("The quick brown foxes are jumping!");
        assertEquals(Arrays.asList("quick", "brown", "foxes", "jumping"), result);
    }

    @Test
    @DisplayName("TextProcessor: Disabled Stop Words")
    void testProcessorDisabledStopWords() {
        TextProcessor processor = new TextProcessor(false, true);
        List<String> result = processor.process("The jumping foxes");
        assertEquals(Arrays.asList("the", "jump", "fox"), result);
    }
    
    @Test
    @DisplayName("TextProcessor: processWithoutStemming")
    void testProcessorWithoutStemmingMethod() {
        TextProcessor processor = new TextProcessor();
        List<String> result = processor.processWithoutStemming("The jumping foxes");
        assertEquals(Arrays.asList("jumping", "foxes"), result);
    }
    
    @Test
    @DisplayName("TextProcessor: tokenizeOnly")
    void testProcessorTokenizeOnlyMethod() {
        TextProcessor processor = new TextProcessor();
        List<String> result = processor.tokenizeOnly("The jumping foxes");
        assertEquals(Arrays.asList("the", "jumping", "foxes"), result);
    }
}
