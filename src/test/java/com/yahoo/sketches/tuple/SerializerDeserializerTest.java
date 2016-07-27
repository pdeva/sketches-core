/*
 * Copyright 2015-16, Yahoo! Inc.
 * Licensed under the terms of the Apache License 2.0. See LICENSE file at the project root for terms.
 */

package com.yahoo.sketches.tuple;

import org.testng.annotations.Test;
import org.testng.Assert;

import com.yahoo.sketches.Family;
import com.yahoo.sketches.SketchesArgumentException;
import com.yahoo.sketches.memory.NativeMemory;

public class SerializerDeserializerTest {

  @Test
  public void validSketchType() {
    byte[] bytes = new byte[4];
    bytes[SerializerDeserializer.TYPE_BYTE_OFFSET] = (byte) SerializerDeserializer.SketchType.CompactSketch.ordinal();
    Assert.assertEquals(SerializerDeserializer.getSketchType(new NativeMemory(bytes)), SerializerDeserializer.SketchType.CompactSketch);
  }

  @Test(expectedExceptions = SketchesArgumentException.class)
  public void invalidSketchType() {
    byte[] bytes = new byte[4];
    bytes[SerializerDeserializer.TYPE_BYTE_OFFSET] = 33;
    SerializerDeserializer.getSketchType(new NativeMemory(bytes));
  }

  @Test(expectedExceptions = SketchesArgumentException.class)
  public void deserializeFromMemoryUsupportedClass() {
    NativeMemory mem = null;
    SerializerDeserializer.deserializeFromMemory(mem, 0, "bogus");
  }

  @Test(expectedExceptions = SketchesArgumentException.class)
  public void toByteArrayUnsupportedObject() {
    SerializerDeserializer.toByteArray(Integer.valueOf(0));
  }

  @Test(expectedExceptions = SketchesArgumentException.class)
  public void validateFamilyNotTuple() {
    SerializerDeserializer.validateFamily((byte) 1, (byte) 0); 
  }

  @Test(expectedExceptions = SketchesArgumentException.class)
  public void validateFamilyWrongPreambleLength() {
    SerializerDeserializer.validateFamily((byte) Family.TUPLE.getID(), (byte) 0); 
  }
}
