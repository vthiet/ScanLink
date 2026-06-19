package com.example.scanlink.features.file_sharing.presentation.ui.component.bottom_bar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BottomNavItem(
    val title: String,
    val icon: ImageVector
)

@Composable
fun AppBottomBar(
    currentTab: Int = 1,
    onTabSelected: (Int) -> Unit = {} ,
    onCameraClick: () -> Unit = {}
) {

    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home),
        BottomNavItem("Transfer", Icons.Default.SwapHoriz),
        BottomNavItem("History", Icons.Default.History),
        BottomNavItem("Profile", Icons.Default.AccountCircle)
    )

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

            // Home
            BottomBarItem(
                item = items[0],
                selected = currentTab == 0,
                onClick = { onTabSelected(0) }
            )

            // Transfer
            BottomBarItem(
                item = items[1],
                selected = currentTab == 1,
                onClick = { onTabSelected(1) }
            )

            Spacer(modifier = Modifier.width(70.dp))

            // History
            BottomBarItem(
                item = items[2],
                selected = currentTab == 2,
                onClick = { onTabSelected(2) }
            )

            // Profile
            BottomBarItem(
                item = items[3],
                selected = currentTab == 3,
                onClick = { onTabSelected(3) }
            )
        }

        Box(
            modifier = Modifier
                .size(86.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-22).dp)
                .background(color = Color.Black, shape = CircleShape)
                .padding(8.dp)
                .background(color = Color(0xFF00E0A4), shape = CircleShape)
                .clickable { onCameraClick()  },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(35.dp)
            )
        }
    }
}

