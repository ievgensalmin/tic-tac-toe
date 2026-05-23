package com.tictactoe.session.service;

import com.tictactoe.common.dto.GameStateDTO;
import com.tictactoe.common.dto.MoveRequest;
import com.tictactoe.common.dto.MoveResponse;
import com.tictactoe.common.enums.GameStatus;
import com.tictactoe.common.enums.Player;
import com.tictactoe.common.enums.SessionStatus;
import com.tictactoe.common.exception.SessionNotFoundException;
import com.tictactoe.session.client.GameEngineClient;
import com.tictactoe.session.entity.Move;
import com.tictactoe.session.entity.Session;
import com.tictactoe.session.repository.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SimulationService {

    private static final Logger log = LoggerFactory.getLogger(SimulationService.class);

    private final SessionRepository sessionRepository;
    private final GameEngineClient gameEngineClient;
    private final MoveGenerationService moveGenerationService;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SimulationService(SessionRepository sessionRepository,
                           GameEngineClient gameEngineClient,
                           MoveGenerationService moveGenerationService) {
        this.sessionRepository = sessionRepository;
        this.gameEngineClient = gameEngineClient;
        this.moveGenerationService = moveGenerationService;
    }

    public void startSimulation(String sessionId) {
        new Thread(() -> runSimulation(sessionId)).start();
    }

    public void runSimulation(String sessionId) {
        // Update session status to IN_PROGRESS
        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new SessionNotFoundException(sessionId));
        session.setStatus(SessionStatus.IN_PROGRESS);
        sessionRepository.save(session);
        log.info("Starting simulation for session: {}", sessionId);

        Player currentPlayer = Player.PLAYER1;
        GameStatus gameStatus = GameStatus.IN_PROGRESS;
        boolean firstMove = true;

        while (gameStatus == GameStatus.IN_PROGRESS) {
            try {
                // Get current game state (or use empty board for first move)
                GameStateDTO gameState;
                if (firstMove) {
                    // For first move, use empty board
                    com.tictactoe.common.enums.Cell[] emptyBoard = new com.tictactoe.common.enums.Cell[9];
                    for (int i = 0; i < 9; i++) {
                        emptyBoard[i] = com.tictactoe.common.enums.Cell.EMPTY;
                    }
                    gameState = new GameStateDTO(sessionId, emptyBoard, Player.PLAYER1, GameStatus.IN_PROGRESS, null);
                    firstMove = false;
                } else {
                    gameState = gameEngineClient.getGameState(sessionId);
                }
                int position = moveGenerationService.generateMove(gameState.board(), currentPlayer);
                log.debug("Generated move: {} at position {}", currentPlayer, position);

                // Make move
                MoveRequest moveRequest = new MoveRequest(currentPlayer, position);
                MoveResponse moveResponse = gameEngineClient.makeMove(sessionId, moveRequest);
                log.debug("Move response status: {}", moveResponse.status());

                // Save move to session (fetch fresh session each time)
                Session currentSession = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new SessionNotFoundException(sessionId));
                Move move = new Move(currentPlayer, position);
                currentSession.addMove(move);
                sessionRepository.save(currentSession);
                log.debug("Saved move to session. Total moves: {}", currentSession.getMoveHistory().size());

                // Emit SSE event
                emitMoveEvent(sessionId, move);

                // Update game status
                gameStatus = moveResponse.status();

                // Switch player
                currentPlayer = currentPlayer.opponent();

                // Small delay for visualization
                Thread.sleep(500);

            } catch (Exception e) {
                log.error("Error in simulation for session {}: {}", sessionId, e.getMessage(), e);
                break;
            }
        }

        // Update session completion (fetch fresh session)
        Session finalSession = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new SessionNotFoundException(sessionId));
        finalSession.setStatus(SessionStatus.COMPLETED);
        finalSession.setCompletedAt(LocalDateTime.now());
        if (gameStatus == GameStatus.PLAYER1_WINS) {
            finalSession.setWinner(com.tictactoe.common.enums.GameResult.PLAYER1_WINS);
        } else if (gameStatus == GameStatus.PLAYER2_WINS) {
            finalSession.setWinner(com.tictactoe.common.enums.GameResult.PLAYER2_WINS);
        } else if (gameStatus == GameStatus.DRAW) {
            finalSession.setWinner(com.tictactoe.common.enums.GameResult.DRAW);
        }
        sessionRepository.save(finalSession);

        // Complete SSE
        completeSseEmitter(sessionId);
    }

    public SseEmitter registerEmitter(String sessionId) {
        SseEmitter emitter = new SseEmitter(300000L); // 5 minutes timeout
        emitters.put(sessionId, emitter);

        emitter.onCompletion(() -> emitters.remove(sessionId));
        emitter.onTimeout(() -> emitters.remove(sessionId));
        emitter.onError((e) -> emitters.remove(sessionId));

        return emitter;
    }

    private void emitMoveEvent(String sessionId, Move move) {
        SseEmitter emitter = emitters.get(sessionId);
        if (emitter != null) {
            try {
                Map<String, Object> data = Map.of(
                    "player", move.getPlayer(),
                    "position", move.getPosition(),
                    "timestamp", move.getTimestamp()
                );
                emitter.send(SseEmitter.event().name("move").data(data));
            } catch (IOException e) {
                emitters.remove(sessionId);
            }
        }
    }

    private void completeSseEmitter(String sessionId) {
        SseEmitter emitter = emitters.get(sessionId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("complete").data("Game completed"));
                emitter.complete();
            } catch (IOException e) {
                // Ignore
            } finally {
                emitters.remove(sessionId);
            }
        }
    }
}
