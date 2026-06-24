package com.example.scanlink.features.dashboard.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scanlink.core.ui.model.home.QuickAction

@Composable
fun QuickActionSection(
    actions: List<QuickAction>,
    onActionClick: (QuickAction) -> Unit = {}
) {
    // Sử dụng Column + Row giúp layout ổn định hơn, không bị cắt chữ hàng dưới
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val rows = actions.chunked(3)
        rows.forEach { rowActions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowActions.forEach { action ->
                    QuickActionItem(
                        action = action,
                        onActionClick = onActionClick,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Bù khoảng trống nếu hàng không đủ 3 items
                if (rowActions.size < 3) {
                    repeat(3 - rowActions.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionItem(
    action: QuickAction,
    onActionClick: (QuickAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onActionClick(action) }
            .padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFF333333)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = action.iconRes),
                contentDescription = null,
                modifier = Modifier.size(action.iconSize.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = action.title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            minLines = 1,
            maxLines = 2
        )
    }
}
