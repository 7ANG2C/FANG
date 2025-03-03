package com.fang.arrangement

@Suppress("unused")
enum class Arrangement(
    val id: String,
) {
    PROD("1hYhuc7IYnVkjx6qK7WePQiTF7Jw9ZUwC-pU8DMVcNdI"),
    UAT("1Z7uSrOTASCKYvEydJ_QTClgwaOPvq_xuRqxKGKzrc34"),
    SIT("1jj5ejgD-FtGH6c2tXNtrAEPDmGsAR_n2yDWRIXRBQac"),
    ;

    companion object {
        val current = PROD
    }
}
