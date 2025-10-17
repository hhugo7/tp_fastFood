package com.foodfast.utils;

public class FoodFastUtils {

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
}
