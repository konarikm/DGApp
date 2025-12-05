const mongoose = require("mongoose");

const roundSchema = new mongoose.Schema(
  {
    playerId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Player",
      required: true,
    },
    courseId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Course",
      required: true,
    },
    date: {
      type: Date,
      default: () => {
        const now = new Date();
        now.setSeconds(0);
        now.setMilliseconds(0);
        return now;
      },
    },
    scores: {
      type: [Number],
      required: true,
      validate: {
        validator: function (arr) {
          return arr.every((score) => score > 0 && Number.isInteger(score));
        },
        message: "All scores must be positive integers.",
      },
    },
  },
  { timestamps: true },
);

module.exports = mongoose.model("Round", roundSchema);
