const mongoose = require("mongoose");

const courseSchema = new mongoose.Schema(
  {
    name: { type: String, required: true },
    location: { type: String },
    numberOfHoles: { type: Number, required: true },
    description: { type: String },
    parValues: {
      type: [Number],
      required: true,
      validate: {
        validator: function (arr) {
          return arr.length === this.numberOfHoles;
        },
        message: "The number of par values must match numberOfHoles.",
      },
    },
  },
  { timestamps: true },
);

// Text search index
courseSchema.index({ name: "text", location: "text" });

module.exports = mongoose.model("Course", courseSchema);
