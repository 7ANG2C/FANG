package com.fang.loan

data class Loan(
    val name: String,
    val all: Int,
    val addition: Int,
    val amount: Int,
    val remain: Int,
) {
    companion object {
        val loans =
            listOf(
                Loan(name = "鄉民", all = 150000, addition = 0, amount = 5673, remain = 23),
                Loan(name = "信用", all = 150000, addition = 0, amount = 5817, remain = 24),
                Loan(name = "樂天", all = 200000, addition = 0, amount = 2598, remain = 77),
                Loan(name = "將來", all = 700000, addition = 0, amount = 9303, remain = 70),
                Loan(name = "將來", all = 800000, addition = 0, amount = 10520, remain = 64),
                Loan(name = "台新", all = 280000, addition = 6030, amount = 3725, remain = 84),
                Loan(name = "安泰", all = 36720, addition = 0, amount = 1530, remain = 17),
                Loan(name = "台金", all = 74880, addition = 0, amount = 3120, remain = 17),
                Loan(name = "玉山", all = 58776, addition = 0, amount = 2449, remain = 17),
            )
    }
}
