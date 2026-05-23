package com.tictactoe.engine.service;

import com.tictactoe.common.dto.GameStateDTO;
import com.tictactoe.common.dto.MoveRequest;
import com.tictactoe.common.dto.MoveResponse;
import com.tictactoe.common.enums.Cell;
import com.tictactoe.common.enums.GameResult;
import com.tictactoe.common.enums.GameStatus;
import com.tictactoe.common.exception.GameNotFoundException;
import com.tictactoe.common.exception.InvalidMoveException;
import com.tictactoe.engine.entity.Game;
import com.tictactoe.engine.repository.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final WinDetectionService winDetectionService;

    public GameService(GameRepository gameRepository, WinDetectionService winDetectionService) {
        this.gameRepository = gameRepository;
        this.winDetectionService = winDetectionService;
    }

    @Transactional
    public Game createGame(String gameId) {
        Game game = new Game(gameId);
        return gameRepository.save(game);
    }

    @Transactional
    public MoveResponse makeMove(String gameId, MoveRequest request) {
        Game game = gameRepository.findById(gameId)
            .orElseThrow(() -> new GameNotFoundException(gameId));

        validateMove(game, request);

        Cell[] board = game.getBoard();
        board[request.position()] = request.player().toCell();
        game.setBoard(board);

        updateGameStatus(game);

        game.setCurrentPlayer(game.getCurrentPlayer().opponent());
        game.setUpdatedAt(LocalDateTime.now());

        Game savedGame = gameRepository.save(game);

        GameResult winner = null;
        if (savedGame.getStatus() == GameStatus.PLAYER1_WINS) {
            winner = GameResult.PLAYER1_WINS;
        } else if (savedGame.getStatus() == GameStatus.PLAYER2_WINS) {
            winner = GameResult.PLAYER2_WINS;
        } else if (savedGame.getStatus() == GameStatus.DRAW) {
            winner = GameResult.DRAW;
        }

        return new MoveResponse(savedGame.getBoard(), savedGame.getStatus(), winner);
    }

    public GameStateDTO getGameState(String gameId) {
        Game game = gameRepository.findById(gameId)
            .orElseThrow(() -> new GameNotFoundException(gameId));

        GameResult winner = null;
        if (game.getStatus() == GameStatus.PLAYER1_WINS) {
            winner = GameResult.PLAYER1_WINS;
        } else if (game.getStatus() == GameStatus.PLAYER2_WINS) {
            winner = GameResult.PLAYER2_WINS;
        } else if (game.getStatus() == GameStatus.DRAW) {
            winner = GameResult.DRAW;
        }

        return new GameStateDTO(
            game.getGameId(),
            game.getBoard(),
            game.getCurrentPlayer(),
            game.getStatus(),
            winner
        );
    }

    private void validateMove(Game game, MoveRequest request) {
        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            throw new InvalidMoveException("Game is already finished");
        }

        if (request.player() != game.getCurrentPlayer()) {
            throw new InvalidMoveException("Not this player's turn");
        }

        if (game.getBoard()[request.position()] != Cell.EMPTY) {
            throw new InvalidMoveException("Cell already occupied");
        }
    }

    private void updateGameStatus(Game game) {
        Cell[] board = game.getBoard();

        if (winDetectionService.checkWin(board, Cell.X)) {
            game.setStatus(GameStatus.PLAYER1_WINS);
        } else if (winDetectionService.checkWin(board, Cell.O)) {
            game.setStatus(GameStatus.PLAYER2_WINS);
        } else if (winDetectionService.isDraw(board)) {
            game.setStatus(GameStatus.DRAW);
        }
    }
}
