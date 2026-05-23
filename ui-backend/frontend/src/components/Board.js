import React from 'react';
import './Board.css';

const Board = ({ board, winningLine }) => {
  const renderCell = (index) => {
    const value = board[index];
    const isWinning = winningLine && winningLine.includes(index);

    return (
      <div
        key={index}
        className={`cell ${isWinning ? 'winning' : ''}`}
      >
        {value === 'X' && <span className="symbol x">X</span>}
        {value === 'O' && <span className="symbol o">O</span>}
      </div>
    );
  };

  return (
    <div className="board">
      {[0, 1, 2, 3, 4, 5, 6, 7, 8].map(renderCell)}
    </div>
  );
};

export default Board;
