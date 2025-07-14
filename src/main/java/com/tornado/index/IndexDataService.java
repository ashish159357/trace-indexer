package com.tornado.index;

import com.tornado.service.SingleIndexWriter;
import com.tornado.service.TraceFieldExtractor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import java.util.Map;

@Slf4j
public class IndexDataService {

    private IndexDataService() {}

    public static void indexTraceData(Map<String, Object> fields) {
        try {
            IndexWriter writer = SingleIndexWriter.getIndexWriter();

            Document doc = new Document();

            // Add all extracted fields based on their type
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                String fieldName = entry.getKey();
                Object value = entry.getValue();

                if (TraceFieldExtractor.getSpanFields().containsKey(fieldName))
                {
                    if (value instanceof String v) {
                        // Determine a field type based on name convention or explicit type info
                        if (fieldName.endsWith("Id")) {
                            doc.add(new StringField(fieldName, v, TraceFieldExtractor.getSpanFields().get(fieldName).shouldStore() ? Field.Store.YES : Field.Store.NO));
                        } else {
                            doc.add(new TextField(fieldName, v, TraceFieldExtractor.getSpanFields().get(fieldName).shouldStore() ? Field.Store.YES : Field.Store.NO));
                        }
                    } else if (value instanceof Long v) {
                        doc.add(new LongPoint(fieldName, v));

                        if (TraceFieldExtractor.getSpanFields().get(fieldName).shouldStore()) {
                            doc.add(new StoredField(fieldName, v));
                        }
                    } else if (value instanceof Integer v) {
                        doc.add(new IntPoint(fieldName, v));
                        if (TraceFieldExtractor.getSpanFields().get(fieldName).shouldStore()) {
                            doc.add(new StoredField(fieldName, v));
                        }
                    }
                }else if(TraceFieldExtractor.getResourceFields().containsKey(fieldName)) {
                    if (value instanceof String v) {
                        // Determine a field type based on name convention or explicit type info
                        if (fieldName.endsWith("Id")) {
                            doc.add(new StringField(fieldName, v, TraceFieldExtractor.getResourceFields().get(fieldName).shouldStore() ? Field.Store.YES : Field.Store.NO));
                        } else {
                            doc.add(new TextField(fieldName, v, TraceFieldExtractor.getResourceFields().get(fieldName).shouldStore() ? Field.Store.YES : Field.Store.NO));
                        }
                    } else if (value instanceof Long v) {
                        doc.add(new LongPoint(fieldName, v));

                        if (TraceFieldExtractor.getResourceFields().get(fieldName).shouldStore()) {
                            doc.add(new StoredField(fieldName, v));
                        }
                    } else if (value instanceof Integer v) {
                        doc.add(new IntPoint(fieldName, v));
                        if (TraceFieldExtractor.getResourceFields().get(fieldName).shouldStore()) {
                            doc.add(new StoredField(fieldName, v));
                        }
                    }
                }else {
                    log.warn("don't have any field named {} in FieldExtractor. So, not adding this field for indexing...", fieldName);
                }
            }

            writer.addDocument(doc);
            writer.commit();

        } catch (Exception exception) {
            log.error("unable to index data : {}", exception.getMessage());
        }
    }
}
