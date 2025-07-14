package com.tornado.controller;

import com.tornado.mapper.TraceMapper;
import com.tornado.model.SearchRequest;
import com.tornado.response.Response;
import com.tornado.index.IndexSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Autowired
    private IndexSearchService indexSearchService;

    @GetMapping("/")
    private ResponseEntity<Response> searchTrace(@RequestBody SearchRequest searchRequest) throws IOException, ParseException {

        var result = indexSearchService.searchTraces(searchRequest.getQueryString(), searchRequest.getField());

        // Create a map to hold the search results
        HashMap<String, Object> resultData = new HashMap<>();
        resultData.put("traces", TraceMapper.documentsToTraceDTOs(result));
        resultData.put("count", TraceMapper.documentsToTraceDTOs(result).size());

        return ResponseEntity.ok(
                Response.builder()
                        .responseTime(LocalDateTime.now())
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .message("Index search query submitted successfully!")
                        .method("SearchController.java")
                        .executionMessage("Implemented business logic of service class method")
                        .data(resultData)
                        .build()
                );
    }

}
