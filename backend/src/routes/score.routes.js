const express = require("express");
const router = express.Router();
const Score = require("../models/score.model");

// GET all scores
router.get("/", async (req, res) => {
  try {
    const scores = await Score.find()
      .populate("playerId", "name pdgaNumber email")
      .populate("courseId", "name numberOfHoles");
    res.json(scores);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// GET score by ID
router.get("/:id", async (req, res) => {
  try {
    const score = await Score.findById(req.params.id)
      .populate("playerId", "name pdgaNumber email")
      .populate("courseId", "name numberOfHoles");
    if (!score) return res.status(404).json({ message: "Score not found" });
    res.json(score);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// GET scores by playerID
router.get("/player/:playerId", async (req, res) => {
  try {
    const scores = await Score.find({ playerId: req.params.playerId }).populate(
      "courseId",
      "name numberOfHoles",
    );
    res.json(scores);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// GET scores by courseID
router.get("/course/:courseId", async (req, res) => {
  try {
    const scores = await Score.find({ courseId: req.params.courseId }).populate(
      "playerId",
      "name",
    );
    res.json(scores);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// CREATE score
router.post("/", async (req, res) => {
  const { playerId, courseId, scores } = req.body;

  const score = new Score({
    playerId,
    courseId,
    scores,
    date: new Date(),
  });

  try {
    const newScore = await score.save();
    res.status(201).json(newScore);
  } catch (err) {
    res.status(400).json({ message: err.message });
  }
});

// UPDATE score
router.put("/:id", async (req, res) => {
  try {
    const updatedScore = await Score.findByIdAndUpdate(
      req.params.id,
      req.body,
      { new: true, runValidators: true },
    );

    if (!updatedScore)
      return res.status(404).json({ message: "Score not found" });

    res.json(updatedScore);
  } catch (err) {
    res.status(400).json({ message: err.message });
  }
});

// DELETE score
router.delete("/:id", async (req, res) => {
  try {
    const deleted = await Score.findByIdAndDelete(req.params.id);
    if (!deleted) return res.status(404).json({ message: "Score not found" });

    res.json({ message: "Score deleted" });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

module.exports = router;
