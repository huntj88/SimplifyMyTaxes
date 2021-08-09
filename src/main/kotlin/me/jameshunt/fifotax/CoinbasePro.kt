package me.jameshunt.fifotax

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File
import java.math.BigDecimal
import java.time.Instant

class CoinbasePro {
    fun parse(): List<Transaction> {
        val file = File("/home/jameshunt/Documents/crypto/coinbase_pro_account.csv")
        val rows: List<List<String>> = csvReader().readAll(file)
        val transactionRows = rows
            .filter { it[1] == "match" }
            .map { it.toTransaction() }

        return transactionRows
            .groupBy { it.time }
            .also { check(it.size == transactionRows.size / 2) }
            .map { (time, rowsForSingleTransaction) ->
                check(rowsForSingleTransaction.size == 2)

                val had = rowsForSingleTransaction.first { it.amount < BigDecimal.ZERO }
                val received = rowsForSingleTransaction.first { it.amount > BigDecimal.ZERO }

                Transaction(
                    time = time,
                    had = had.currency,
                    hadAmount = had.amount.abs(),
                    received = received.currency,
                    receivedAmount = received.amount
                )
            }
    }

    private fun List<String>.toTransaction(): CoinbaseRow {
        return CoinbaseRow(
            time = Instant.parse(this[2]),
            amount = BigDecimal(this[3]),
            currency = Currency.valueOf(this[5])
        )
    }

    private data class CoinbaseRow(
        val time: Instant,
        val amount: BigDecimal,
        val currency: Currency
    )
}
