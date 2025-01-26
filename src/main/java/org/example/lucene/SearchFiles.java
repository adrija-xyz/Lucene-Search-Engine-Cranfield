package org.example.lucene;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

public class SearchFiles {
    public static void main(String[] args) throws Exception {

        String index = "index";
        String results_path = "my-results.txt";
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
        PrintWriter writer = new PrintWriter(results_path, "UTF-8");
        IndexSearcher searcher = new IndexSearcher(reader);

        //Choose one analyzer and one similarity

        //searcher.setSimilarity(new ClassicSimilarity());
        searcher.setSimilarity(new BM25Similarity());
        //Analyzer analyzer = new EnglishAnalyzer();
        Analyzer analyzer = new CustomAnalyzer();


        String queriesPath = "./cranfield/cran.qry";
        BufferedReader buffer = Files.newBufferedReader(Paths.get(queriesPath), StandardCharsets.UTF_8);
        MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[] {"title","author","bibliography","content"}, analyzer);

        String queryString = "";
        Integer queryNumber = 1;
        String line;
        Boolean first = true;

        System.out.println("Reading in queries and creating search results.");

        while ((line = buffer.readLine()) != null){

            if(line.substring(0,2).equals(".I")){
                if(!first){
                    Query query = parser.parse(QueryParser.escape(queryString));
                    performSearch(searcher,writer,queryNumber,query);
                    queryNumber++;
                }
                else{ first=false; }
                queryString = "";
            } else {
                queryString += " " + line;
            }
        }

        Query query = parser.parse(QueryParser.escape(queryString));
        performSearch(searcher,writer,queryNumber,query);

        writer.close();
        reader.close();
    }
    public static void performSearch(IndexSearcher searcher, PrintWriter writer, Integer queryNumber, Query query) throws IOException {
        TopDocs results = searcher.search(query, 1400);
        ScoreDoc[] hits = results.scoreDocs;
        for(int i=0;i<hits.length;i++){
            Document doc = searcher.doc(hits[i].doc);
            writer.println(queryNumber + " 0 " + doc.get("id") + " " + i + " " + hits[i].score + " EXP");
        }
    }

}