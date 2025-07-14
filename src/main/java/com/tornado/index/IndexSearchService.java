package com.tornado.index;

import com.tornado.service.SingleIndexReader;
import com.tornado.service.SingleIndexWriter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class IndexSearchService {

    private static final String INDEX_DIR = "traces-index";

    public List<Document> searchTraces(String queryString, String field) throws IOException, ParseException {
        IndexSearcher searcher = new IndexSearcher(SingleIndexReader.getIndexReader());
        StandardAnalyzer analyzer = new StandardAnalyzer();

        QueryParser parser = new QueryParser(field, analyzer);
        Query query = parser.parse(queryString);

        TopDocs topDocs = searcher.search(query, 100); // Limit to 100 results
        List<Document> documents = new ArrayList<>();

        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            documents.add(searcher.doc(scoreDoc.doc));
        }

        return documents;
    }

    public List<Document> searchByTimeRange(long minTime, long maxTime) throws IOException {
        FSDirectory directory = FSDirectory.open(Paths.get(INDEX_DIR));
        DirectoryReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        Query query = LongPoint.newRangeQuery("startTime", minTime, maxTime);
        TopDocs topDocs = searcher.search(query, 100);
        List<Document> documents = new ArrayList<>();

        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            documents.add(searcher.doc(scoreDoc.doc));
        }

        reader.close();
        return documents;
    }

    public List<Document> searchByTraceId(String traceId) throws IOException, ParseException {
        return searchTraces(traceId, "traceId");
    }

    public List<Document> searchByServiceName(String serviceName) throws IOException, ParseException {
        return searchTraces(serviceName, "serviceName");
    }

    public List<Document> searchBySpanName(String spanName) throws IOException, ParseException {
        return searchTraces(spanName, "spanName");
    }

    public List<Document> advancedSearch(String traceId, String serviceName, String spanName,
                                         Long minStartTime, Long maxStartTime) throws IOException, ParseException {
        FSDirectory directory = FSDirectory.open(Paths.get(INDEX_DIR));
        DirectoryReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        StandardAnalyzer analyzer = new StandardAnalyzer();

        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();

        if (traceId != null && !traceId.isEmpty()) {
            QueryParser traceParser = new QueryParser("traceId", analyzer);
            booleanQuery.add(traceParser.parse(traceId), BooleanClause.Occur.MUST);
        }

        if (serviceName != null && !serviceName.isEmpty()) {
            QueryParser serviceParser = new QueryParser("serviceName", analyzer);
            booleanQuery.add(serviceParser.parse(serviceName), BooleanClause.Occur.MUST);
        }

        if (spanName != null && !spanName.isEmpty()) {
            QueryParser spanParser = new QueryParser("spanName", analyzer);
            booleanQuery.add(spanParser.parse(spanName), BooleanClause.Occur.MUST);
        }

        if (minStartTime != null && maxStartTime != null) {
            booleanQuery.add(LongPoint.newRangeQuery("startTime", minStartTime, maxStartTime),
                    BooleanClause.Occur.MUST);
        }

        TopDocs topDocs = searcher.search(booleanQuery.build(), 100);
        List<Document> documents = new ArrayList<>();

        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            documents.add(searcher.doc(scoreDoc.doc));
        }

        reader.close();
        return documents;
    }
}