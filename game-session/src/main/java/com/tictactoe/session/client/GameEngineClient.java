package com.tictactoe.session.client;

import com.tictactoe.common.dto.GameStateDTO;
import com.tictactoe.common.dto.MoveRequest;
import com.tictactoe.common.dto.MoveResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "game-engine")
public interface GameEngineClient {

    @PostMapping("/games/{gameId}/move")
    MoveResponse makeMove(@PathVariable("gameId") String gameId, @RequestBody MoveRequest request);

    @GetMapping("/games/{gameId}")
    GameStateDTO getGameState(@PathVariable("gameId") String gameId);
}
