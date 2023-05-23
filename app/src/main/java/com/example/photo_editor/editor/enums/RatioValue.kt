package com.example.photo_editor.editor.enums

enum class RatioValue (val id: Int, val ratio: Float) {
    Original(0, -1f), // -1 will not be used actually
    _1x1(1, 1f),
    _4x5(2, 4f / 5f),
    _9x16(7, 9f / 16f),
    _3x4(4, 3f / 4f),
    _4x3(5, 4f / 3f),
    _2x3(9, 2f / 3f),
    _3x2(6, 3f / 2f),
    _5x4(3, 5f / 4f),
    _16x9(7, 16f / 9f),
}