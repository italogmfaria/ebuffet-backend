package com.ebuffet.utils.security;

public class BuffetContext {

    private static final ThreadLocal<Long> BUFFET_ID = new ThreadLocal<>();

    public static void set(Long buffetId) {
        BUFFET_ID.set(buffetId);
    }

    public static Long get() {
        return BUFFET_ID.get();
    }

    public static void clear() {
        BUFFET_ID.remove();
    }
}
