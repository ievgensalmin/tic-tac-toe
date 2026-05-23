import React, { useState, useEffect, useRef } from 'react';
import Board from './components/Board';
import GameControls from './components/GameControls';
import MoveHistory from './components/MoveHistory';
import './App.css';

function App() {
  const [board, setBoard] = useState(Array(9).fill('EMPTY'));
  const [gameStatus, setGameStatus] = useState(null);
  const [moveHistory, setMoveHistory] = useState([]);
  const [sessionId, setSessionId] = useState(null);
  const [isSimulating, setIsSimulating] = useState(false);
  const [error, setError] = useState(null);
  const eventSourceRef = useRef(null);

  const startNewGame = async () => {
    try {
      setError(null);
      setIsSimulating(true);
      setBoard(Array(9).fill('EMPTY'));
      setGameStatus('IN_PROGRESS');
      setMoveHistory([]);

      // Create new session
      const sessionResponse = await fetch('/sessions', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' }
      });

      if (!sessionResponse.ok) {
        throw new Error('Failed to create session');
      }

      const sessionData = await sessionResponse.json();
      const newSessionId = sessionData.sessionId;
      setSessionId(newSessionId);

      // Start SSE connection
      const eventSource = new EventSource(`/sessions/${newSessionId}/stream`);
      eventSourceRef.current = eventSource;

      eventSource.onmessage = (event) => {
        console.log('SSE message:', event);
      };

      eventSource.addEventListener('move', (event) => {
        const moveData = JSON.parse(event.data);
        console.log('Move received:', moveData);

        setMoveHistory(prev => [...prev, moveData]);

        // Update board
        setBoard(prevBoard => {
          const newBoard = [...prevBoard];
          const symbol = moveData.player === 'PLAYER1' ? 'X' : 'O';
          newBoard[moveData.position] = symbol;
          return newBoard;
        });
      });

      eventSource.addEventListener('complete', () => {
        console.log('Game completed');
        eventSource.close();
        setIsSimulating(false);

        // Fetch final game state
        fetch(`/sessions/${newSessionId}`)
          .then(res => res.json())
          .then(data => {
            if (data.winner === 'PLAYER1_WINS') {
              setGameStatus('PLAYER1_WINS');
            } else if (data.winner === 'PLAYER2_WINS') {
              setGameStatus('PLAYER2_WINS');
            } else if (data.winner === 'DRAW') {
              setGameStatus('DRAW');
            }
          });
      });

      eventSource.onerror = (error) => {
        console.error('SSE error:', error);
        eventSource.close();
        setIsSimulating(false);
        setError('Connection error. Please try again.');
      };

      // Start simulation
      await fetch(`/sessions/${newSessionId}/simulate`, {
        method: 'POST'
      });

    } catch (err) {
      console.error('Error starting game:', err);
      setError(err.message);
      setIsSimulating(false);
    }
  };

  useEffect(() => {
    return () => {
      if (eventSourceRef.current) {
        eventSourceRef.current.close();
      }
    };
  }, []);

  return (
    <div className="App">
      <header className="App-header">
        <h1>🎮 Distributed Tic Tac Toe</h1>
        <p className="subtitle">Automated Microservices Gameplay</p>
      </header>

      {error && (
        <div className="error-message">
          ⚠️ {error}
        </div>
      )}

      <GameControls
        onStartGame={startNewGame}
        gameStatus={gameStatus}
        isSimulating={isSimulating}
      />

      <Board board={board} />

      <MoveHistory moves={moveHistory} />

      <footer className="App-footer">
        <p>Built with Spring Boot, Spring Cloud, and React</p>
        <p className="tech-stack">
          Eureka • Feign • SSE • H2 • JPA
        </p>
      </footer>
    </div>
  );
}

export default App;
