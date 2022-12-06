package org.cis1200;

import java.util.*;
import java.util.Map.Entry;

/**
 * This class represents a probability distribution over a type T as a map from
 * T values to integers. We can think of this map as a "histogram" of the
 * frequency of occurrences observed for T values.
 * <p>
 * We can build a probability distribution by "recording" new occurrences of a
 * value. For instance, if we want to build a probability distribution over
 * String values, we might record the following occurrences: record("a");
 * record("b"); record("a"); record("a"); record("c")
 * <p>
 * The resulting distribution would map "a" to a frequency count of 3, and "b"
 * and "c" both to 1, since we recorded only one of each.
 * <p>
 * We can sample (a.k.a. "pick") an element from the probability distribution at
 * random, based on the frequency information in the records.
 * <p>
 * For instance, given the distribution above, we would pick "a" with 3/5
 * probability and each of "b" and "c" with 1/5 probability. Of the 5 total
 * observations, 3 were "a". (Assuming that the number generator is truly random
 * -- a non-random number generator, which is useful for testing, might yield
 * different outcomes.)
 */
class ProbabilityDistribution<T extends Comparable<T>> {

    // association between keys and number of occurrences
    // store in a TreeMap so that the entries can be accessed in sorted order
    // INVARIANT: keys are never null, values are > 0
    private final TreeMap<T, Integer> records;
    // INVARIANT: total is sum of all values stored in records
    private Integer total = 0;

    public ProbabilityDistribution() {
        this.records = new TreeMap<>();
    }

    /**
     * Total number of instances that have been added via record().
     *
     * @return an int representing the number of records in the
     *         ProbabilityDistribution.
     */
    public int getTotal() {
        return total;
    }

    /**
     * @return an EntrySet representation of the internal Map.
     */
    public Set<Entry<T, Integer>> getEntrySet() {
        // Copy constructor so records cannot be modified externally.
        return new TreeSet<>(records.entrySet());
    }

    /**
     * @return a copy of the ProbabilityDistribution's internal Map
     */
    public Map<T, Integer> getRecords() {
        // Copy constructor so records cannot be modified externally.
        return new TreeMap<>(records);
    }

    /**
     * Picks an instance of the ProbabilityDistribution according to the
     * provided NumberGenerator. In the provided NumberGenerator is random, then
     * the resulting T values are chosen with probability proportional to the
     * frequency that they have been recorded.
     *
     * @param generator - uses the generator to pick a particular element in the
     *                  ProbabilityDistribution.
     * @return the chosen element of the ProbabilityDistribution
     * @throws IllegalArgumentException if a number received from the generator
     *                                  is less than zero or greater than the
     *                                  total number of records in the PD
     */
    public T pick(NumberGenerator generator) {
        return this.pick(generator.next(total));
    }

    /**
     * Picks an instance of the ProbabilityDistribution non-randomly according
     * to the provided index.
     *
     * @param index - use this to pick a particular element in the
     *              ProbabilityDistribution. Must not be more than the number of
     *              elements in the ProbabilityDistribution.
     * @return the chosen element of the ProbabilityDistribution
     * @throws IllegalArgumentException if index is less than zero or greater
     *                                  than the total number of records in the
     *                                  PD
     */
    public T pick(int index) {
        if (index >= total || index < 0) {
            throw new IllegalArgumentException(
                    "Index has to be less than or " +
                            "equal to the total " + "number of records in the PD"
            );
        }
        int currentIndex = 0;
        // go through the keys in order, summing their occurrences until we
        // reach the weighted key
        for (Map.Entry<T, Integer> entry : records.entrySet()) {
            T key = entry.getKey();
            int currentCount = entry.getValue();
            if (currentIndex + currentCount > index) {
                return key;
            }
            currentIndex += currentCount;
        }
        throw new IllegalStateException(
                "Error in ProbabilityDistribution. Make " +
                        "sure to only add new " + "records through " + "record()"
        );
    }

    /**
     * Add an instance to the ProbabilityDistribution. If the element already
     * exists in the ProbabilityDistribution, it will increment the number of
     * occurrences of that element.
     *
     * @param t - an element to add to the distribution
     * @throws IllegalArgumentException when t is null
     */
    public void record(T t) {
        records.put(t, records.getOrDefault(t, 0) + 1);
        total++;
    }

    /**
     * Counts the number of occurrences of an element in the
     * ProbabilityDistribution
     *
     * @param t - the element you want to get the count of
     * @return the number of occurrences of the provided element in the
     *         ProbabilityDistribution
     */
    public int count(T t) {
        Integer count = records.get(t);
        return count != null ? count : 0;
    }

    /**
     * @return a set containing all elements in the
     *         ProbabilityDistribution
     */
    public Set<T> keySet() {
        return records.keySet();
    }

    /**
     * Returns the index of the element such that pick(index) will return the
     * element
     *
     * @param element - the element to find the index of
     * @return the index of the specified element
     * @throws IllegalArgumentException if the element is not in the
     *                                  distribution
     */
    public int index(T element) {
        int currentIndex = 0;
        for (Map.Entry<T, Integer> entry : records.entrySet()) {
            T key = entry.getKey();
            int currentCount = entry.getValue();
            if (key.equals(element)) {
                return currentIndex;
            }
            currentIndex += currentCount;
        }
        throw new IllegalArgumentException("element not in the distribution");
    }

    /**
     * Print the probability distribution
     */
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (Entry<T, Integer> r : records.entrySet()) {
            res.append("Frequency of ");
            res.append(r.getKey());
            res.append(": ");
            res.append(r.getValue());
        }
        return res.toString();
    }
}
