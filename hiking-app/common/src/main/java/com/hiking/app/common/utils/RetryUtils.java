package com.hiking.app.common.utils;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RetryUtils {

    public static <T> T retryWhenNull(RetryContent<T> handler, long timeoutMills, long intervalMills) {
        while (timeoutMills > 0) {
            T result = handler.retry();
            if (result != null) {
                return result;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(intervalMills);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timeoutMills -= intervalMills;

        }
        return null;

    }

    public static <T> T retryWhenRuntimeException(RetryContent<T> handler, long timeoutMills, long intervalMills) {
        while (timeoutMills > 0) {
            try {
                return handler.retry();
            } catch (RuntimeException e1) {
                e1.printStackTrace();
                try {
                    TimeUnit.MILLISECONDS.sleep(intervalMills);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
                timeoutMills -= intervalMills;
            } catch (Exception e3) {
                return null;
            }
        }
        return null;

    }


    public interface RetryContent<T> {
        RetryContent<Integer> DEFAULT = () -> {
            int i = new Random().nextInt();
            System.out.println(i);
            return i > 10 ? i : null;
        };

        T retry();
    }


    public static void main(String[] args) {
        Integer random = RetryUtils.retryWhenNull(RetryContent.DEFAULT, 5000, 500);
        System.out.println("result is " + random);
    }
}
