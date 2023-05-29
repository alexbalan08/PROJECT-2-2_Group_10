package utils;

public class StringUtils {

    /**
     *
     * Checks if two input strings are similar sentences by comparing them without slots.
     * It will remove the slots in the question and in the input, then check with the Levenshtein Distance if they are similar.
     *
     * */
    public static boolean areSimilarSentences(String input, String question, double percentage) {
        input = input.replace(" ", "");
        question = question.replace(" ", "");
        int firstIndex = question.indexOf("<");
        while (firstIndex != -1) {
            int secondIndex = question.indexOf(">") + 1;
            if (input.length() <= secondIndex + 1) {
                if (input.length() < firstIndex || input.lastIndexOf(" ") == -1) {
                    return false;
                } else {
                    input = input.substring(0, firstIndex) + input.substring(input.lastIndexOf(" ")).trim();
                }
            } else {
                input = removeSlot(input, question, firstIndex, secondIndex);
                if(input.equals("")) {
                    return false;
                }
            }
            question = question.substring(0, firstIndex) + question.substring(secondIndex + 1);
            firstIndex = question.indexOf("<");
        }
        return checkLevenshteinDistance(input, question, percentage);
    }

    /**
     *
     * Removes a certain slot value from the input string based on its position within the question string.
     *
     * First extracts a substring from question starting from the character after the secondIndex position until the first space character encountered.
     * This substring represents the slot value that needs to be removed from input.
     *
     * If the slot value is found within the input string after the firstIndex position, the method removes the slot
     * value and any characters before it from the input string and returns the modified input.
     *
     * If the slot value is not found in the input string, the method returns an empty string.
     *
     * If the slot value is the last word in the question string, the method removes it from the input string and
     * returns the modified input.
     *
     * */
    private static String removeSlot(String input, String question, int firstIndex, int secondIndex) {
        String remain = question.substring(secondIndex + 1);
        if (remain.contains(" ")) {
            String nextWord = remain.substring(0, remain.indexOf(" "));
            String sub = input.substring(firstIndex);
            if (sub.contains(nextWord)) {
                input = input.substring(0, firstIndex) + sub.substring(sub.indexOf(nextWord));
            } else {
                return "";
            }
        } else {
            input = input.substring(0, firstIndex) + remain;
        }
        return input;
    }


    /**
     *
     * This method checks the Levenshtein distance between two strings to determine their similarity.
     * The Levenshtein distance is the minimum number of insertions, deletions, or substitutions required to transform one string into another.
     *
     * The method initializes a matrix of size n+1 by m+1, where n is the length of the first string and m is the length of the second string.
     * The elements in the matrix represent the Levenshtein distance between the prefixes of the two strings.
     * The method then fills in the matrix by iterating over the strings and computing the cost of each operation, which is 0 if the characters at the corresponding positions are the same and 1 otherwise.
     * Finally, the method computes the similarity between the two strings as (maxLength - distance) / maxLength, where maxLength is the length of the longer string, and returns true if the similarity is greater than or equal to 0.8, indicating that the two strings are similar enough.
     *
     * */
    private static boolean checkLevenshteinDistance(String input, String question, double percentage) {
        int n = input.length();
        int m = question.length();
        int[][] dp = new int[n + 1][m + 1];

        if (n == 0 || m == 0) {
            return false;
        }

        for (int i = 1; i <= n; i++) {
            dp[i][0] = i;
        }

        for (int j = 1; j <= m; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = input.charAt(i - 1) == question.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }

        int distance = dp[n][m];
        int maxLength = Math.max(n, m);
        double similarity = (double) (maxLength - distance) / maxLength;
        return similarity >= percentage;
    }
}

