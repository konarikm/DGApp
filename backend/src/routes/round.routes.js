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
  const { player, course: courseId, scores } = req.body;

  try {
    // Check if the course exists and get its hole count (BUSINESS LOGIC)
    const courseDoc = await Course.findById(courseId);
    if (!courseDoc) {
      return res.status(404).json({ message: "Course not found." });
    }

    // Validate that the scores array length matches the course's number of holes
    if (scores.length !== courseDoc.numberOfHoles) {
      return res.status(400).json({
        message: `Scores array must contain ${courseDoc.numberOfHoles} scores, but received ${scores.length}.`,
      });
    }

    const round = new Round({
      player,
      course: courseId,
      scores,
      date: new Date(),
    });

    const newRound = await round.save();
    res.status(201).json(newRound);
  } catch (err) {
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
