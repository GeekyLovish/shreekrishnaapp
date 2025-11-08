package com.raman.kumar;

import java.util.concurrent.atomic.AtomicInteger;

public class NotificationId {
    private final static AtomicInteger counter = new AtomicInteger(0);

    public static int getID() {
        return counter.incrementAndGet();  // Always gives a unique ID like 1, 2, 3...
    }
}
