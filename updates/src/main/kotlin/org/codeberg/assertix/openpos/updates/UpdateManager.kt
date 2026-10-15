package org.codeberg.assertix.openpos.updates

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createTempFile
import kotlin.io.path.outputStream
import kotlin.runCatching
import kotlin.system.exitProcess

// HARDCODED
private const val CODEBERG = "https://codeberg.org"
private const val OWNER = "assertix"
private const val REPO = "openpos"
private const val UPDATE_FILE = ".next-update.txt"

class UpdateManager(
    private val currentVersion: String,
    private val baseUrl: String = CODEBERG,
    private val onExit: (Path) -> Unit = { installer ->
        ProcessBuilder(installer.absolutePathString()).start()
        exitProcess(0)
    }
) {
    private val httpClient = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build()

    private fun constructRawUrl(version: String) =
        "$baseUrl/$OWNER/$REPO/src/tag/$version/$UPDATE_FILE"

    suspend fun checkForUpdate() = withContext(Dispatchers.IO) {
        runCatching {
            val uri = URI(constructRawUrl(currentVersion))
            val request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build()

            val response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
            )

            if (response.statusCode() == 200) {
                val content = response.body().trim()
                // TODO check for validity later
                if (content.isNotEmpty()) return@runCatching content
            }
            null
        }
    }

    suspend fun downloadInstaller(
        rawUrl: String,
        onProgress: (Float) -> Unit,
    ) = withContext(Dispatchers.IO) {
        runCatching {
            val request = HttpRequest.newBuilder()
                .GET()
                .uri(URI(rawUrl))
                .build()

            val response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofInputStream()
            )

            if (response.statusCode() != 200) throw RuntimeException("Error with downloading update: ${response.statusCode()}")

            val contentLength = response.headers()
                .firstValue("Content-Length")
                .map { it.toLong() }.orElse(-1L)!!
            val tempFile = createTempFile("openpos-updater", ".exe")

            response.body().use { inputStream ->
                tempFile.outputStream().use { outputStream ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var bytesRead: Int
                    var totalBytesRead = 0L

                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead

                        if (contentLength > 0) {
                            val progress = totalBytesRead.toFloat() / contentLength
                            onProgress(progress)
                        }
                    }
                }
            }

            tempFile
        }
    }

    fun launchInstallerAndExit(installer: Path) {
        onExit(installer)
    }
}