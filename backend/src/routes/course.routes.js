const express = require("express");
const router = express.Router();
const Course = require("../models/course.model");

// GET all courses OR SEARCH using query parameters
router.get("/", async (req, res) => {
  try {
    const { search } = req.query;
    let courses;

    if (search) {
      // If a search query is provided, use the text index
      courses = await Course.find({ $text: { $search: search } });
    } else {
      // Otherwise, return all courses
      courses = await Course.find();
    }
    res.json(courses);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// GET course by ID
router.get("/:id", async (req, res) => {
  try {
    const course = await Course.findById(req.params.id);
    if (!course) return res.status(404).json({ message: "Course not found" });
    res.json(course);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

// CREATE a new course
router.post("/", async (req, res) => {
  const { name, location, numberOfHoles, description, parValues } = req.body;
  const course = new Course({
    name,
    location,
    numberOfHoles,
    description,
    parValues,
  });

  try {
    const newCourse = await course.save();
    res.status(201).json(newCourse);
  } catch (err) {
    res.status(400).json({ message: err.message });
  }
});

// UPDATE a course
router.put("/:id", async (req, res) => {
  try {
    // 1. Find the existing course document by ID
    const course = await Course.findById(req.params.id);
    if (!course) {
      return res.status(404).json({ message: "Course not found" });
    }

    const { _id, ...updateData } = req.body;

    // 2. Apply updates manually to the Mongoose document instance
    Object.assign(course, updateData);

    // 3. Save the updated document (triggers full validation and pre-save hooks)
    const updatedCourse = await course.save();

    res.json(updatedCourse);
  } catch (err) {
    res.status(400).json({ message: err.message });
  }
});

// DELETE a course
router.delete("/:id", async (req, res) => {
  try {
    const deletedCourse = await Course.findByIdAndDelete(req.params.id);
    if (!deletedCourse)
      return res.status(404).json({ message: "Course not found" });
    res.json({ message: "Course deleted successfully" });
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
});

module.exports = router;
