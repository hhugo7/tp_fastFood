package com.foodfast.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FoodFastUtilsTest {

    @Test
    void testDeliveryPlanner()
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
        Assertions.assertFalse(FoodFastUtils.isLeapYear(2025));
    }

    @Test
    void reverseString()
    {
        Assertions.assertEquals("CBA", FoodFastUtils.anonymize("ABC"));
        Assertions.assertEquals("selliuoC seM", FoodFastUtils.anonymize("Mes Couilles"));
    }

    @Test
    void supUpTo()
    {
        Assertions.assertEquals(15, FoodFastUtils.sumUpTo(5));
        Assertions.assertNotEquals(18, FoodFastUtils.sumUpTo(5));
        Assertions.assertEquals(105, FoodFastUtils.sumUpTo(14));
    }

}
