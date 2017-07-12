package com.template.lib.exceptions;

/**
 * Created by nestor on 11.07.2017.
 */
public class UnsupportedBrowserException extends Exception {

    /**
     * @param e a {@link java.lang.Throwable} object.
     */
    public UnsupportedBrowserException(Throwable e) {
        super(e);
    }

    /**
     * @param message a {@link java.lang.String} object.
     * @param e       a {@link java.lang.Throwable} object.
     */
    public UnsupportedBrowserException(String message, Throwable e) {
        super(message, e);
    }

    /**
     * @param message a {@link java.lang.String} object.
     */
    public UnsupportedBrowserException(String message) {
        super(message);
    }
}
