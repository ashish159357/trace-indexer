package com.tornado.mapper;

import com.tornado.dto.TraceDTO;
import org.apache.lucene.document.Document;

import java.util.ArrayList;
import java.util.List;

public class TraceMapper {

    // And update the documentToTraceDTO method
    public static List<TraceDTO> documentsToTraceDTOs(List<Document> documents) {
        List<TraceDTO> traceDTOs = new ArrayList<>();

        for (var document : documents) {
            TraceDTO traceDto = TraceDTO.builder()
                            .traceId(document.get("traceId"))
                            .spanId(document.get("spanId"))
                            .serviceName(document.get("serviceName"))
                            .spanName(document.get("name"))
                            .attributes(document.get("spanAttributes"))
                            .resourceAttributes(document.get("resourceAttributes"))
                            .startTime(Long.parseLong(document.get("startTime")))
                            .endTime(Long.parseLong(document.get("endTime")))
                            .build();

            traceDTOs.add(traceDto);
        }

        return traceDTOs;
    }
}
