package com.tictactoe.ui.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class ProxyController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String gameSessionUrl = "http://localhost:8082";
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @PostMapping("/sessions")
    public ResponseEntity<String> createSession() {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                gameSessionUrl + "/sessions",
                null,
                String.class
            );
            return response;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/sessions/{sessionId}/simulate")
    public ResponseEntity<Void> startSimulation(@PathVariable String sessionId) {
        try {
            restTemplate.postForEntity(
                gameSessionUrl + "/sessions/" + sessionId + "/simulate",
                null,
                Void.class
            );
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<String> getSession(@PathVariable String sessionId) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                gameSessionUrl + "/sessions/" + sessionId,
                String.class
            );
            return response;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/sessions/{sessionId}/stream")
    public SseEmitter streamSession(@PathVariable String sessionId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        executor.execute(() -> {
            try {
                URL url = URI.create(gameSessionUrl + "/sessions/" + sessionId + "/stream").toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "text/event-stream");

                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
                );

                String line;
                StringBuilder eventData = new StringBuilder();
                String eventType = null;

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("event:")) {
                        eventType = line.substring(6).trim();
                    } else if (line.startsWith("data:")) {
                        eventData.append(line.substring(5).trim());
                    } else if (line.isEmpty() && eventData.length() > 0) {
                        // Send the event
                        if (eventType != null) {
                            emitter.send(SseEmitter.event()
                                .name(eventType)
                                .data(eventData.toString()));
                        } else {
                            emitter.send(eventData.toString());
                        }
                        eventData.setLength(0);
                        eventType = null;
                    }
                }

                reader.close();
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }
}
