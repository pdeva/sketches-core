/*
 * Copyright 2015-16, Yahoo! Inc.
 * Licensed under the terms of the Apache License 2.0. See LICENSE file at the project root for terms.
 */

package com.yahoo.sketches.tuple;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.yahoo.sketches.Family;
import com.yahoo.sketches.SketchesArgumentException;
import com.yahoo.sketches.memory.Memory;
import com.yahoo.sketches.memory.MemoryRegion;
import com.yahoo.sketches.memory.NativeMemory;

final class SerializerDeserializer {
  static enum SketchType { QuickSelectSketch, CompactSketch, ArrayOfDoublesQuickSelectSketch, 
    ArrayOfDoublesCompactSketch }
  
  static final int TYPE_BYTE_OFFSET = 3;

  private static final Map<String, Method> deserializeMethodCache = new HashMap<String, Method>();

  static void validateFamily(final byte familyId, final byte preambleLongs) {
    final Family family = Family.idToFamily(familyId);
    if (family.equals(Family.TUPLE)) {
      if (preambleLongs != Family.TUPLE.getMinPreLongs()) {
        throw new SketchesArgumentException(
            "Possible corruption: Invalid PreambleLongs value for family TUPLE: " + preambleLongs);
      }
    } else {
      throw new SketchesArgumentException(
          "Possible corruption: Invalid Family: " + family.toString());
    }
  }

  static void validateType(final byte sketchTypeByte, final SketchType expectedType) {
    SketchType sketchType = getSketchType(sketchTypeByte);
    if (!sketchType.equals(expectedType)) {
      throw new SketchesArgumentException("Sketch Type mismatch. Expected " + expectedType.name() 
        + ", got " + sketchType.name());
    }
  }

  static SketchType getSketchType(final Memory mem) {
    final byte sketchTypeByte = mem.getByte(TYPE_BYTE_OFFSET);
    return getSketchType(sketchTypeByte);
  }

  static byte[] toByteArray(final Object object) {
    try {
      final String className = object.getClass().getName();
      final byte[] objectBytes = 
          ((byte[]) object.getClass().getMethod("toByteArray", (Class<?>[])null).invoke(object));
      final byte[] bytes = new byte[1 + className.length() + objectBytes.length];
      final Memory mem = new NativeMemory(bytes);
      int offset = 0;
      mem.putByte(offset++, (byte)className.length());
      mem.putByteArray(offset, className.getBytes(UTF_8), 0, className.length());
      offset += className.length();
      mem.putByteArray(offset, objectBytes, 0, objectBytes.length);
      return bytes;
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException 
        | SketchesArgumentException | InvocationTargetException e) {
      throw new SketchesArgumentException("Failed to serialize given object: " + e);
    }
  }

  static <T> DeserializeResult<T> deserializeFromMemory(final Memory mem, final int offset) {
    final int classNameLength = mem.getByte(offset);
    final byte[] classNameBuffer = new byte[classNameLength];
    mem.getByteArray(offset + 1, classNameBuffer, 0, classNameLength);
    final String className = new String(classNameBuffer, UTF_8);
    final DeserializeResult<T> result = 
        deserializeFromMemory(mem, offset + classNameLength + 1, className);
    return new DeserializeResult<T>(result.getObject(), result.getSize() + classNameLength + 1);
  }

  @SuppressWarnings("unchecked")
  static <T> DeserializeResult<T> 
      deserializeFromMemory(final Memory mem, final int offset, final String className) {
    try {
      Method method = deserializeMethodCache.get(className);
      if (method == null) {
          method = Class.forName(className).getMethod("fromMemory", Memory.class);
          deserializeMethodCache.put(className, method);
      }
      return (DeserializeResult<T>) 
          method.invoke(null, new MemoryRegion(mem, offset, mem.getCapacity() - offset));
    } catch (IllegalAccessException | SketchesArgumentException | InvocationTargetException 
        | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
      throw new SketchesArgumentException("Failed to deserialize class " + className + " " + e);
    }
  }

  private static SketchType getSketchType(final byte sketchTypeByte) {
    if (sketchTypeByte < 0 || sketchTypeByte >= SketchType.values().length) {
      throw new SketchesArgumentException("Invalid Sketch Type " + sketchTypeByte);
    }
    return SketchType.values()[sketchTypeByte];
  }
}
