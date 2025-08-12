package com.task.crawling.controller;

import com.task.crawling.service.CrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CrawlingController {

    private final CrawlingService crawlingService;

    @GetMapping("/instagram-feed")
    public ResponseEntity<Void> instagramFeedTask() throws Exception {
         crawlingService.instagramFeedCrawling();

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/instagram-info")
    public ResponseEntity<Void> instagramInfoTask() throws Exception {
        crawlingService.instagramInfoCrawling();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/x-info")
    public ResponseEntity<Void> xInfoTask() throws Exception {
        crawlingService.xInfoCrawling();
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
