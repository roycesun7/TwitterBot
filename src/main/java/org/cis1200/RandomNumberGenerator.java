package org.cis1200;

import java.util.Random;

/**
 * Produces random numbers using Java's Random class
 */
public class RandomNumberGenerator implements NumberGenerator {

    private Random r;

    /**
     * Generates pseudo-random numbers.
     */
    public RandomNumberGenerator() {
        r = new Random();
    }

    /**
     * Generates numbers randomly based off of a specified seed.
     *
     * @param seed - a seed to pass to the Random number generator.
     */
    public RandomNumberGenerator(long seed) {
        r = new Random(seed);
    }

    /**
     * Grabs another integer from the Random number generator that is between 0
     * and bound (inclusive).
     *
     * @param bound - the max value that can be returned by this call to next
     * @return a random number between 0 and bound (inclusive).
     */
    public int next(int bound) {
        return r.nextInt(bound);
    }

}