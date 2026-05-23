import React from 'react';
import './GameControls.css';

const GameControls = ({ onStartGame, gameStatus, isSimulating }) => {
  const getStatusMessage = () => {
    switch (gameStatus) {
      case 'PLAYER1_WINS':
        return '🎉 Player 1 (X) Wins!';
      case 'PLAYER2_WINS':
        return '🎉 Player 2 (O) Wins!';
      case 'DRAW':
        return '🤝 Draw!';
      case 'IN_PROGRESS':
        return '🎮 Game in Progress...';
      default:
        return 'Ready to start';
    }
  };

  const getStatusClass = () => {
    switch (gameStatus) {
      case 'PLAYER1_WINS':
      case 'PLAYER2_WINS':
        return 'status-win';
      case 'DRAW':
        return 'status-draw';
      case 'IN_PROGRESS':
        return 'status-progress';
      default:
        return '';
    }
  };

  return (
    <div className="game-controls">
      <button
        onClick={onStartGame}
        disabled={isSimulating}
        className="start-button"
      >
        {isSimulating ? 'Simulating...' : 'Start New Game'}
      </button>
      <div className={`status-message ${getStatusClass()}`}>
        {getStatusMessage()}
      </div>
    </div>
  );
};

export default GameControls;
