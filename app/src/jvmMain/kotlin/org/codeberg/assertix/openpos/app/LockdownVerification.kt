package org.codeberg.assertix.openpos.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.russhwolf.settings.Settings
import org.codeberg.assertix.openpos.app.ui.startup.StartupConstants
import org.codeberg.assertix.openpos.app.ui.startup.StartupHeader
import org.codeberg.assertix.openpos.resources.Res
import org.codeberg.assertix.openpos.resources.error
import java.net.InetAddress
import java.net.NetworkInterface
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

sealed interface TrialStatus {
    data object Active : TrialStatus
    data class Expired(val daysRemaining: Int) : TrialStatus
    data object Blocked : TrialStatus
}

object HardwareIdProvider {
    fun getHardwareId(): String {
        return runCatching {
            val sb = StringBuilder()
            sb.append(System.getProperty("os.name", ""))
            sb.append(System.getProperty("os.arch", ""))
            sb.append(System.getProperty("user.name", ""))
            val host = InetAddress.getLocalHost()
            sb.append(host.hostName)
            NetworkInterface.getByInetAddress(host)?.hardwareAddress?.let { mac ->
                sb.append(mac.joinToString("") { "%02x".format(it) })
            }
            val digest = MessageDigest.getInstance("SHA-256")
            digest.digest(sb.toString().toByteArray(Charsets.UTF_8))
                .joinToString("") { "%02x".format(it) }
        }.getOrElse { "OPENPOS-FALLBACK-HWID" }
    }
}

object LicenseManager {
    // Hardcoded Ed25519 Public Key (DER / X.509 encoded bytes in Base64)
    // Generated via Ed25519 KeyPairGenerator. Developer keeps private key offline for signing.
    // Default valid test public key (DER X.509 encoded Ed25519 public key)
    private const val EMBEDDED_PUBLIC_KEY_BASE64 = "MCowBQYDK2VwAyEAiAYqw5N6PiIC68/6WK+Bo5FSIpFTn6MkYAH5p2cUBY0="

    // Fallback test key generator if default placeholder needs init
    private val defaultPublicKeyBytes: ByteArray by lazy {
        try {
            Base64.getDecoder().decode(EMBEDDED_PUBLIC_KEY_BASE64)
        } catch (e: Exception) {
            // Generate a valid keypair on the fly for safe fallback if base64 is placeholder
            KeyPairGenerator.getInstance("Ed25519").genKeyPair().public.encoded
        }
    }

    fun verifyLicenseKey(
        keyString: String,
        currentHardwareId: String,
        publicKeyBytes: ByteArray = defaultPublicKeyBytes,
        expiresAt: (Instant) -> Unit,
    ): Boolean {
        return try {
            val parts = keyString.trim().split(".")
            if (parts.size != 2) return false

            val payloadBytes = Base64.getUrlDecoder().decode(parts[0])
            val signatureBytes = Base64.getUrlDecoder().decode(parts[1])

            val publicKey = KeyFactory.getInstance("Ed25519")
                .generatePublic(X509EncodedKeySpec(publicKeyBytes))

            val verifier = Signature.getInstance("Ed25519")
            verifier.initVerify(publicKey)
            verifier.update(payloadBytes)

            if (!verifier.verify(signatureBytes)) return false

            val payload = String(payloadBytes)
            val fields = payload.split("|", limit = 2)
            if (fields.size != 2) return false

            val expiresAt = Instant.parse(fields[0])
            val licensedHardwareId = fields[1]

            if (licensedHardwareId == currentHardwareId &&
                expiresAt.isAfter(Instant.now())
            ) {
                expiresAt(expiresAt)
                true
            } else false
        } catch (_: Exception) {
            false
        }
    }
}

object LockdownVerification {
    private const val KEY_INITIAL_DATE = "trial_initial_date"
    private const val KEY_FINISH_DATE = "trial_finish_date"
    private const val KEY_LAST_DATE = "trial_last_date"
    private const val KEY_BLOCKED = "trial_blocked"
    private const val KEY_HWID = "trial_hardware_id"
    private const val KEY_LICENSED = "app_licensed"
    private const val TRIAL_DAYS = 14L

    private val secretKey: SecretKey by lazy {
        val keyBytes = "OpenPOS-Trial-Secret-Key-2026".toByteArray(Charsets.UTF_8).let {
            MessageDigest.getInstance("SHA-256").digest(it).copyOf(16)
        }
        SecretKeySpec(keyBytes, "AES")
    }

    private fun encrypt(value: String): String {
        return try {
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            Base64.getEncoder().encodeToString(cipher.doFinal(value.toByteArray(Charsets.UTF_8)))
        } catch (e: Exception) {
            value
        }
    }

    private fun decrypt(encrypted: String): String {
        return try {
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            String(cipher.doFinal(Base64.getDecoder().decode(encrypted)), Charsets.UTF_8)
        } catch (e: Exception) {
            encrypted
        }
    }

    fun verify(settings: Settings): TrialStatus {
        val currentHwId = HardwareIdProvider.getHardwareId()

        // Check hardware ID binding (anti-reinstall / anti-reset protection)
        val storedHwId = settings.getStringOrNull(KEY_HWID)?.let { decrypt(it) }
        if (storedHwId != null && storedHwId != currentHwId) {
            blockApp(settings)
            return TrialStatus.Blocked
        }
        println("hardware is the same")

        val currentDate = LocalDate.now()

        val encryptedInitial = settings.getStringOrNull(KEY_INITIAL_DATE)
        val encryptedFinish = settings.getStringOrNull(KEY_FINISH_DATE)
        val encryptedLast = settings.getStringOrNull(KEY_LAST_DATE)

        if (encryptedInitial == null || encryptedFinish == null) {
            val finishDate = currentDate.plusDays(TRIAL_DAYS)
            settings.putString(KEY_INITIAL_DATE, encrypt(currentDate.toString()))
            settings.putString(KEY_FINISH_DATE, encrypt(finishDate.toString()))
            settings.putString(KEY_LAST_DATE, encrypt(currentDate.toString()))
            settings.putString(KEY_BLOCKED, encrypt("false"))
            settings.putString(KEY_HWID, encrypt(currentHwId))
            settings.putString(KEY_LICENSED, encrypt("false"))
            return TrialStatus.Active
        }

        val initialDate = runCatching { LocalDate.parse(decrypt(encryptedInitial)) }.getOrNull()
        val finishDate = runCatching { LocalDate.parse(decrypt(encryptedFinish)) }.getOrNull()
        val lastDate = encryptedLast?.let { runCatching { LocalDate.parse(decrypt(it)) }.getOrNull() }

        println("$initialDate $finishDate $lastDate")

        if (initialDate == null || finishDate == null) {
            blockApp(settings)
            return TrialStatus.Blocked
        }

        println("no last dates tempering (config)")

        // Clock tamper-protection: if current date is earlier than last known run timestamp
        if (lastDate != null && currentDate.isBefore(lastDate)) {
            blockApp(settings)
            return TrialStatus.Blocked
        }

        val updatedLastDate = if (lastDate == null || currentDate.isAfter(lastDate)) currentDate else lastDate
        settings.putString(KEY_LAST_DATE, encrypt(updatedLastDate.toString()))

        if (currentDate.isAfter(finishDate)) {
            blockApp(settings)
            return TrialStatus.Blocked
        }

        val encryptedLicensed = settings.getStringOrNull(KEY_LICENSED)
        if (encryptedLicensed != null && decrypt(encryptedLicensed).toBooleanStrictOrNull() == true) {
            return TrialStatus.Active
        }

        val encryptedBlocked = settings.getStringOrNull(KEY_BLOCKED)
        if (encryptedBlocked != null && decrypt(encryptedBlocked).toBooleanStrictOrNull() == true) {
            return TrialStatus.Blocked
        }

        val daysRemaining = ChronoUnit.DAYS.between(currentDate, finishDate).toInt()
        println("days remaining: $daysRemaining")
        return if (daysRemaining <= 0) {
            blockApp(settings)
            TrialStatus.Blocked
        } else {
            TrialStatus.Active
        }
    }

    fun activateLicense(settings: Settings, licenseKey: String): Boolean {
        val currentHwId = HardwareIdProvider.getHardwareId()
        var expiresAt: Instant
        val isValid = LicenseManager.verifyLicenseKey(licenseKey, currentHwId) {
            expiresAt = it
        }
        if (isValid) {
            settings.putString(KEY_BLOCKED, encrypt("false"))
            settings.putString(KEY_LICENSED, encrypt("true"))
            settings.putString(KEY_HWID, encrypt(currentHwId))
            settings.putString(KEY_LAST_DATE, encrypt(currentHwId))
            return true
        }
        return false
    }

    fun blockApp(settings: Settings) {
        settings.putString(KEY_BLOCKED, encrypt("true"))
    }
}

@Composable
fun TrialLockedScreen(settings: Settings, onActivated: () -> Unit) {
    var licenseKeyInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    val hardwareId = remember { HardwareIdProvider.getHardwareId() }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(StartupConstants.SpacingLarge)
    ) {
        StartupHeader(
            icon = Res.drawable.error,
            title = "Trial Period Expired or Locked",
            subtitle = "Your 14-day trial has ended, system time tampering was detected, or hardware signature changed. Please enter your signed Ed25519 license key."
        )

        OutlinedTextField(
            value = hardwareId,
            onValueChange = {},
            readOnly = true,
            label = { Text("Hardware ID") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = licenseKeyInput,
            onValueChange = {
                licenseKeyInput = it
                errorMessage = null
            },
            label = { Text("License Key (payload64.signature64)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (successMessage != null) {
            Text(
                text = successMessage!!,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(StartupConstants.SpacingSmall))

        Button(
            onClick = {
                val success = LockdownVerification.activateLicense(settings, licenseKeyInput)
                if (success) {
                    successMessage = "License verified successfully!"
                    onActivated()
                } else {
                    errorMessage = "Invalid Ed25519 signature or mismatched hardware ID."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Activate License")
        }
    }
}
