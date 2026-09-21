package org.codeberg.assertix.openpos.app.ui

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun DrawableResource.toImage() {
    Image(
        painterResource(this),
        ""
    )
}