import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/auth/AuthContext.jsx';
import '../../styles/Room.css';

/**
 * Room page displays the current state of a game room.  It fetches the
 * room details from the backend and shows the list of players, waiting
 * status and a start button for the host when both players are present.
 */
export default function Room() {
  const { code } = useParams();
  const navigate = useNavigate();
  const { user, token } = useAuth();
  const [room, setRoom] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchRoom = async () => {
    try {
      const response = await fetch(`/api/rooms/${code}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      if (!response.ok) {
        const msg = await response.text();
        throw new Error(msg || 'Failed to load room');
      }
      const data = await response.json();
      setRoom(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRoom();
    // set up polling or websocket here if needed in future
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const startRound = async () => {
    try {
      const response = await fetch(`/api/rooms/${code}/start-round`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      if (!response.ok) {
        const msg = await response.text();
        throw new Error(msg || 'Failed to start round');
      }
      // refresh room state after starting round
      await fetchRoom();
    } catch (err) {
      alert(err.message);
    }
  };

  if (loading) {
    return <div className="room-container"><p>Loading...</p></div>;
  }
  if (error) {
    return <div className="room-container"><p>Error: {error}</p></div>;
  }
  if (!room) {
    return <div className="room-container"><p>Room not found.</p></div>;
  }

  // Determine if the current user is the host
  const isHost = user?.username === room.hostName;
  const players = room.players || [];

  return (
    <div className="room-container">
      <h2>Room {room.code}</h2>
      <p><strong>Host:</strong> {room.hostName}</p>
      <div className="players-list">
        <h3>Players</h3>
        <ul>
          {players.map((p) => (
            <li key={p.id}>{p.name}</li>
          ))}
        </ul>
      </div>
      {players.length < 2 ? (
        <p className="waiting-text">
          Waiting for your friend to join... Share this code: <strong>{room.code}</strong>
        </p>
      ) : (
        <>
          {room.state === 'lobby' && isHost && (
            <button className="start-button" onClick={startRound}>Start Game</button>
          )}
          {room.state === 'in_round' && (
            <p className="round-text">The round has started! Good luck!</p>
          )}
        </>
      )}
      <button className="back-button" onClick={() => navigate('/')}>Back to Home</button>
    </div>
  );
}