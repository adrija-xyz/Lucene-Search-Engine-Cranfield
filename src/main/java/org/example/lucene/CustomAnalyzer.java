package org.example.lucene;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;

public class CustomAnalyzer extends Analyzer {

    protected TokenStreamComponents createComponents(String s) {

        WhitespaceTokenizer tokenizer = new WhitespaceTokenizer();
        TokenStream tokenstream = tokenizer;
        tokenstream = new LowerCaseFilter(tokenstream);
        tokenstream = new StopFilter(tokenstream, EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
        tokenstream = new LengthFilter(tokenstream, 3, 16);
        tokenstream = new KStemFilter(tokenstream);

        return new TokenStreamComponents(tokenizer, tokenstream);
    }

}
