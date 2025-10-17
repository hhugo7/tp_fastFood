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
