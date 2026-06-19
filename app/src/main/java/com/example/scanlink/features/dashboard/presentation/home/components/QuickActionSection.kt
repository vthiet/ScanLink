package com.example.scanlink.features.file_sharing.presentation.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.scanlink.features.file_sharing.presentation.model.home.QuickAction

@Composable
fun QuickActionSection(
    actions: List<QuickAction>
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .height(220.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        userScrollEnabled = false
    ) {

        items(actions) { action ->

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(65.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4A4A4A)),
                    contentAlignment = Alignment.Center
                ) {

                    Image(
                        painter = painterResource(id = action.iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(action.iconSize.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = action.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}