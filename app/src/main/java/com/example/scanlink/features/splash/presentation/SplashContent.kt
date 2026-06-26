package com.example.scanlink.features.splash.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scanlink.R
import com.example.scanlink.core.ui.theme.ScanLinkTheme
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

@Composable
fun SplashContent(
    modifier: Modifier = Modifier
) {
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.9f) }

    LaunchedEffect(Unit) {
        joinAll(
            launch { alpha.animateTo(1f, animationSpec = tween(1500)) },
            launch { scale.animateTo(1f, animationSpec = tween(1500)) }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_scanlink_logo),
            contentDescription = "ScanLink logo",
            modifier = Modifier
                .size(132.dp)
                .graphicsLayer(
                    alpha = alpha.value,
                    scaleX = scale.value,
                    scaleY = scale.value
                )
        )

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Scan")
                }
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Link")
                }
            },
            fontSize = 30.sp,
            modifier = Modifier.graphicsLayer(alpha = alpha.value)
        )
    }
}


