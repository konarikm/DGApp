const express = require("express");
const cors = require("cors");
const connectDB = require("./db");
const courseRoutes = require("./routes/course.routes");
const playerRoutes = require("./routes/player.routes");
const roundRoutes = require("./routes/round.routes");
require("dotenv").config();

const app = express();

// Middleware
app.use(cors());
app.use(express.json());

// Connect to MongoDB
connectDB();

// Route handlers
app.use("/api/courses", courseRoutes);
app.use("/api/players", playerRoutes);
app.use("/api/rounds", roundRoutes);

// Simple root check
app.get("/", (req, res) => {
  res.send("Disc Golf API is running.");
});

// Start server
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
