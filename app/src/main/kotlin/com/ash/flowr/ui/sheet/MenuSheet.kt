package com.ash.flowr.ui.sheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ash.flowr.ui.theme.MutedText

@Composable
fun MenuSheet(
    portfolioEnabled: Boolean = false,
    onStatsClick: () -> Unit = {},
    onPortfolioClick: () -> Unit = {},
    onRecurringClick: () -> Unit = {},
    onExportClick: () -> Unit = {},
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            "Menu",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 12.dp)
        )

        MenuTile(
            icon = Icons.Default.BarChart,
            label = "Stats",
            description = "Monthly breakdown by category",
            onClick = { onStatsClick(); onDismiss() }
        )

        if (portfolioEnabled) {
            MenuTile(
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                label = "Portfolio",
                description = "Holdings P&L",
                onClick = { onPortfolioClick(); onDismiss() }
            )
        }

        MenuTile(
            icon = Icons.Default.Repeat,
            label = "Recurring",
            description = "Templates and schedules",
            onClick = { onRecurringClick(); onDismiss() }
        )

        MenuTile(
            icon = Icons.Default.Share,
            label = "Export",
            description = "JSON + CSV to Downloads",
            onClick = { onExportClick(); onDismiss() }
        )

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun MenuTile(
    icon: ImageVector,
    label: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MutedText)
        }
    }
}
