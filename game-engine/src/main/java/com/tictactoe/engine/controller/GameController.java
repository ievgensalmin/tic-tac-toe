package com.tictactoe.engine.controller;

import com.tictactoe.common.dto.GameStateDTO;
import com.tictactoe.common.dto.MoveRequest;
import com.tictactoe.common.dto.MoveResponse;
import com.tictactoe.engine.repository.GameRepository;
import com.tictactoe.engine.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;
    private final GameRepository gameRepository;

    public GameController(GameService gameService, GameRepository gameRepository) {
        this.gameService = gameService;
        this.gameRepository = gameRepository;
    }

    @PostMapping("/{gameId}/move")
    public ResponseEntity<MoveResponse> makeMove(
            @PathVariable String gameId,
            @RequestBody MoveRequest request) {

        if (gameRepository.findById(gameId).isEmpty()) {
            gameService.createGame(gameId);
        }

        MoveResponse response = gameService.makeMove(gameId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameStateDTO> getGameState(@PathVariable String gameId) {
        GameStateDTO state = gameService.getGameState(gameId);
        return ResponseEntity.ok(state);
    }
}
