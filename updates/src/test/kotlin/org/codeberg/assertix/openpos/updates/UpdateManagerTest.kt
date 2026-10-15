package org.codeberg.assertix.openpos.updates

import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.test.*

class UpdateManagerTest {

    private lateinit var httpServer: HttpServer
    private lateinit var updateManager: UpdateManager
    private var lastRequestedPath: String? = null
    private var responseCode: Int = 200
    private var responseBody: String = ""
    private var exitedPath: Path? = null

    @BeforeTest
    fun setUp() {
        httpServer = HttpServer.create(InetSocketAddress("localhost", 0), 0)
        httpServer.createContext("/") { exchange ->
            lastRequestedPath = exchange.requestURI.path
            val bytes = responseBody.toByteArray()
            exchange.sendResponseHeaders(responseCode, if (responseBody.isEmpty()) -1 else bytes.size.toLong())
            if (responseBody.isNotEmpty()) {
                exchange.responseBody.write(bytes)
            }
            exchange.close()
        }
        httpServer.start()
        val port = httpServer.address.port
        val baseUrl = "http://localhost:$port"

        updateManager = UpdateManager(
            currentVersion = "1.0.0",
            baseUrl = baseUrl,
            onExit = { path -> exitedPath = path }
        )
    }

    @AfterTest
    fun tearDown() {
        httpServer.stop(0)
        exitedPath = null
        lastRequestedPath = null
    }

    @Test
    fun `checkForUpdate returns update raw url when server responds with 200 and content`() = runBlocking {
        val downloadUrl = "https://example.com/installer.exe"
        responseCode = 200
        responseBody = downloadUrl

        val result = updateManager.checkForUpdate()

        assertTrue(result.isSuccess)
        assertEquals(downloadUrl, result.getOrNull())
        assertEquals("/assertix/openpos/raw/tag/1.0.0/.next-update.txt", lastRequestedPath)
    }

    @Test
    fun `checkForUpdate returns null when server responds with 404`() = runBlocking {
        responseCode = 404
        responseBody = ""

        val result = updateManager.checkForUpdate()

        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `checkForUpdate returns null when body is empty`() = runBlocking {
        responseCode = 200
        responseBody = ""

        val result = updateManager.checkForUpdate()

        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `downloadInstaller downloads file and reports progress successfully`() = runBlocking {
        val fileContent = "binary-installer-content-mock"
        responseCode = 200
        responseBody = fileContent

        val progressValues = mutableListOf<Float>()
        val installerUrl = "http://localhost:${httpServer.address.port}/installer.exe"

        val result = updateManager.downloadInstaller(installerUrl) { progress ->
            progressValues.add(progress)
        }

        assertTrue(result.isSuccess)
        val tempFile = result.getOrNull()
        assertNotNull(tempFile)
        assertEquals(fileContent, tempFile.readText())
        assertTrue(progressValues.isNotEmpty())
        assertEquals(1.0f, progressValues.last())
    }

    @Test
    fun `downloadInstaller fails when server responds with error status`() = runBlocking {
        responseCode = 500
        responseBody = "Internal Server Error"

        val installerUrl = "http://localhost:${httpServer.address.port}/installer.exe"

        val result = updateManager.downloadInstaller(installerUrl) {}

        assertTrue(result.isFailure)
        assertEquals(true, result.exceptionOrNull()?.message?.contains("500"))
    }

    @Test
    fun `launchInstallerAndExit invokes exit handler with path`() {
        val dummyPath = Path.of("dummy-installer.exe")
        updateManager.launchInstallerAndExit(dummyPath)

        assertEquals(dummyPath, exitedPath)
    }
}
