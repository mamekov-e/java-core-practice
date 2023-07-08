package org.example;

import java.util.Arrays;
import java.util.concurrent.Semaphore;

/*
    The example of horse riders waiting in control places
*/
public class SemaphorePractice {
    private static final int COUNT_CONTROL_PLACES = 5;
    private static final int COUNT_RIDERS = 10;
    private static Semaphore semaphore;
    private static boolean[] CONTROL_PLACES;

    public static void main(String[] args) throws InterruptedException {
        CONTROL_PLACES = new boolean[COUNT_CONTROL_PLACES];

        // init control places as free
        Arrays.fill(CONTROL_PLACES, true);

        // define semaphore with parameters
        // *permits = control places count
        // *fair = true (means taking threads will be in fifo order)
        semaphore = new Semaphore(COUNT_CONTROL_PLACES, true);

        // put Riders to queue for checking in control place
        for (int i = 1; i <= COUNT_RIDERS; i++) {
            Thread rider = new Thread(new Rider(i));
            rider.start();
            // wait
            Thread.sleep(400);
        }
    }

    static class Rider implements Runnable {
        private int riderNum;

        Rider(int riderNum) {
            this.riderNum = riderNum;
        }

        @Override
        public void run() {
            System.out.printf("Rider #%s came to control place\n", riderNum);

            try {
                // get access to control place
                semaphore.acquire();

                // rider checks if free place exist
                int controlPlaceId = -1;
                synchronized (CONTROL_PLACES) {
                    for (int i = 0; i < COUNT_CONTROL_PLACES; i++) {
                        if (CONTROL_PLACES[i]) {
                            CONTROL_PLACES[i] = false;
                            controlPlaceId = i;
                            System.out.printf("Control places taken by rider #%s is %d\n", riderNum, i);
                            break;
                        }
                    }
                }
                Thread.sleep((int) (Math.random() * 10) * 1000);

                // release control place
                synchronized (CONTROL_PLACES) {
                    CONTROL_PLACES[controlPlaceId] = true;
                }

                // release control place resource
                semaphore.release();
                System.out.printf("Checking of rider $%d is finished\n", riderNum);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
