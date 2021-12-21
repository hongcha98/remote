package com.hongcha.remote.common.util;

public class ExceptionUtils {
    public static StackTraceElement[] getAllStackTraceElements(Throwable e) {
        StackTraceElement[] clone = e.getStackTrace().clone();
        if (e.getCause() != null) {
            StackTraceElement[] causeStackTraceElements = getAllStackTraceElements(e.getCause());
            StackTraceElement[] temp = clone;
            clone = new StackTraceElement[clone.length + causeStackTraceElements.length];
            for (int i = 0; i < temp.length; i++) {
                clone[i] = temp[i];
            }
            for (int i = temp.length; i < clone.length; i++) {
                clone[i] = causeStackTraceElements[i - temp.length];
            }
        }
        return clone;
    }

}
