package com.yahoo.sketches.quantiles;

import com.yahoo.sketches.memory.Memory;

/**
 * @author Prashant Deva
 */
public class NativeDoublesSketch extends DoublesSketch
{

    NativeDoublesSketch(int k) {
        super(k);
    }

    @Override
    public void update(double dataItem) {

    }

    @Override
    public double getQuantile(double fraction) {
        return 0;
    }

    @Override
    public double[] getQuantiles(double[] fractions) {
        return new double[0];
    }

    @Override
    public double[] getPMF(double[] splitPoints) {
        return new double[0];
    }

    @Override
    public double[] getCDF(double[] splitPoints) {
        return new double[0];
    }

    @Override
    public int getK() {
        return 0;
    }

    @Override
    public double getMinValue() {
        return 0;
    }

    @Override
    public double getMaxValue() {
        return 0;
    }

    @Override
    public void reset() {

    }

    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }

    @Override
    public byte[] toByteArray(boolean sort)
    {
        return new byte[0];
    }

    @Override
    public String toString(boolean sketchSummary, boolean dataDetail) {
        return null;
    }

    @Override
    public DoublesSketch downSample(int smallerK) {
        return null;
    }

    @Override
    public void putMemory(Memory dstMem, boolean sort)
    {

    }

    @Override
    public void putMemory(Memory dstMem) {

    }

    @Override
    int getBaseBufferCount() {
        return 0;
    }

    @Override
    int getCombinedBufferItemCapacity() {
        return 0;
    }

    @Override
    double[] getCombinedBuffer() {
        return new double[0];
    }
}
