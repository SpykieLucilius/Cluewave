import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function CreateRoom() {
  const [hostName, setHostName] = useState('');
  const [hostEmail, setHostEmail] = useState('');
  const [room, setRoom] = useState(null);
  const navigate = useNavigate();

  const createRoom = async (e) => {
    e.preventDefault();
    const response = await fetch('/api/rooms', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ hostName, hostEmail }),
    });
    if (response.ok) {
      const data = await response.json();
      setRoom(data);
    } else {
      alert('Error creating room');
    }
  };

  if (room) {
    const link = `${window.location.origin}/join/${room.code}`;
    return (
      <div>
        <h2>Room Created</h2>
        <p>Share this link with your friend:</p>
        <p>
          <a href={link}>{link}</a>
        </p>
        <button onClick={() => navigate(`/room/${room.code}`)}>Go to Room</button>
      </div>
    );
  }

  return (
    <form onSubmit={createRoom}>
      <h2>Create a Game</h2>
      <label>
        Host Name
        <input
          type="text"
          value={hostName}
          onChange={(e) => setHostName(e.target.value)}
          required
        />
      </label>
      <label>
        Host Email
        <input
          type="email"
          value={hostEmail}
          onChange={(e) => setHostEmail(e.target.value)}
          required
        />
      </label>
      <button type="submit">Create Room</button>
    </form>
  );
}