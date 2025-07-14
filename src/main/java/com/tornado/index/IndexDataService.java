package com.tornado.service;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class IndexDataService {

    private static final String INDEX_DIR = "traces-index";

    public static void indexTrace(String traceId, String spanId, String serviceName, String spanName, long startTime, long endTime, String attributes) {
        try {
            FSDirectory indexDirectory = FSDirectory.open(Paths.get(INDEX_DIR));
            StandardAnalyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter writer = new IndexWriter(indexDirectory, config);

            Document doc = new Document();
            doc.add(new StringField("traceId", traceId, Field.Store.YES));
            doc.add(new StringField("spanId", spanId, Field.Store.YES));
            doc.add(new TextField("serviceName", serviceName, Field.Store.YES));
            doc.add(new TextField("spanName", spanName, Field.Store.YES));
            doc.add(new LongPoint("startTime", startTime));
            doc.add(new LongPoint("endTime", endTime));
            doc.add(new StoredField("attributes", attributes));

            writer.addDocument(doc);
            writer.close();

            System.out.println("✅ Indexed Trace: " + traceId + " | Span: " + spanName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
