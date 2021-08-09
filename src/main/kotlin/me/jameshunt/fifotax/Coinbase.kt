package me.jameshunt.fifotax

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File
import java.math.BigDecimal
import java.time.Instant

class Coinbase {

    fun parse(): List<Transaction> {
        val file = File("/home/jameshunt/Documents/crypto/Coinbase_without_header.csv")
        val rows: List<List<String>> = csvReader().readAll(file).let { it.subList(1, it.size) }

        return rows
            .filter { it[1] in listOf("Buy", "Sell") }
            .map {
                val time = Instant.parse(it[0])
                val assetPurchasedOrSold = Currency.valueOf(it[2])
                val amountOfAsset = BigDecimal(it[3])
                val amountOfUSD = BigDecimal(it[5])

                when (it[1]) {
                    "Buy" -> Transaction(
                        time = time,
                        had = Currency.USD,
                        hadAmount = amountOfUSD,
                        received = assetPurchasedOrSold,
                        receivedAmount = amountOfAsset
                    )
                    "Sell" -> Transaction(
                        time = time,
                        had = assetPurchasedOrSold,
                        hadAmount = amountOfAsset,
                        received = Currency.USD,
                        receivedAmount = amountOfUSD
                    )
                    else -> throw IllegalStateException()
                }
            }
    }
}