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
  // 1. Destructure player and course using the *new* names for incoming IDs from req.body.
  // We rename 'course' from req.body to 'courseId' locally for clear naming in step 3.
  const { player, course: courseId, scores } = req.body;

  try {
    // 2. Check if the course exists and get its hole count
    // Use courseId to find the document.
    const courseDoc = await Course.findById(courseId);
    if (!courseDoc) {
      return res.status(404).json({ message: "Course not found." });
    }

    // 3. Validate scores length against course's number of holes
    if (scores.length !== courseDoc.numberOfHoles) {
      return res.status(400).json({
        message: `Scores array must contain ${courseDoc.numberOfHoles} scores, but received ${scores.length}.`,
      });
    }

    // 4. Create Round: Mongoose expects ONLY the IDs here!
    const round = new Round({
      player, // Uses player ID (string) from req.body
      course: courseId, // Uses course ID (string) from req.body
      scores,
      date: new Date(),
    });

    const newRound = await round.save();
    res.status(201).json(newRound);
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

    // CRITICAL FIX: Extract the scores array and optionally the date from req.body.
    // Ensure we exclude Mongoose system fields like _id.
    const { scores, date, ...otherUpdates } = req.body;

    // 2. Apply updates manually to the Mongoose document instance
    if (scores && Array.isArray(scores)) {
      // Direct assignment to Mongoose array is necessary to trigger change tracking and validators
      round.scores = scores;

      // OPTIONAL: If date is present in body, update it (we removed it from UI, but keep for API robustness)
      if (date) {
        round.date = date;
      }

      // Apply any other updates (though we only edit scores in the UI)
      Object.assign(round, otherUpdates);
    } else if (otherUpdates) {
      // If only other fields were sent (e.g., date), assign them
      Object.assign(round, otherUpdates);
    }

    // NOTE: Scores validation logic (length check) relies on the schema validator
    // which runs during round.save()

    // 3. Save the updated document (triggers full validation and pre-save hooks)
    const updatedRound = await round.save();

    res.json(updatedRound);
  } catch (err) {
    // 400 status for validation error
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
