/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.rococoa.foundation;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.imageio.ImageIO;

import org.rococoa.cocoa.foundation.NSData;
import org.rococoa.cocoa.foundation.NSDate;

import vavi.util.Debug;


/**
 * AEConverter.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2022/03/06 umjammer initial version <br>
 */
public class AEConverter {

    /** ae -> java */
    private final Map<Integer, Function<NSAppleEventDescriptor, Object>> handlerDict = new HashMap<>();

    {
        // register default handlers

        // string -> NSStrings
        handlerDict.put(NSAppleEventDescriptor.typeUnicodeText, AEConverter::stringWithAEDesc);
        handlerDict.put(NSAppleEventDescriptor.typeText, AEConverter::stringWithAEDesc);
        handlerDict.put(NSAppleEventDescriptor.typeUTF8Text, AEConverter::stringWithAEDesc);
        handlerDict.put(NSAppleEventDescriptor.typeCString, AEConverter::stringWithAEDesc);
        handlerDict.put(NSAppleEventDescriptor.typeChar, AEConverter::stringWithAEDesc);

        // number/bool -> NSNumber
        handlerDict.put(NSAppleEventDescriptor.typeBoolean, AEConverter::numberWithAEDesc);
        handlerDict.put(NSAppleEventDescriptor.typeTrue, AEConverter::numberWithAEDesc);
        handlerDict.put(NSAppleEventDescriptor.typeFalse, AEConverter::numberWithAEDesc);
        handlerDict.put(NSAppleEventDescriptor.typeSInt16, AEConverter::numberWithAEDesc);
        handlerDict.put(NSAppleEventDescriptor.typeSInt32, AEConverter::numberWithAEDesc);
        handlerDict.put(NSAppleEventDescriptor.typeUInt32, AEConverter::numberWithAEDesc);
        handlerDict.put(NSAppleEventDescriptor.typeSInt64, AEConverter::numberWithAEDesc);
        handlerDict.put(NSAppleEventDescriptor.typeIEEE32BitFloatingPoint, AEConverter::numberWithAEDesc);
        handlerDict.put(NSAppleEventDescriptor.typeIEEE64BitFloatingPoint, AEConverter::numberWithAEDesc);
        handlerDict.put(NSAppleEventDescriptor.type128BitFloatingPoint, AEConverter::numberWithAEDesc);

        // list -> NSArray
        handlerDict.put(NSAppleEventDescriptor.typeAEList, this::arrayWithAEDesc);

        // record -> NSDictionary
        handlerDict.put(NSAppleEventDescriptor.typeAERecord, this::dictionaryWithAEDesc);

        // date -> NSDate
        handlerDict.put(NSAppleEventDescriptor.typeLongDateTime, AEConverter::dateWithAEDesc);

        // images -> NSImage
        handlerDict.put(NSAppleEventDescriptor.typeTIFF, AEConverter::imageWithAEDesc);
        handlerDict.put(NSAppleEventDescriptor.typeJPEG, AEConverter::imageWithAEDesc);
        handlerDict.put(NSAppleEventDescriptor.typeGIF, AEConverter::imageWithAEDesc);
        handlerDict.put(NSAppleEventDescriptor.typePict, AEConverter::imageWithAEDesc);
        handlerDict.put(NSAppleEventDescriptor.typeIconFamily, AEConverter::imageWithAEDesc);
        handlerDict.put(NSAppleEventDescriptor.typeIconAndMask, AEConverter::imageWithAEDesc);

        // vers -> NSString
        handlerDict.put(NSAppleEventDescriptor.typeVersion, AEConverter::versionWithAEDesc);

        // null -> NSNull
        handlerDict.put(NSAppleEventDescriptor.typeNull, AEConverter::nullWithAEDesc);
    }

    /** ae -> java */
    public Object toJava(NSAppleEventDescriptor desc) {
        int type = desc.descriptorType();
        Function<NSAppleEventDescriptor, Object> handlerInvocation = handlerDict.get(type);
        if (handlerInvocation == null) {
Debug.println("no handlar: " + desc.descriptorType());
            return desc.data().getBytes();
        } else {
            return handlerInvocation.apply(desc);
        }
    }

    /** ae -> java */
    private Map<Object, Object> dictionaryWithAEDesc(NSAppleEventDescriptor desc) {
        NSAppleEventDescriptor recDescriptor = desc.coerceToDescriptorType(NSAppleEventDescriptor.typeAERecord);
        Map<Object, Object> resultDict = new HashMap<>();

        // NSAppleEventDescriptor uses 1 indexing
        int recordCount = recDescriptor.numberOfItems();
        for (int recordIndex = 1; recordIndex <= recordCount; recordIndex++) {
            int keyword = recDescriptor.keywordForDescriptorAtIndex(recordIndex);

            if (keyword == NSAppleEventDescriptor.keyASUserRecordFields) {
                NSAppleEventDescriptor listDescriptor = recDescriptor.descriptorAtIndex(recordIndex);

                // NSAppleEventDescriptor uses 1 indexing
                for (int listIndex = 1; listIndex <= listDescriptor.numberOfItems(); listIndex += 2) {
                    Object keyObj = toJava(listDescriptor.descriptorAtIndex(listIndex));
                    Object valObj = toJava(listDescriptor.descriptorAtIndex(listIndex + 1));

                    resultDict.put(valObj, keyObj);
                }
            } else {
                Object keyObj = keyword;
                Object valObj = toJava(recDescriptor.descriptorAtIndex(recordIndex));

                resultDict.put(valObj, keyObj);
            }
        }

        return resultDict;
    }

    /** ae -> java */
    private static Object numberWithAEDesc(NSAppleEventDescriptor desc) {
        int type = desc.descriptorType();

        if ((type == NSAppleEventDescriptor.typeTrue) ||
            (type == NSAppleEventDescriptor.typeFalse) ||
            (type == NSAppleEventDescriptor.typeBoolean)) {
            return desc.booleanValue();
        }

        if (type == NSAppleEventDescriptor.typeSInt16) {
            return (short) desc.int32Value();
        }

        if (type == NSAppleEventDescriptor.typeSInt32) {
            return desc.int32Value();
        }

        if (type == NSAppleEventDescriptor.typeUInt32) {
            return desc.int32Value();
        }

        if (type == NSAppleEventDescriptor.typeIEEE32BitFloatingPoint) {
            return (float) desc.doubleValue();
        }

        if (type == NSAppleEventDescriptor.typeIEEE64BitFloatingPoint) {
            return  desc.doubleValue();
        }

        // try to coerce to 64bit floating point
        desc = desc.coerceToDescriptorType(NSAppleEventDescriptor.typeIEEE64BitFloatingPoint);
        if (desc != null) {
            return desc.doubleValue();
        }

        throw new IllegalArgumentException(String.format(
            "conversion of an NSAppleEventDescriptor with objCType '%s' to an aeDescriptor is not supported.", type));
    }

    /** ae -> java */
    private static BufferedImage imageWithAEDesc(NSAppleEventDescriptor desc) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(desc.data().bytes().getByteArray(0, desc.data().length()));
            return ImageIO.read(bais);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** ae -> java */
    private static long dateWithAEDesc(NSAppleEventDescriptor desc) {
        return (long) desc.dateValue().timeIntervalSince1970() * 1000;
    }

    /** ae -> java */
    private static Object nullWithAEDesc(NSAppleEventDescriptor desc) {
        return null;
    }

    /** ae -> java */
    private static String stringWithAEDesc(NSAppleEventDescriptor desc) {
        return desc.stringValue();
    }

    /** ae -> java */
    private static String versionWithAEDesc(NSAppleEventDescriptor desc) {
        return desc.stringValue(); // TODO
    }

    /** ae -> java */
    private List<Object> arrayWithAEDesc(NSAppleEventDescriptor desc) {
        NSAppleEventDescriptor listDesc = desc.coerceToDescriptorType(NSAppleEventDescriptor.typeAEList);
        List<Object> resultArray = new ArrayList<>();

        // apple event descriptors are 1-indexed
        for (int i = 1; i <= listDesc.numberOfItems(); i++) {
            resultArray.add(toJava(listDesc.descriptorAtIndex(i)));
        }

        return resultArray;
    }

    /** java -> ae */
    @SuppressWarnings("unchecked")
    public NSAppleEventDescriptor toAe(Object self) {
        if (self == null) {
            return NSAppleEventDescriptor.nullDescriptor();
        }

        if (self instanceof String) {
            return NSAppleEventDescriptor.descriptorWithString((String) self);
        }

        if (self instanceof Date) {
            NSDate date = NSDate.from(((Date) self).getTime() / 1000d);
            return NSAppleEventDescriptor.alloc().descriptorWithDate(date);
        }
        if (self instanceof Instant) {
            NSDate date = NSDate.from(((Instant) self).getEpochSecond());
            return NSAppleEventDescriptor.alloc().descriptorWithDate(date);
        }

        if (self instanceof BufferedImage) {
            try {
                BufferedImage image = (BufferedImage) self;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "TIFF", baos);
                NSData data = NSData.dataWithBytes(baos.toByteArray());
                return NSAppleEventDescriptor.descriptorWithDescriptorType_data(NSAppleEventDescriptor.typeTIFF, data);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        if (self instanceof Dimension size) {
            return toAe(Arrays.asList(size.width, size.height));
        }

        if (self instanceof Point point) {
            return toAe(Arrays.asList(point.x, point.y));
        }

        if (self instanceof Rectangle rect) {
            return toAe(Arrays.asList(rect.x, rect.y, rect.width, rect.height));
        }

        // collections go to lists
        if (self.getClass().isArray()) {
            NSAppleEventDescriptor resultDesc = NSAppleEventDescriptor.listDescriptor();
            for (int i = 0; i < Array.getLength(self); i ++) {
                Object e = Array.get(self, i);
                resultDesc.insertDescriptor_atIndex(toAe(e), i++);
            }
        }

        if (self instanceof List) {
            List<NSAppleEventDescriptor> list = (List<NSAppleEventDescriptor>) self;
            NSAppleEventDescriptor resultDesc = NSAppleEventDescriptor.listDescriptor();
            int i = 1;
            for (Object e : list) {
                resultDesc.insertDescriptor_atIndex(toAe(e), i++);
            }
        }

        if (self instanceof Map) {
            return aeDescriptorValue((Map<Object, Object>) self);
        }

        if (self instanceof Number) {
            return aeDescriptorValue((Number) self);
        }

Debug.println("unhandled :" + self.getClass());
        return NSAppleEventDescriptor.descriptorWithString(String.valueOf(self));
    }

    private NSAppleEventDescriptor aeDescriptorValue(Map<Object, Object> self) {
        NSAppleEventDescriptor resultDesc = NSAppleEventDescriptor.recordDescriptor();
        List<Object> userFields = new ArrayList<>();

        for (Map.Entry<Object, Object> e : self.entrySet()) {

            if (e.getKey() instanceof Number) {
                resultDesc.setDescriptor_forKeyword(toAe(e.getValue()), (int) e.getKey());
            } else if (e.getKey() instanceof String) {
                userFields.add(e.getKey());
                userFields.add(e.getValue());
            }
        }

        if (!userFields.isEmpty()) {
            resultDesc.setDescriptor_forKeyword(toAe(userFields), NSAppleEventDescriptor.keyASUserRecordFields);
        }

        return resultDesc;
    }

    private static NSAppleEventDescriptor aeDescriptorValue(Number self) {
        if (self instanceof Integer) {
            return NSAppleEventDescriptor.descriptorWithInt32(self.intValue());
        } else  if (self instanceof Short) {
            return NSAppleEventDescriptor.descriptorWithInt32(self.shortValue() & 0xffff);
        } else if (self instanceof Long) {
            return NSAppleEventDescriptor.alloc().descriptorWithDouble(self.longValue());
        } else if (self instanceof Byte) {
            return NSAppleEventDescriptor.descriptorWithInt32(self.byteValue() & 0xff);
        } else if (self instanceof Float) {
            return NSAppleEventDescriptor.alloc().descriptorWithDouble(self.floatValue());
        } else if (self instanceof Double) {
            return NSAppleEventDescriptor.alloc().descriptorWithDouble(self.doubleValue());
        } else if (self instanceof BigDecimal) {
            return NSAppleEventDescriptor.alloc().descriptorWithDouble(self.doubleValue());
        } else if (self instanceof BigInteger) {
            return NSAppleEventDescriptor.alloc().descriptorWithDouble(self.doubleValue());
        } else {
            throw new IllegalArgumentException(String.format(
                "JavaAppleScriptEngineAdditions: conversion of an NSNumber with objCType '%s' to an aeDescriptor is not supported.", self.getClass()));
        }
    }
}

/* */
