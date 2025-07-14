package com.tornado.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;

import java.io.IOException;

@Slf4j
public class SingleIndexReader {

    private static DirectoryReader reader;

    private SingleIndexReader() {}

    public static DirectoryReader getIndexReader(){
        try{
            return DirectoryReader.open(SingleIndexWriter.getIndexWriter());
        }catch (Exception e){
            log.error("Unable to create reader : {}", e.getMessage());
        }

        return reader;
    }

    public static synchronized void closeWriter() throws IOException {
        reader.close();
        reader = null;
    }
}
