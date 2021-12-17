package com.hongcha.remote.common.util;


public interface Assert {


    static void notNull(Object object, String msg) {
        isTrue(object != null, msg);
    }

    static void hasText(String str, String msg) {
        isTrue((str != null && !str.trim().isEmpty()), msg);
    }


    static void isTrue(boolean bool, String msg) {
        if (!bool) {
            throwError(msg);
        }
    }


    static void isFalse(boolean bool, String msg) {
        if (bool) {
            throwError(msg);
        }
    }


    static void throwError(String msg) {
        throw new IllegalStateException(msg);
    }

}

