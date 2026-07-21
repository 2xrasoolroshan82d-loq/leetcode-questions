class Solution {
    public boolean isPalindrome(String s) {
        int left = 0;
        int right = s.length()-1;
        while(left < right){

            //skip space from the left
            if(!Character.isLetterOrDigit(s.charAt(left))){
                left++;
                continue;
            }
            //skip space from the right
            if(!Character.isLetterOrDigit(s.charAt(right))){
                right--;
                continue;
            }

            //compare both character in lowercase
            if(Character.toLowerCase(s.charAt(left))!= Character.toLowerCase(s.charAt(right))){
                return false;
            }

            left++;
            right--;
        }
        return true;
    }
}
