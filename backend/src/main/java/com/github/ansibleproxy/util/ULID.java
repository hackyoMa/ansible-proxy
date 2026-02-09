package com.github.ansibleproxy.util;

/**
 * ULID
 *
 * @author hackyo
 * @since 1.0.0
 */
public final class ULID {

    private static final de.huxhorn.sulky.ulid.ULID ULID = new de.huxhorn.sulky.ulid.ULID();

    public static String randomULID() {
        return ULID.nextULID();
    }

}
