package com.fang.arrangement.definition.sheet

internal class NoSheetException(clazz: Any) : Throwable(clazz.toString())

internal class NoRowIdException(sheetName: String, key: String, value: String) : Throwable(
    "SheetName: $sheetName, Key: $key, Value: $value",
)
