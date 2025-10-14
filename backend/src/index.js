// backend/src/index.js
import path from "path";
import express from "express";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
app.set("trust proxy", true);

// => Depuis backend/src vers frontend/dist : ../../frontend/dist
const distPath = path.resolve(__dirname, "..", "..", "frontend", "dist");

// Sert les fichiers statiques du build Vite
app.use(express.static(distPath));

// Endpoint de santé (pour probes Heroku ou checks)
app.get("/healthz", (_req, res) => res.status(200).send("ok"));

// ⚠️ Catch-all compatible Express 5 (path-to-regexp v6)
app.get(/.*/, (_req, res) => {
  res.sendFile(path.join(distPath, "index.html"));
});

// Démarrage du serveur
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Listening on ${PORT}`);
});
