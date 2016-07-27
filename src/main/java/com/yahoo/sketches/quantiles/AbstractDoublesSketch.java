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


    /**
     * In the initial on-heap version, equals combinedBuffer_.length.
     * May differ in later versions that grow space more aggressively.
     * Also, in the off-heap version, combinedBuffer_ won't even be a java array,
     * so it won't know its own length.
     */
    int combinedBufferItemCapacity_;

    /**
     * Number of samples currently in base buffer.
     *
     * <p>Count = N % (2*K)
     */
    int baseBufferCount_;

    /**
     * Active levels expressed as a bit pattern.
     *
     * <p>Pattern = N / (2 * K)
     */
    long bitPattern_;


    AbstractDoublesSketch(int k) {
        super(k);
    }
}
