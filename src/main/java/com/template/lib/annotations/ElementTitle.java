package com.template.lib.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 * User: Nestor
 * Date: 29.02.16
 * Time: 22:01
 * To change this template use File | Settings | File Templates.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ElementTitle {

    /**
     * Title text
     *
     * @return a {@link java.lang.String} object.
     */
    public String value();
}