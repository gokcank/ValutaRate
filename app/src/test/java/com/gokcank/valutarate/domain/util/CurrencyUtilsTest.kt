package com.gokcank.valutarate.domain.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class CurrencyUtilsTest {

    @Test
    fun getPopularityIndex_returnsCorrectPriority() {
        assertEquals(0, CurrencyUtils.getPopularityIndex("USD"))
        assertEquals(1, CurrencyUtils.getPopularityIndex("EUR"))
        assertEquals(2, CurrencyUtils.getPopularityIndex("GBP"))
        assertEquals(Int.MAX_VALUE, CurrencyUtils.getPopularityIndex("UNKNOWN"))
    }

    @Test
    fun getCurrencySymbol_returnsExpectedSymbols() {
        assertEquals("$", CurrencyUtils.getCurrencySymbol("USD"))
        assertEquals("€", CurrencyUtils.getCurrencySymbol("EUR"))
        assertEquals("₺", CurrencyUtils.getCurrencySymbol("TRY"))
        assertEquals("£", CurrencyUtils.getCurrencySymbol("GBP"))
    }

    @Test
    fun getCurrencyFlag_returnsExpectedFlags() {
        assertEquals("🇺🇸", CurrencyUtils.getCurrencyFlag("USD"))
        assertEquals("🇪🇺", CurrencyUtils.getCurrencyFlag("EUR"))
        assertEquals("🇹🇷", CurrencyUtils.getCurrencyFlag("TRY"))
    }

    @Test
    fun getNextTcmbUpdateTimeMillis_returnsValidFutureTimestamp() {
        val nextUpdateTime = CurrencyUtils.getNextTcmbUpdateTimeMillis()
        assertNotNull(nextUpdateTime)
    }
}
