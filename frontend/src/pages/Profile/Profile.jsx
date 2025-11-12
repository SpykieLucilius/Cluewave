import { useAuth } from '../../context/auth/AuthContext.jsx';

export default function Profile() {
  const { user } = useAuth();

  if (!user) {
    return (
      <main style={{ padding: 24, color: '#fff' }}>
        Please login to view your profile.
      </main>
    );
  }

  return (
    <main style={{ padding: 24, color: '#fff' }}>
      <h1>Profile</h1>
      <p><strong>Username:</strong> {user.username}</p>
      <p><strong>Email:</strong> {user.email}</p>
    </main>
  );
}
