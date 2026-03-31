package com.fang.loan

data class Loan(
    val name: String,
    val all: Int,
    val amount: Int,
    val remain: Int,
    val day: Int,
) {
    companion object {
        val loans =
            listOf(
                Loan(name = "鄉民", all = 150000, amount = 5673, remain = 23, day = 18),
                Loan(name = "信用", all = 150000, amount = 5817, remain = 24, day = 8),
                Loan(name = "樂天", all = 200000, amount = 2598, remain = 77, day = 11),
                Loan(name = "將來", all = 700000, amount = 9303, remain = 70, day = 8),
                Loan(name = "將來", all = 800000, amount = 10520, remain = 64, day = 20),
                Loan(name = "台新", all = 280000, amount = 3725, remain = 84, day = 21),
                Loan(name = "安泰", all = 36720, amount = 1530, remain = 17, day = 18),
                Loan(name = "台金", all = 74880, amount = 3120, remain = 17, day = 17),
                Loan(name = "玉山", all = 58776, amount = 2449, remain = 17, day = 13),
                Loan(name = "國泰", all = 0, amount = 3696, remain = 29, day = 17),
                Loan(name = "玉山", all = 0, amount = 1056, remain = 29, day = 13),
            )
    }

    val remainAmount get() = amount * remain
}
