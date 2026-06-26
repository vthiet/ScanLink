package com.example.scanlink.core.ui.components.bottom_bar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.scanlink.core.ui.components.bottom_bar.model.items

@Composable
fun AppBottomBar(
    currentRoute: String?,
    onTabSelected: (String) -> Unit,
    onCameraClick: () -> Unit
) {

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .background(Color.Black),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            BottomBarItem(
                item = items[0],
                selected = currentRoute == items[0].route,
                onClick = { onTabSelected(items[0].route) }
            )

            BottomBarItem(
                item = items[1],
                selected = currentRoute == items[1].route,
                onClick = { onTabSelected(items[1].route) }
            )

            Spacer(modifier = Modifier.width(70.dp))

            BottomBarItem(
                item = items[2],
                selected = currentRoute == items[2].route,
                onClick = { onTabSelected(items[2].route) }
            )

            BottomBarItem(
                item = items[3],
                selected = currentRoute == items[3].route,
                onClick = { onTabSelected(items[3].route) }
            )
        }

        Box(
            modifier = Modifier
                .size(86.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-22).dp)
                .background(
                    MaterialTheme.colorScheme.surface,
                    CircleShape
                )
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
                .clickable {
                    onCameraClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}