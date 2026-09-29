package org.codeberg.assertix.openpos.app

import com.russhwolf.settings.Settings
import java.time.LocalDateTime

private const val KEY_INITIAL_DATE = "trial_initial_date"
private const val KEY_FINISH_DATE = "trial_finish_date"
private const val KEY_LAST_DATE = "trial_last_date"
private const val KEY_BLOCKED = "trial_blocked"

class TrialVerifier(
    private val settings: Settings,
) {
    fun isTrialActive(): Boolean {
        if (settings.getBoolean(KEY_BLOCKED, false)) {
            return false
        }

        val now = LocalDateTime.now()

        val initialDate = settings
            .getStringOrNull(KEY_INITIAL_DATE)
            ?.let(LocalDateTime::parse)

        val finishDate = settings
            .getStringOrNull(KEY_FINISH_DATE)
            ?.let(LocalDateTime::parse)

        val lastDate = settings
            .getStringOrNull(KEY_LAST_DATE)
            ?.let(LocalDateTime::parse)

        // First startup
        if (initialDate == null || finishDate == null) {
            val trialFinishDate = now.plusDays(14)

            settings.putString(KEY_INITIAL_DATE, now.toString())
            settings.putString(KEY_FINISH_DATE, trialFinishDate.toString())
            settings.putString(KEY_LAST_DATE, now.toString())
            settings.putBoolean(KEY_BLOCKED, false)

            return true
        }

        // Device clock was moved backwards
        if (lastDate != null && now.isBefore(lastDate)) {
            blockTrial()
            return false
        }

        settings.putString(KEY_LAST_DATE, now.toString())

        // Trial expired
        if (!now.isBefore(finishDate)) {
            blockTrial()
            return false
        }

        return true
    }

    private fun blockTrial() {
        settings.putBoolean(KEY_BLOCKED, true)
    }
}
