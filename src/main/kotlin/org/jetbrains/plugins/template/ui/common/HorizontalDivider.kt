package org.jetbrains.plugins.template.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Divider

@Composable
fun HorizontalDivider() {
    Divider(
        modifier = Modifier.fillMaxWidth(),
        orientation = Orientation.Horizontal
    )
}
