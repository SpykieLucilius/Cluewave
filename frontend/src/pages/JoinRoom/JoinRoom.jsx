import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function JoinRoom() {
  const [playerName, setPlayerName] = useState('');
  const [roomCode, setRoomCode] = useState('');
  const [hostEmail, setHostEmail] = useState('');
  const navigate = useNavigate();

  const joinByCode = async (e) => {
    e.preventDefault();
    const response = await fetch(`/api/rooms/${roomCode}/join`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ playerName }),
    });
    if (response.ok) {
      navigate(`/room/${roomCode}`);
    } else {
      alert('Could not join room');
    }
  };

  const joinByEmail = async (e) => {
    e.preventDefault();
    const response = await fetch('/api/rooms/join-by-email', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: hostEmail, playerName }),
    });
    if (response.ok) {
      const room = await response.json();
      navigate(`/room/${room.code}`);
    } else {
      alert('Could not join room by email');
    }
  };

  return (
    <div>
      <h2>Join by Code</h2>
      <form onSubmit={joinByCode}>
        <label>
          Room Code
          <input
            type="text"
            value={roomCode}
            onChange={(e) => setRoomCode(e.target.value)}
            required
          />
        </label>
        <label>
          Your Name
          <input
            type="text"
            value={playerName}
            onChange={(e) => setPlayerName(e.target.value)}
            required
          />
        </label>
        <button type="submit">Join</button>
      </form>

      <h2>Join by Host Email</h2>
      <form onSubmit={joinByEmail}>
        <label>
          Host Email
          <input
            type="email"
            value={hostEmail}
            onChange={(e) => setHostEmail(e.target.value)}
            required
          />
        </label>
        <label>
          Your Name
          <input
            type="text"
            value={playerName}
            onChange={(e) => setPlayerName(e.target.value)}
            required
          />
        </label>
        <button type="submit">Join</button>
      </form>
    </div>
  );
}