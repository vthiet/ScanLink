package com.example.scanlink.features.document_scanner.presentation.ocr.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false
) {

    Button(
        onClick = {},
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor =
                if (isPrimary)
                    Color(0xFF2ABA8A)
                else
                    Color(0xFF1E1E1E)
        )
    ) {

        Icon(icon, null)

        Spacer(modifier = Modifier.width(5.dp))

        Text(text)
    }
}