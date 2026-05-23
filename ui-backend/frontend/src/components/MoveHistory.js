import React from 'react';
import './MoveHistory.css';

const MoveHistory = ({ moves }) => {
  const getPositionLabel = (position) => {
    const row = Math.floor(position / 3);
    const col = position % 3;
    return `${String.fromCharCode(65 + row)}${col + 1}`;
  };

  const formatTime = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleTimeString();
  };

  return (
    <div className="move-history">
      <h3>Move History</h3>
      {moves.length === 0 ? (
        <p className="no-moves">No moves yet</p>
      ) : (
        <div className="moves-list">
          {moves.map((move, index) => (
            <div key={index} className="move-item">
              <span className="move-number">#{index + 1}</span>
              <span className={`move-player ${move.player.toLowerCase()}`}>
                {move.player === 'PLAYER1' ? 'X' : 'O'}
              </span>
              <span className="move-position">
                Position {move.position} ({getPositionLabel(move.position)})
              </span>
              <span className="move-time">{formatTime(move.timestamp)}</span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default MoveHistory;
