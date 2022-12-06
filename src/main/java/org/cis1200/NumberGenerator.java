package org.cis1200;

/**
 * This simple interface produces numbers.
 * <p>
 * It is used in MarkovChain and ProbabilityDistribution with just
 * two implementations: RandomNumberGenerator and ListNumberGenerator,
 * the first of which is useful for producing random numbers and thus
 * is good for the bot in practice, the latter of which can be passed
 * a list for deterministic testing of the bot.
 */
public interface NumberGenerator {
    /**
     * Generates a number based of off a specified bound.
     *
     * @param bound - the max value that can be returned by the generator.
     * @return a number based off of the bound.
     */
    int next(int bound);
}
