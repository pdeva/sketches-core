package com.yahoo.sketches.quantiles;

/**
 * @author Prashant Deva
 */
public abstract class AbstractDoublesSketch extends DoublesSketch {
    /**
     * The smallest value ever seen in the stream.
     */
    double minValue_;

    /**
     * The largest value ever seen in the stream.
     */
    double maxValue_;

    AbstractDoublesSketch(int k) {
        super(k);
    }
}
