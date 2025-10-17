package com.foodfast.utils;

public class FoodFastUtils {

    /**
     * Constructor
     */
    public FoodFastUtils() {
        return;
    }

    /**
     * FizzBuzz function
     * @param n input number
     * @return fizz, buzz or fizzbuzz
     */
    public static String deliveryPlanner(int n){
        String returnString = "";

        int modulo3 = n % 3;
        if (modulo3 == 0) {
            returnString += "Fizz";
        }

        int modulo5 = n % 5;
        if (modulo5 == 0) {
            returnString += "Buzz";
        }

        return returnString;
    }

    /**
     * Returns if a year is a leap year or not
     * @param year year
     * @return leap year
     */
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    /**
     * Reverses a string
     * @param text string to reverse
     * @return reversed string
     */
    public static String anonymize(String text) {
        String reverseString = "";

        for (int i = 0; i < text.length(); i++) {
            reverseString = reverseString + text.charAt(i);
        }
        return reverseString;
    }

    /**
     * Returns the sum up to a number
     * @param n number to sum up to
     * @return sum
     */
    public static int sumUpTo(int n) {

        int result = 0;

        for (int i = 0; i <= n; i++) {
            result += i;
        }

        return result;
    }
}
