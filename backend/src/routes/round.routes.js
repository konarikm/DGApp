const express = require("express");
const router = express.Router();
const Round = require("../models/round.model");
const Course = require("../models/course.model");

// GET all rounds
router.get("/", async (req, res) => {
  try {
    const rounds = await Round.find()
      .populate("playerId", "name pdgaNumber email")
      .populate("courseId", "name numberOfHoles parValues");
    res.json(rounds);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// GET round by ID
router.get("/:id", async (req, res) => {
  try {
    const round = await Round.findById(req.params.id)
      .populate("playerId", "name pdgaNumber email")
      .populate("courseId", "name numberOfHoles parValues");
    if (!round) return res.status(404).json({ message: "Round not found" });
    res.json(round);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// GET rounds by playerID
router.get("/player/:playerId", async (req, res) => {
  try {
    const rounds = await Round.find({ playerId: req.params.playerId }).populate(
      "courseId",
      "name numberOfHoles parValues",
    );
    res.json(rounds);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// GET rounds by courseID
router.get("/course/:courseId", async (req, res) => {
  try {
    const rounds = await Round.find({ courseId: req.params.courseId }).populate(
      "playerId",
      "name pdgaNumber",
    );
    res.json(rounds);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// CREATE round
router.post("/", async (req, res) => {
  const { playerId, courseId, scores } = req.body;

  try {
    // Check if the course exists and get its hole count (BUSINESS LOGIC)
    const course = await Course.findById(courseId);
    if (!course) {
      return res.status(404).json({ message: "Course not found." });
    }

    // Validate that the scores array length matches the course's number of holes
    if (scores.length !== course.numberOfHoles) {
      return res.status(400).json({
        message: `Scores array must contain ${course.numberOfHoles} scores, but received ${scores.length}.`,
      });
    }

    const round = new Round({
      playerId,
      courseId,
      scores,
      date: new Date(),
    });

    const newRound = await round.save();
    res.status(201).json(newRound);
  } catch (err) {
    // Catches Mongoose validation errors (e.g., scores are not positive integers)
    res.status(400).json({ message: err.message });
  }
});

// UPDATE round
router.put("/:id", async (req, res) => {
  const { courseId, scores } = req.body;
  try {
    // If scores are updated, we must re-validate against the course's hole count
    if (scores && courseId) {
      const course = await Course.findById(courseId);
      if (!course) {
        return res.status(404).json({ message: "Course not found." });
      }
      if (scores.length !== course.numberOfHoles) {
        return res.status(400).json({
          message: `Scores array must contain ${course.numberOfHoles} scores, but received ${scores.length}.`,
        });
      }
    }

    const updatedRound = await Round.findByIdAndUpdate(
      req.params.id,
      req.body,
      { new: true, runValidators: true },
    );

    if (!updatedRound)
      return res.status(404).json({ message: "Round not found" });

    res.json(updatedRound);
  } catch (err) {
    res.status(400).json({ message: err.message });
  }
});

// DELETE round
router.delete("/:id", async (req, res) => {
  try {
    const deletedRound = await Round.findByIdAndDelete(req.params.id);
    if (!deletedRound)
      return res.status(404).json({ message: "Round not found" });

    res.json({ message: "Round deleted" });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

module.exports = router;
