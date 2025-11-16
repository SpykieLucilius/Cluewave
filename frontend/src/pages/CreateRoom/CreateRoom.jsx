import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/auth/AuthContext.jsx';
import '../../styles/CreateRoom.css';

/**
 * Page allowing an authenticated user to create a new two‑player room.  The
 * logged‑in user becomes the host automatically, so no additional fields
 * are displayed.  Once a room has been created a link is presented that
 * can be shared with a friend, along with a button to enter the room.
 */
export default function CreateRoom() {
  const { user, token } = useAuth();
  const [room, setRoom] = useState(null);
  const navigate = useNavigate();

  const createRoom = async () => {
    try {
      const response = await fetch('/api/rooms', {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      if (!response.ok) {
        const msg = await response.text();
        throw new Error(msg || 'Unable to create room');
      }
      const data = await response.json();
      setRoom(data);
    } catch (err) {
      alert(err.message);
    }
  };

  if (room) {
    const link = `${window.location.origin}/join/${room.code}`;
    return (
      <div className="create-room-container">
        <h2>Room Created!</h2>
        <p>Share this link with your friend:</p>
        <p>
          <a href={link}>{link}</a>
        </p>
        <button onClick={() => navigate(`/room/${room.code}`)}>Enter Room</button>
      </div>
    );
  }

  return (
    <div className="create-room-container">
      <h2>Create a Room</h2>
      {user ? (
        <>
          <p>You will be the host as <strong>{user.username}</strong>.</p>
          <button onClick={createRoom}>Create Room</button>
        </>
      ) : (
        <>
          <p>You must be logged in to create a room.</p>
          <button onClick={() => navigate('/login')}>Login</button>
        </>
      )}
    </div>
  );
}