package utils;

public class StringUtils {

    public static boolean areSimilarSentences(String input, String question) {
        int firstIndex = question.indexOf("<");
        while (firstIndex != -1) {
            int secondIndex = question.indexOf(">") + 1;
            if (input.length() > secondIndex + 1) {
                question = question.substring(0, firstIndex) + question.substring(secondIndex + 1);
                input = input.substring(0, firstIndex) + input.substring(secondIndex + 1);
            } else {
                if (input.length() < firstIndex) {
                    break;
                } else {
                    question = question.substring(0, firstIndex) + question.substring(secondIndex + 1);
                    // Quick hack otherwise it crashes with "aaa bbb"
                    if (input.lastIndexOf(" ") == -1)
                        return false;
                    input = input.substring(0, firstIndex) + input.substring(input.lastIndexOf(" ")).trim();
                }
            }
            firstIndex = question.indexOf("<");
        }
        return checkLevenshteinDistance(input, question);
    }

    // Levenshtein distance to check if two sentences are similar, from Chat GPT
    private static boolean checkLevenshteinDistance(String input, String question) {
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
        return similarity >= 0.8;
    }
}
