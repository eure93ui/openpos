package org.codeberg.assertix.openpos.app.ui.components

enum class QuerySize(val size: Int) {
    SIZE_10(10),
    SIZE_25(25),
    SIZE_50(50);

    companion object {
        val default = SIZE_10
    }
}
