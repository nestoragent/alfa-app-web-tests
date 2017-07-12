package com.template.lib.support;

import org.apache.commons.lang3.ClassUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nestor on 11.07.2017.
 */
public class ClassUtilsExt extends ClassUtils {
    public ClassUtilsExt() {
    }

    public static List<Class> getSuperclassesWithInheritance(Class clazz) {
        ArrayList classes = new ArrayList();

        for(Class supp = clazz.getSuperclass(); supp != Object.class; supp = supp.getSuperclass()) {
            classes.add(supp);
        }

        return classes;
    }
}
