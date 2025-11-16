import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/auth/AuthContext.jsx';
import '../../styles/JoinRoom.css';

/**
 * Page for joining an existing room.  Users can either enter a room code or
 * search by the host’s email.  The player name field is optional and
 * defaults to the logged‑in user’s username when left blank.
 */
export default function JoinRoom() {
  const { user, token } = useAuth();
  const navigate = useNavigate();
  // default the player name to the current username if available
  const [playerName, setPlayerName] = useState(user?.username || '');
  const [roomCode, setRoomCode] = useState('');
  const [hostEmail, setHostEmail] = useState('');

  const joinByCode = async (e) => {
    e.preventDefault();
    const name = playerName && playerName.trim().length > 0 ? playerName : user?.username;
    try {
      const response = await fetch(`/api/rooms/${roomCode}/join`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ playerName: name }),
      });
      if (!response.ok) {
        const msg = await response.text();
        throw new Error(msg || 'Could not join room');
      }
      navigate(`/room/${roomCode}`);
    } catch (err) {
      alert(err.message);
    }
  };

  const joinByEmail = async (e) => {
    e.preventDefault();
    const name = playerName && playerName.trim().length > 0 ? playerName : user?.username;
    try {
      const response = await fetch('/api/rooms/join-by-email', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ email: hostEmail, playerName: name }),
      });
      if (!response.ok) {
        const msg = await response.text();
        throw new Error(msg || 'Could not join room by email');
      }
      const room = await response.json();
      navigate(`/room/${room.code}`);
    } catch (err) {
      alert(err.message);
    }
  };

  return (
    <div className="join-room-container">
      <h2>Join a Room</h2>
      <form onSubmit={joinByCode} className="join-form">
        <h3>Join by Code</h3>
        <input
          type="text"
          placeholder="Room Code"
          value={roomCode}
          onChange={(e) => setRoomCode(e.target.value.toUpperCase())}
          required
        />
        <input
          type="text"
          placeholder="Your Name (optional)"
          value={playerName}
          onChange={(e) => setPlayerName(e.target.value)}
        />
        <button type="submit">Join by Code</button>
      </form>
      <div className="divider">or</div>
      <form onSubmit={joinByEmail} className="join-form">
        <h3>Join by Host Email</h3>
        <input
          type="email"
          placeholder="Host Email"
          value={hostEmail}
          onChange={(e) => setHostEmail(e.target.value)}
          required
        />
        <input
          type="text"
          placeholder="Your Name (optional)"
          value={playerName}
          onChange={(e) => setPlayerName(e.target.value)}
        />
        <button type="submit">Join by Email</button>
      </form>
    </div>
  );
}