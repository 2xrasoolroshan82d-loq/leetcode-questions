class Solution {
    public boolean isPalindrome(int x) {
        if(x < 0) return false;
        int num = 0;
        int og = x;
        while(x > 0)
        {
            int rem = x % 10;
            num = (num * 10) + rem;
            x /= 10;
        }
        if(og == num) return true;
        return false;
    }
}
