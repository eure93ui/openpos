package org.codeberg.assertix.openpos.data.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface TaxRate {
    val percent: Int

    @Serializable
    data object None : TaxRate {
        override val percent: Int = 0
    }

    @Serializable
    data object Rate5 : TaxRate {
        override val percent: Int = 5
    }

    @Serializable
    data object Rate7 : TaxRate {
        override val percent: Int = 7
    }

    @Serializable
    data object Rate22 : TaxRate {
        override val percent: Int = 22
    }

    @Serializable
    data class Custom(
        override val percent: Int,
    ) : TaxRate

    companion object {
        val all = listOf(None, Rate5, Rate7, Rate22)

        fun from(value: Int): TaxRate =
            when (value) {
                None.percent -> None
                Rate5.percent -> Rate5
                Rate22.percent -> Rate22
                else -> Custom(value)
            }
    }
}
