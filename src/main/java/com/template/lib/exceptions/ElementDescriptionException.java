package com.template.lib.exceptions;

/**
 * Created by nestor on 11.07.2017.
 */
public class ElementDescriptionException extends PageException {

    /**
     *
     * @param e a {@link java.lang.Throwable} object.
     */
    public ElementDescriptionException(Throwable e) {
        super(e);
    }

    /**
     *
     * @param message a {@link java.lang.String} object.
     * @param e a {@link java.lang.Throwable} object.
     */
    public ElementDescriptionException(String message, Throwable e) {
        super(message, e);
    }

    /**
     *
     * @param message a {@link java.lang.String} object.
     */
    public ElementDescriptionException(String message) {
        super(message);
    }
}
