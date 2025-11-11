import React from "react";
import { Link } from "react-router-dom";
import "../styles/App.css"; // optionnel, enlève si tu n'as pas

const NotFound = () => {
  return (
    <div style={styles.container}>
      <h1 style={styles.title}>404</h1>
      <p style={styles.text}>Page not found.</p>
      <Link to="/" style={styles.link}>
        Back to home
      </Link>
    </div>
  );
};

const styles = {
  container: {
    minHeight: "100vh",
    display: "flex",
    flexDirection: "column",
    gap: "1rem",
    justifyContent: "center",
    alignItems: "center",
  },
  title: {
    fontSize: "3rem",
    fontWeight: 700,
  },
  text: {
    fontSize: "1.1rem",
  },
  link: {
    color: "#007bff",
    textDecoration: "underline",
  },
};

export default NotFound;
