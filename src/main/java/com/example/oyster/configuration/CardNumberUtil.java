package com.example.oyster.configuration;

public class CardNumberUtil {

    private static final long CARD_NUMBER_BASE = 100000000000L;
    private static final long CARD_NUMBER_RANGE = 900000000000L;

    public static Long generateUniqueCardNumber() {
        return CARD_NUMBER_BASE + (long) (Math.random() * CARD_NUMBER_RANGE);
    }
}