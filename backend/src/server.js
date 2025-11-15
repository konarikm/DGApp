const express = require("express");
const cors = require("cors");
const connectDB = require("./db");
const courseRoutes = require("./routes/course.routes");
const playerRoutes = require("./routes/player.routes");
const scoreRoutes = require("./routes/score.routes");
require("dotenv").config();

const app = express();

app.use("/api/courses", courseRoutes);
app.use("/api/players", playerRoutes);
app.use("/api/scores", scoreRoutes);

// Middleware
app.use(cors());
app.use(express.json());

// Connect to MongoDB
connectDB();

// Start server
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
