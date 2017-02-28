//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package matcher;

public class EditDistance {
    public EditDistance() {
    }

    public static int getEditDistance(String s, String t) {
        int n = s.length();
        int m = t.length();
        if(n == 0) {
            return m;
        } else if(m == 0) {
            return n;
        } else {
            int[][] d = new int[n + 1][m + 1];

            int i;
            for(i = 0; i <= n; d[i][0] = i++) {
                ;
            }

            int j;
            for(j = 0; j <= m; d[0][j] = j++) {
                ;
            }

            for(i = 1; i <= n; ++i) {
                char s_i = s.charAt(i - 1);

                for(j = 1; j <= m; ++j) {
                    char t_j = t.charAt(j - 1);
                    byte cost;
                    if(s_i == t_j) {
                        cost = 0;
                    } else {
                        cost = 1;
                    }

                    d[i][j] = Minimum(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost);
                }
            }

            return d[n][m];
        }
    }

    private static int Minimum(int a, int b, int c) {
        int mi = a;
        if(b < a) {
            mi = b;
        }

        if(c < mi) {
            mi = c;
        }

        return mi;
    }

    public static double getSimilarity(String s, String t) {
        int edit = getEditDistance(s, t);
        double sim = 1.0D / Math.exp((double)edit / (double)(s.length() + t.length() - edit));
        return sim;
    }
}
