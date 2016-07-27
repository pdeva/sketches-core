/*
 * Copyright 2015-16, Yahoo! Inc.
 * Licensed under the terms of the Apache License 2.0. See LICENSE file at the project root for terms.
 */

package com.yahoo.sketches.tuple;

import static com.yahoo.sketches.hash.MurmurHash3.hash;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.yahoo.sketches.SketchesArgumentException;

final class Util {
  /**
  * Gets the starting power of 2 so that it is a proper sub-multiple of the target by resize ratio.
  * This version uses an integer to specify the lgResizeRatio.
  *
  * @param lgTarget Power of 2 of the target number
  * @param lgResizeRatio Values 0 to 3 (0 - no resize (max size upfront), 1 - double, 
  * 2 - four times, 3 - 8 times)
  * @param lgMin Minimum starting power of 2
  * @return The returning log2 size will be a proper sub-multiple of the final lgTarget by the 
  * lgResizeRatio
  */
  static final int startingSubMultiple(final int lgTarget, final int lgResizeRatio, 
      final int lgMin) {
    int lgStart;
    if (lgResizeRatio > 0) {
      lgStart = (Math.abs(lgTarget - lgMin) % lgResizeRatio) + lgMin;
    } else {
      lgStart = (lgTarget < lgMin) ? lgMin : lgTarget;
    }
    return lgStart;
  }

  static final long[] doubleToLongArray(final double value) {
    final double d = (value == 0.0) ? 0.0 : value; // canonicalize -0.0, 0.0
    final long[] array = { Double.doubleToLongBits(d) }; // canonicalize all NaN forms
    return array;
  }

  static final byte[] stringToByteArray(final String value) {
    if (value == null || value.isEmpty()) return null;
    return value.getBytes(UTF_8);
  }

  /**
   * Computes and checks the 16-bit seed hash from the given long seed.
   * The seed hash may not be zero in order to maintain compatibility with older serialized
   * versions that did not have this concept.
   * @param seed <a href="{@docRoot}/resources/dictionary.html#seed">See Update Hash Seed</a>
   * @return the seed hash.
   */
  static short computeSeedHash(final long seed) {
    final long[] seedArr = {seed};
    final short seedHash = (short)((hash(seedArr, 0L)[0]) & 0xFFFFL);
    if (seedHash == 0) {
      throw new SketchesArgumentException(
          "The given seed: " + seed + " produced a seedHash of zero. " 
              + "You must choose a different seed.");
    }
    return seedHash; 
  }
  
  static final void checkSeedHashes(final short seedHashA, final short seedHashB) {
    if (seedHashA != seedHashB) {
      throw new SketchesArgumentException("Incompatible Seed Hashes. " + seedHashA + ", " 
          + seedHashB);
    }
        
  }

}
