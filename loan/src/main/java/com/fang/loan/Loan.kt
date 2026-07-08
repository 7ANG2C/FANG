package com.fang.loan

data class Loan(
    val name: String,
    val amount: Int,
    val remain: Int,
    val day: Int,
) {
    companion object {
        val loans =
            listOf(
                Loan(name = "安泰", amount = 1530, remain = 4, day = 18),
                Loan(name = "台金", amount = 3120, remain = 4, day = 17),
                Loan(name = "玉山", amount = 2449, remain = 4, day = 13),
                Loan(name = "鄉民", amount = 5673, remain = 10, day = 18),
                Loan(name = "信用", amount = 5817, remain = 11, day = 8),
                Loan(name = "國泰", amount = 3696, remain = 16, day = 17),
                Loan(name = "玉山", amount = 1056, remain = 16, day = 13),
                Loan(name = "連線", amount = 15672, remain = 120, day = 18),
            )
    }

    val remainAmount get() = amount * remain
}
