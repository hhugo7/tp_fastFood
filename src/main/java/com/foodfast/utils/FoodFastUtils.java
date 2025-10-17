package com.foodfast.utils;

public class FoodFastUtils {

    public static String deliveryPlanner(int n) {
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

    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    public static String anonymize(String text) {
        String reverseString = "";

        for (int i = 0; i < text.length(); i++) {
            reverseString = reverseString + text.charAt(i);
        }
        return reverseString;
    }
}
