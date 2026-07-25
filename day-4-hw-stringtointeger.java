class Solution {
    public int myAtoi(String s) {
        long ans = 0;
        if (s == null || s.length() == 0)
            return 0;
        int i = 0;
        int n = s.length();
        while (i < n && s.charAt(i) == ' ' )
            i += 1;
        if (i == n)
            return 0;
        int k = 1;
        if (s.charAt(i) == '+')
            i += 1;
        else if (s.charAt(i) == '-') {
            k = -1;
            i += 1;
        }
        while (i < n && Character.isDigit(s.charAt(i))) {
            int c = s.charAt(i) - '0';
            ans = ans * 10 + c;
            if (k * ans >= Integer.MAX_VALUE)
                return Integer.MAX_VALUE;
            if (k * ans <= Integer.MIN_VALUE)
                return Integer.MIN_VALUE;
            i += 1;
        }
        return (int)ans * k;
    }
}
