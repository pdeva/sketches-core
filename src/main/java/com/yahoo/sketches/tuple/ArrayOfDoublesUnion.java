/*
 * Copyright 2015-16, Yahoo! Inc.
 * Licensed under the terms of the Apache License 2.0. See LICENSE file at the project root for terms.
 */

package com.yahoo.sketches.tuple;

import com.yahoo.sketches.memory.Memory;

/**
 * The base class for unions of tuple sketches of type ArrayOfDoubles.
 */
public abstract class ArrayOfDoublesUnion {
  final int nomEntries_;
  final int numValues_;
  final long seed_;
  final short seedHash_;
  ArrayOfDoublesQuickSelectSketch sketch_;
  long theta_;

  ArrayOfDoublesUnion(final ArrayOfDoublesQuickSelectSketch sketch) {
    nomEntries_ = sketch.getNominalEntries();
    numValues_ = sketch.getNumValues();
    seed_ = sketch.getSeed();
    seedHash_ = Util.computeSeedHash(seed_);
    sketch_ = sketch;
    theta_ = sketch.getThetaLong();
  }

  /**
   * Updates the union by adding a set of entries from a given sketch
   * @param sketchIn sketch to add to the union
   */
  public void update(final ArrayOfDoublesSketch sketchIn) {
    if (sketchIn == null) return;
    Util.checkSeedHashes(seedHash_, sketchIn.getSeedHash());
    if (sketchIn.isEmpty()) return;
    if (sketchIn.getThetaLong() < theta_) theta_ = sketchIn.getThetaLong();
    ArrayOfDoublesSketchIterator it = sketchIn.iterator();
    while (it.next()) {
      sketch_.merge(it.getKey(), it.getValues());
    }
  }

  /**
   * Returns the resulting union in the form of a compact sketch
   * @param mem memory for the result (can be null)
   * @return compact sketch representing the union (off-heap if memory is provided)
   */
  public ArrayOfDoublesCompactSketch getResult(final Memory mem) {
    trim();
    return sketch_.compact(mem);
  }

  /**
   * Returns the resulting union in the form of a compact sketch
   * @return on-heap compact sketch representing the union
   */
  public ArrayOfDoublesCompactSketch getResult() {
    return getResult(null);
  }

  /**
   * Resets the union to an empty state
   */
  public abstract void reset();

  /**
   * @return a byte array representation of this object
   */
  public byte[] toByteArray() {
    trim();
    return sketch_.toByteArray();
  }

  /**
   * @param nomEntries Nominal number of entries. Forced to the nearest power of 2 greater than 
   * given value.
   * @param numValues Number of double values to keep for each key
   * @return maximum required storage bytes given nomEntries and numValues
   */
  public static int getMaxBytes(final int nomEntries, final int numValues) {
    return ArrayOfDoublesQuickSelectSketch.getMaxBytes(nomEntries, numValues);
  }

  private void trim() {
    sketch_.trim();
    if (theta_ < sketch_.getThetaLong()) {
      sketch_.setThetaLong(theta_);
      sketch_.rebuild();
    }
  }

}
