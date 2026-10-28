package com.example.autoexpense.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AlipayBurstAccumulatorTest {
    @Test
    fun separateAlipayNotificationsFormOneTotal() {
        val burst = AlipayBurstAccumulator()
        assertTrue(burst.add(14.30, "支付宝 14.30", "notification-1", 1000))
        assertFalse(burst.add(33.26, "支付宝 33.26", "notification-2", 1500))
        val result = burst.drain()!!
        assertEquals(47.56, result.amount, 0.001)
        assertEquals(2, result.count)
    }

    @Test
    fun accessibilityAndNotificationCopiesAreCountedOnce() {
        val burst = AlipayBurstAccumulator()
        assertTrue(burst.add(14.30, "无障碍提醒", null, 1000))
        assertFalse(burst.add(14.30, "通知提醒", "notification-1", 1100))
        assertFalse(burst.add(14.30, "重复通知", "notification-1", 1200))
        assertEquals(1, burst.drain()!!.count)
    }

    @Test
    fun distinctNotificationsWithSameAmountAreNotCollapsed() {
        val burst = AlipayBurstAccumulator()
        assertTrue(burst.add(10.0, "第一笔", "notification-1", 1000))
        assertFalse(burst.add(10.0, "第二笔", "notification-2", 1500))
        assertEquals(20.0, burst.drain()!!.amount, 0.001)
    }
}
