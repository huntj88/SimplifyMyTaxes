package me.jameshunt.fifotax

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ImportTest {

    @Test
    fun parseCoinbasePro() {
        CoinbasePro().parse().forEach {
            println(it)
        }
    }

    @Test
    fun parseCoinbase() {
        Coinbase().parse().forEach {
            println(it)
        }
    }

    @Test
    fun parseBinance() {
        Binance().parse().forEach {
            println(it)
        }
    }
}