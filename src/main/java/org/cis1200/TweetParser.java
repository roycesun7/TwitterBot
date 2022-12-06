package org.cis1200;

import java.util.LinkedList;
import java.util.List;
import java.io.BufferedReader;

/**
 * TweetParser.csvDataToTrainingData() takes in a buffered reader that contains
 * tweets and iterates through the reader, one tweet at a time, removing parts
 * of the tweets that would be bad inputs to MarkovChain (for example, a URL).
 * It then parses tweets into sentences and returns those sentences as lists
 * of cleaned-up words.
 * <p>
 * Note: TweetParser's public methods are csvDataToTrainingData() and
 * getPunctuation(). These are the only methods that other classes should call.
 * <p>
 * All the other methods provided are helper methods that build up the code
 * you'll need to write those public methods. They have "package" (default, no
 * modifier) visibility, which lets us write test cases for them as long as
 * those test cases are in the same package.
 */
public class TweetParser {

    /**
     * Regular Expressions
     * <p>
     * For the purposes of this project, we consider "word characters" to be
     * alphanumeric characters [a-zA-Z0-9] and apostrophes [']. A word is "bad"
     * if it contains some other character. (In particular, twitter mentions
     * like "@user" are "bad".)
     * <p>
     * The regular expression BAD_WORD_REGEX expresses those constraints -- any
     * String that matches it is considered "bad" and will be removed from the
     * training data.
     * <p>
     * The regular expression {@code "[\\W&&[^']]"} matches non-word characters.
     * The regular expression ".*" matches _any_ sequence of characters. When
     * concatenated into the full regular expression, they match any sequence of
     * characters followed by a non-word character followed again by any
     * sequence of characters, or, any string containing a non-word character.
     * <p>
     * Similarly, the URL_REGEX matches any substring that starts a word with
     * "http" and continues until some whitespace occurs. See the removeURLs
     * static method.
     * <p>
     * See https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern
     * .html for more details about Java's regular expressions.
     * <p>
     * tldr: use word.matches(BAD_WORD_REGEX) to determine if word is a bad
     * String.
     */
    private static final String BAD_WORD_REGEX = ".*[\\W&&[^']].*";
    private static final String URL_REGEX = "\\bhttp\\S*";
    private static final String URL_REGEX_END_SPACE = "\\bhttp\\S*\\.\\s";
    private static final String URL_REGEX_END_STRING = "\\bhttp\\S*\\.$";

    /**
     * Valid punctuation marks.
     */
    private static final char[] PUNCTUATION = new char[] { '.', '?', '!', ';' };

    /**
     * <p>
     * The clone() function helps us clone the array and return the cloned version
     *
     * @return an array containing the punctuation marks used by the parser.
     */
    public static char[] getPunctuation() {
        return PUNCTUATION.clone();
    }

    /**
     * Do not modify this method.
     * <p>
     * Given a string, replaces all the punctuation with periods.
     * <p>
     * The replace() function returns a string where all instances of a character
     * are replaced with another character of your choice
     *
     * @param tweet - a String representing a tweet
     * @return A String with all the punctuation replaced with periods
     */
    static String replacePunctuation(String tweet) {
        for (char c : PUNCTUATION) {
            tweet = tweet.replace(c, '.');
        }
        return tweet;
    }

    /**
     * Do not modify this method.
     * <p>
     * Given a tweet, splits the tweet into sentences (without end punctuation)
     * and inserts each sentence into a list.
     * <p>
     * Use this as a helper function for parseAndCleanTweet().
     * <p>
     * The trim() function returns a string where the leading and trailing
     * spaces are removed. The split() function breaks apart a string according
     * to the given regular expression and returns a string array of these
     * splits.
     *
     * @param tweet - a String representing a tweet
     * @return A List of Strings where each String is a (non-empty) sentence
     *         from the tweet
     */
    static List<String> tweetSplit(String tweet) {
        List<String> sentences = new LinkedList<>();
        for (String sentence : replacePunctuation(tweet).split("\\.")) {
            sentence = sentence.trim();
            if (!sentence.equals("")) {
                sentences.add(sentence);
            }
        }
        return sentences;
    }

    /**
     * Given a String that represents a CSV line extracted from a reader and an
     * int that represents the column of the String that we want
     * to extract from, return the contents of that column from the String.
     * Columns in the buffered reader are zero indexed.
     * <p>
     * You may find the String.split() method useful here. Your solution should
     * be relatively short.
     * <p>
     * You may assume that the column contents themselves don't have any
     * commas.
     *
     * @param csvLine   - a line extracted from a buffered reader
     * @param csvColumn - the column of the CSV line whose contents ought to be
     *                  returned
     * @return the portion of csvLine corresponding to the column of csvColumn.
     *         If the csvLine is null or has no appropriate csvColumn, return null
     */
    static String extractColumn(String csvLine, int csvColumn) {
        String[] sentences = csvLine.split(",", -1);
        if (csvLine == null || csvColumn < 0 || csvColumn >= sentences.length) {
            return null;
        }
        return sentences[csvColumn];

    }

    /**
     * Given a buffered reader and the column that the tweets are in,
     * use the extractColumn and a FileLineIterator to extract every tweet from
     * the reader. (Recall that extractColumn returns null if there is no data
     * at that column.) You should skip lines in the reader for which the
     * tweetColumn is out of bounds.
     *
     * @param br          - a BufferedReader that represents tweets
     * @param tweetColumn - the number of the column in the buffered reader
     *                    that contains the tweet
     * @return a List of tweet Strings, none of which are null (but that are not
     *         yet cleaned)
     */
    static List<String> csvDataToTweets(BufferedReader br, int tweetColumn) {
        FileLineIterator f = new FileLineIterator(br);
        List<String> lst = new LinkedList<String>();

        while (f.hasNext()) {
            String l = f.next();
            int size = (l.split(",", -1)).length;
            if (tweetColumn >= 0 && size > tweetColumn) {
                if (extractColumn(l, tweetColumn) != null) {
                    lst.add(extractColumn(l, tweetColumn));
                }
            }
        }
        return lst;
    }

    /**
     * Do not modify this method.
     * <p>
     * Cleans a word by removing leading and trailing whitespace and converting
     * it to lower case. If the word matches the BAD_WORD_REGEX or is the empty
     * String, returns null instead.
     *
     * @param word - a (non-null) String to clean
     * @return - a trimmed, lowercase version of the word if it contains no
     *         illegal characters and is not empty, and null otherwise.
     */
    static String cleanWord(String word) {
        String cleaned = word.trim().toLowerCase();
        if (cleaned.matches(BAD_WORD_REGEX) || cleaned.isEmpty()) {
            return null;
        }
        return cleaned;
    }

    /**
     * Splits a String representing a sentence into a sequence of words,
     * filtering out any "bad" words from the sentence.
     * <p>
     * Hint: use the String split method and the cleanWord helper defined above.
     * You should be splitting on one space of whitespace since words are
     * delimited by spaces.
     *
     * @param sentence - a (non-null) String representing one sentence with no
     *                 end punctuation from a tweet
     * @return a (non-null) list of clean words in the order they appear in the
     *         sentence. Any "bad" words are just dropped.
     */
    static List<String> parseAndCleanSentence(String sentence) {
        List<String> wList = new LinkedList<String>();
        String[] nonClean = sentence.split(" ", -1);
        for (int i = 0; i < nonClean.length; i++) {
            if (cleanWord(nonClean[i]) != null) {
                wList.add(cleanWord(nonClean[i]));
            }
        }
        return wList;
    }

    /**
     * Do not modify this method
     * <p>
     * Given a String, remove all substrings that look like a URL. Any word that
     * begins with the character sequence 'http' is simply replaced with the
     * empty string.
     *
     * @param s - a String from which URL-like words should be removed
     * @return s where each "URL-like" string has been deleted
     */
    static String removeURLs(String s) {
        s = s.replaceAll(URL_REGEX_END_STRING, ".");
        s = s.replaceAll(URL_REGEX_END_SPACE, ". ");
        return s.replaceAll(URL_REGEX, "");
    }

    /**
     * Processes a tweet in to a list of sentences, where each sentence is
     * itself a (non-empty) list of cleaned words. Before breaking up the tweet
     * into sentences, this method uses removeURLs to sanitize the tweet.
     * <p>
     * Hint: use removeURLs followed by tweetSplit and parseAndCleanSentence
     *
     * @param tweet - a String that will be split into sentences, each of which
     *              is cleaned as described above (assumed to be non-null)
     * @return a (non-null) list of sentences, each of which is a (non-empty)
     *         sequence of clean words drawn from the tweet.
     */
    static List<List<String>> parseAndCleanTweet(String tweet) {

        String cleanedTweet = removeURLs(tweet);
        List<List<String>> s = new LinkedList<List<String>>();
        List<String> sentences = tweetSplit(cleanedTweet);

        if(!sentences.isEmpty()) {
            for (int i = 0; i < sentences.size(); i++) {
                if (!(parseAndCleanSentence(sentences.get(i)).isEmpty())) {
                    List<String> words = parseAndCleanSentence(sentences.get(i));
                    s.add(words);
                }
            }
        }
        return s;
    }

    /**
     * Given a buffered reader and the column from which to extract the tweet
     * data, computes a training set. The training set is a list of sentences,
     * each of which is a list of words. The sentences have been cleaned up by
     * removing URLs and non-word characters, putting all words into lower case,
     * and stripping out punctuation. Note that empty sentences are not added to
     * the final list of training data examples.
     *
     * @param br          - a BufferedReader that contains the tweets
     * @param tweetColumn - the number of the column in the buffered reader that
     *                    contains the tweet
     * @return a list of training data examples
     */
    public static List<List<String>> csvDataToTrainingData(
            BufferedReader br,
            int tweetColumn
    ) {
        List<List<String>> sentenceList = new LinkedList<List<String>>();
        List<String> raw = csvDataToTweets(br, tweetColumn);

        for (int i = 0; i < raw.size(); i++) {
            sentenceList.addAll(parseAndCleanTweet(raw.get(i)));
        }

        return sentenceList;
    }

}
