package org.mjc.utils;

import java.lang.reflect.Field;

//静态于对象无关
public class ColunmTypeUtils {
    public static Field getColumnType(Class cls,String name) throws NoSuchFieldException {
        Field fd = cls.getDeclaredField(name);
        return fd;

    }
}
