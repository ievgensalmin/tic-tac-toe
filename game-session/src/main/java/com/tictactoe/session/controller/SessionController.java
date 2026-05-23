package com.tictactoe.session.controller;

import com.tictactoe.common.dto.SessionDTO;
import com.tictactoe.session.service.SessionService;
import com.tictactoe.session.service.SimulationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;
    private final SimulationService simulationService;

    public SessionController(SessionService sessionService, SimulationService simulationService) {
        this.sessionService = sessionService;
        this.simulationService = simulationService;
    }

    @PostMapping
    public ResponseEntity<SessionDTO> createSession() {
        SessionDTO session = sessionService.createSession();
        return ResponseEntity.status(HttpStatus.CREATED).body(session);
    }

    @PostMapping("/{sessionId}/simulate")
    public ResponseEntity<Void> startSimulation(@PathVariable String sessionId) {
        simulationService.startSimulation(sessionId);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionDTO> getSession(@PathVariable String sessionId) {
        SessionDTO session = sessionService.getSession(sessionId);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/{sessionId}/stream")
    public SseEmitter streamMoves(@PathVariable String sessionId) {
        return simulationService.registerEmitter(sessionId);
    }
}
