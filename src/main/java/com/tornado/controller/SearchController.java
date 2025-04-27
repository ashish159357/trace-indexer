package com.tornado.controller;

import com.tornado.model.SearchRequest;
import com.tornado.response.Response;
import com.tornado.service.IndexSearchService;
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

@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Autowired
    private IndexSearchService indexSearchService;

    @GetMapping("/")
    private ResponseEntity<Response> searchTrace(@RequestBody SearchRequest searchRequest) throws IOException, ParseException {

        var result = indexSearchService.searchTraces(searchRequest.getQueryString(), searchRequest.getField());

        return ResponseEntity.ok(
                Response.builder()
                        .responseTime(LocalDateTime.now())
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .message("Index search query submitted successfully!")
                        .method("SearchController.java")
                        .executionMessage("Implemented business logic of service class method").build()
                );
    }

}
