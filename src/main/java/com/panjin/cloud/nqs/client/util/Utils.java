/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.cloud.nqs.client.util;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class.
 *
 * @author panjin
 * @version $Id: Utils.java 2016年7月19日 下午6:29:27 $
 */
public class Utils {
    /**
     * Logger for this class.
     */
    private static Logger logger = LoggerFactory.getLogger(Utils.class);

    /**
     * Constructor, do nothing.
     */
    private Utils() {
        // do nothing
    }

    /**
     * Check whether the object is null.
     * 
     * @param para
     *            the object to be checked
     * @param name
     *            the object name
     */
    public static void checkNotNull(Object para, String name) {
        if (para == null) {
            throw new IllegalArgumentException(name + " shouldn't be null");
        }
    }

    /**
     * Check whether the object is empty.
     * 
     * @param para
     *            the object to be checked
     * @param name
     *            the object name
     */
    public static void checkNotEmpty(String para, String name) {
        checkNotNull(para, name);

        if (para.trim().length() == 0) {
            throw new IllegalArgumentException(name + " shouldn't empty");
        }
    }

    /**
     * Get the private field with reflection.
     * 
     * @param instance
     *            the object instance
     * @param fieldName
     *            the name of the field
     * @return the filed object
     */
    public static Object getPrivateFieldValue(Object instance, String fieldName) {
        try {
            Field field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(instance);
        } catch (SecurityException e) {
            logger.error("getPrivateFieldValue exception", e);
        } catch (NoSuchFieldException e) {
            logger.error("getPrivateFieldValue exception", e);
        } catch (IllegalArgumentException e) {
            logger.error("getPrivateFieldValue exception", e);
        } catch (IllegalAccessException e) {
            logger.error("getPrivateFieldValue exception", e);
        }

        return null;
    }
}
