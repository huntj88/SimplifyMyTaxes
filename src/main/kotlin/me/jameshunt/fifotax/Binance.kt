package me.jameshunt.fifotax

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class Binance {
    fun parse(): List<Transaction> {
        val file1 = File("/home/jameshunt/Documents/crypto/part-00000-676a462b-302e-4f46-94dd-ed43146f1139-c000.csv")
        val file2 = File("/home/jameshunt/Documents/crypto/part-00000-aaa26b3a-a4a0-43d3-bbf1-470efaf75557-c000.csv")
        val file3 = File("/home/jameshunt/Documents/crypto/part-00000-fe3fa67b-d2a1-4131-ba32-17912d8a5770-c000.csv")
        val rows: List<List<String>> = csvReader().run { readAll(file1) + readAll(file2) + readAll(file3) }
        val transactionRows = rows
            .filter { it[2] in listOf("Buy", "Sell") }
            .map { it.toTransaction() }

        return transactionRows
            .groupBy { it.time }
            .map { (time, rowsForSingleTransaction) ->
                check(rowsForSingleTransaction.size % 2 == 0) {
                    println(rowsForSingleTransaction)
                }

                val had = rowsForSingleTransaction.filter { it.amount < BigDecimal.ZERO }
                val received = rowsForSingleTransaction.filter { it.amount > BigDecimal.ZERO }

                Transaction(
                    time = time,
                    had = had.first().currency,
                    hadAmount = had.sumOf { it.amount }.abs(),
                    received = received.first().currency,
                    receivedAmount = received.sumOf { it.amount }
                )
            }
    }

    private fun List<String>.toTransaction(): BinanceRow {
        val datePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return BinanceRow(
            time = LocalDateTime.parse(this[0], datePattern).toInstant(ZoneOffset.UTC),
            amount = BigDecimal(this[4]),
            currency = Currency.valueOf(this[3])
        )
    }

    private data class BinanceRow(
        val time: Instant,
        val amount: BigDecimal,
        val currency: Currency
    )
}