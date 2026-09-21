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
    data object Rate20 : TaxRate {
        override val percent: Int = 20
    }

    @Serializable
    data class Custom(
        override val percent: Int,
    ) : TaxRate

    companion object {
        val all = listOf(None, Rate5, Rate20)

        fun from(value: Int): TaxRate =
            when (value) {
                None.percent -> None
                Rate5.percent -> Rate5
                Rate20.percent -> Rate20
                else -> Custom(value)
            }
    }
}
