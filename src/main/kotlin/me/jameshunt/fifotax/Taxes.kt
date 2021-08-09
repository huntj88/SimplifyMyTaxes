package me.jameshunt.fifotax

import com.github.doyaaaaaken.kotlincsv.client.CsvWriter
import java.io.File
import java.math.BigDecimal
import java.time.Instant
import java.time.format.DateTimeFormatter

class Taxes {
    fun run() {
        val client = ClientFactory().client
        val transactions = CoinbasePro().parse() + Coinbase().parse() + Binance().parse()

        val withUsd = transactions

//            .filter { it.had != Currency.USD && it.received != Currency.USD }.take(4)
//            .filter { it.had == Currency.USD || it.received == Currency.USD }

            .map {
                TransactionWithUSDConversion(
                    time = it.time,
                    had = it.had,
                    hadAmount = it.hadAmount,
                    received = it.received,
                    receivedAmount = it.receivedAmount,
                    usdValueAtTimeOfTrade = when {
                        it.had == Currency.USD -> it.hadAmount
                        it.received == Currency.USD -> it.receivedAmount
                        else -> {

                            val currencyString = when(it.received) {
                                Currency.IOTA -> "MIOTA"
                                else -> it.received.toString()
                            }

                            val priceForOneFullAsset = client
                                .getUSDPriceForCurrencyAtTime(currencyString, it.time.epochSecond)
                                .execute().body()!!.Data.Data.first()
                                .let { (it.open + it.close) / 2 }
                                .toBigDecimal()

                            it.receivedAmount * priceForOneFullAsset
                        }
                    }
                )
            }

        val csvData = withUsd.map {
            val time = DateTimeFormatter.ISO_INSTANT.format(it.time)
            listOf<String>(
                time,
                it.had.toString(), it.hadAmount.toPlainString(),
                it.received.toString(), it.receivedAmount.toPlainString(),
                it.usdValueAtTimeOfTrade.toPlainString()
            )
        }
        val header = listOf(listOf("time", "had", "hadAmount", "received", "receivedAmount", "usdValueAtTimeOfTrade"))
        val rows = header + csvData
        CsvWriter().writeAll(rows, File("transactionsSimplified.csv"))
    }
}

enum class Currency {
    USD,
    ETH,
    BTC,
    LTC,
    XLM,
    ADA,
    IOTA,
    FUN,
    BAT,
    VEN,
    NEO,
    BNB
}

data class Transaction(
    val time: Instant,
    val had: Currency,
    val hadAmount: BigDecimal,
    val received: Currency,
    val receivedAmount: BigDecimal
)

data class TransactionWithUSDConversion(
    val time: Instant,
    val had: Currency,
    val hadAmount: BigDecimal,
    val received: Currency,
    val receivedAmount: BigDecimal,
    val usdValueAtTimeOfTrade: BigDecimal
)