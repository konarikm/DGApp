const mongoose = require("mongoose");

// Function to handle database connection
async function connectDB() {
  try {
    // 1. Connection attempt
    await mongoose.connect(process.env.MONGO_URI);
    console.log("MongoDB connected successfully.");

    // 2. Set up Mongoose connection event listeners
    const db = mongoose.connection;

    // Log connection errors
    db.on("error", (err) => {
      console.error("MongoDB connection error:", err);
    });

    // Log disconnection
    db.on("disconnected", () => {
      console.log("MongoDB disconnected. Attempting to reconnect...");
    });

    // Log successful reconnection
    db.on("reconnected", () => {
      console.log("MongoDB reconnected!");
    });

    // 3. Graceful application shutdown (optional but recommended)
    process.on("SIGINT", async () => {
      await db.close(false);
      console.log("MongoDB connection closed due to app termination (SIGINT).");
      process.exit(0);
    });
  } catch (error) {
    // Initial connection failed, log error and exit process
    console.error("MongoDB initial connection failed:", error.message);
    process.exit(1);
  }
}

module.exports = connectDB;
