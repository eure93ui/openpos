package org.codeberg.assertix.openpos.app.settings

enum class ScreenZoomValue(val value: Float) {
    Zero(1f), One(1.15f),
    Two(1.25f), Three(1.4f),
    Four(1.5f), Five(1.65f),
    Six(1.75f), Seven(1.9f),
    Eight(2f);

    companion object {
        fun safeLoad(value: Float) = entries.find { it.value == value } ?: Zero
    }
}