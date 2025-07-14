package com.tornado.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

@Slf4j
public class SingleIndexWriter {

    private static final String INDEX_DIR = "traces-index";

    private static IndexWriter writer;
    private static StandardAnalyzer analyzer;
    private static IndexWriterConfig config;
    private static FSDirectory indexDirectory;

    private SingleIndexWriter(){}

    public static IndexWriter getIndexWriter() {
        if (writer == null) {
            synchronized (SingleIndexWriter.class){
                try {
                    indexDirectory = FSDirectory.open(Paths.get(INDEX_DIR));
                    analyzer = new StandardAnalyzer();
                    config = new IndexWriterConfig(analyzer);
                    writer = new IndexWriter(indexDirectory, config);
                } catch (Exception e) {
                    log.error("unable to create instance of writer : {}", e.getMessage());
                }
            }
        }
        return writer;
    }

    public static synchronized void closeWriter() throws IOException {
        if (writer != null) {
            writer.close();
            writer = null;
        }

        if (config != null){
            config = null;
        }

        if (analyzer != null){
            analyzer.close();
            analyzer = null;
        }

        if (indexDirectory != null){
            indexDirectory.close();
            indexDirectory = null;
        }
    }
}
