const express = require("express");
const router = express.Router();
const Round = require("../models/round.model");
const Course = require("../models/course.model");

const PLAYER_FIELDS = "name pdgaNumber email";
const COURSE_FIELDS = "name location numberOfHoles description parValues";

// GET all rounds
router.get("/", async (req, res) => {
  try {
    const rounds = await Round.find()
      .sort({ date: -1 })
      .populate("player", PLAYER_FIELDS)
      .populate("course", COURSE_FIELDS);
    res.json(rounds);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// GET round by ID
router.get("/:id", async (req, res) => {
  try {
    const round = await Round.findById(req.params.id)
      .populate("player", PLAYER_FIELDS)
      .populate("course", COURSE_FIELDS);
    if (!round) return res.status(404).json({ message: "Round not found" });
    res.json(round);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// GET rounds by playerID
router.get("/player/:id", async (req, res) => {
  try {
    const rounds = await Round.find({ player: req.params.id })
      .populate("player", PLAYER_FIELDS)
      .populate("course", COURSE_FIELDS);

    res.json(rounds);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// GET rounds by courseID
router.get("/course/:id", async (req, res) => {
  try {
    const rounds = await Round.find({ course: req.params.id })
      .populate("player", PLAYER_FIELDS)
      .populate("course", COURSE_FIELDS);
    res.json(rounds);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// CREATE round
router.post("/", async (req, res) => {
  const { player, course, scores } = req.body;
  try {
    const courseDoc = await Course.findById(course);
    if (!courseDoc) {
      return res.status(404).json({ message: "Course not found." });
    }

    if (scores.length !== courseDoc.numberOfHoles) {
      return res.status(400).json({
        message: `Scores array must contain ${courseDoc.numberOfHoles} scores, but received ${scores.length}.`,
      });
    }

    const round = new Round({
      player,
      course,
      scores,
      date: new Date(),
    });

    let newRound = await round.save();

    res.status(201).json({ id: newRound._id });
  } catch (err) {
    // Catches Mongoose validation errors
    res.status(400).json({ message: err.message });
  }
});

// UPDATE round
router.put("/:id", async (req, res) => {
  try {
    // 1. Find the existing round document by ID
    const round = await Round.findById(req.params.id);
    if (!round) {
      return res.status(404).json({ message: "Round not found" });
    }

    // Deconstruct fields from req.body (assuming only scores and date are sent)
    const { scores, date, ...otherUpdates } = req.body;

    // 2. Apply updates manually
    if (scores && Array.isArray(scores)) {
      round.scores = scores; // Direct assignment to trigger change tracking
    }
    if (date) {
      round.date = date; // Update date
    }
    // Apply any other updates
    Object.assign(round, otherUpdates);

    // 3. Save the updated document (triggers validation)
    let updatedRound = await round.save();

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
