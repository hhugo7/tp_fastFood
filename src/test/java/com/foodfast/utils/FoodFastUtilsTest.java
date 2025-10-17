package com.foodfast.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FoodFastUtilsTest {

    @Test
    void test()
    {
        Assertions.assertEquals("Fizz", FoodFastUtils.deliveryPlanner(3));
        Assertions.assertEquals("", FoodFastUtils.deliveryPlanner(7));
        Assertions.assertEquals("Buzz", FoodFastUtils.deliveryPlanner(5));
        Assertions.assertEquals("FizzBuzz", FoodFastUtils.deliveryPlanner(15));
    }

    @Test
    void testLeapYear()
    {
        Assertions.assertTrue(FoodFastUtils.isLeapYear(2024));
    }

    @Test
    void reverseString()
    {
        Assertions.assertEquals("CBA", FoodFastUtils.anonymize("ABC"));
    }

}
