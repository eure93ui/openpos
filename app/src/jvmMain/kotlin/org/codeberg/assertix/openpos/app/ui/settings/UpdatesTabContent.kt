package org.codeberg.assertix.openpos.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.codeberg.assertix.openpos.app.ui.components.FormSectionCard
import org.codeberg.assertix.openpos.app.ui.components.MessageBanner
import org.codeberg.assertix.openpos.app.ui.toImage
import org.codeberg.assertix.openpos.app.util.appVersion
import org.codeberg.assertix.openpos.resources.Res
import org.codeberg.assertix.openpos.resources.info
import org.codeberg.assertix.openpos.resources.search
import org.codeberg.assertix.openpos.resources.upload_file
import org.codeberg.assertix.openpos.updates.UpdateManager

@Composable
internal fun UpdatesTabContent(
    updateManager: UpdateManager
) {
    val coroutineScope = rememberCoroutineScope()
    var isChecking by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableStateOf(0f) }
    var updateUrl by remember { mutableStateOf<String?>(null) }
    var isUpToDate by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FormSectionCard(title = "Обновления приложения") {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Текущая версия:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = appVersion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                MessageBanner(
                    errorMessage = errorMessage,
                    successMessage = null,
                    onDismiss = { errorMessage = null }
                )

                if (isUpToDate) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Res.drawable.info.toImage()
                            Text(
                                text = "У вас установлена самая свежая версия приложения ($appVersion).",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (updateUrl != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Res.drawable.upload_file.toImage()
                                Text(
                                    text = "Доступно новое обновление!",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            if (isDownloading) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    LinearProgressIndicator(
                                        progress = { downloadProgress },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Text(
                                        text = "Загрузка обновления: ${(downloadProgress * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            } else {
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            isDownloading = true
                                            downloadProgress = 0f
                                            errorMessage = null
                                            val result = updateManager.downloadInstaller(updateUrl!!) { progress ->
                                                downloadProgress = progress
                                            }
                                            isDownloading = false
                                            result.onSuccess { installerPath ->
                                                updateManager.launchInstallerAndExit(installerPath)
                                            }.onFailure { ex ->
                                                errorMessage = ex.localizedMessage ?: "Ошибка загрузки файла обновления"
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Скачать и установить обновление")
                                }
                            }
                        }
                    }
                }

                if (!isDownloading && updateUrl == null) {
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                isChecking = true
                                errorMessage = null
                                isUpToDate = false
                                updateUrl = null
                                val result = updateManager.checkForUpdate()
                                isChecking = false
                                result.onSuccess { url ->
                                    if (url != null) {
                                        updateUrl = url
                                    } else {
                                        isUpToDate = true
                                    }
                                }.onFailure { ex ->
                                    errorMessage = ex.localizedMessage ?: "Не удалось проверить наличие обновлений"
                                }
                            }
                        },
                        enabled = !isChecking,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isChecking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Проверка обновлений...")
                        } else {
                            Res.drawable.search.toImage()
                            Spacer(Modifier.width(8.dp))
                            Text("Проверить обновление")
                        }
                    }
                }
            }
        }
    }
}
