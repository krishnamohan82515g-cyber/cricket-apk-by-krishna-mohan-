package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CricketGoldLight
import com.example.ui.theme.CricketGoldSecondary

import androidx.compose.ui.text.style.TextOverflow

@Composable
fun UserNameDisplay(
    fullName: String,
    modifier: Modifier = Modifier,
    isRowLayout: Boolean = false,
    primaryFontSize: TextUnit = 16.sp,
    secondaryFontSize: TextUnit = 11.sp,
    primaryColor: Color = Color.Unspecified,
    secondaryColor: Color = Color.Unspecified,
    primaryFontWeight: FontWeight = FontWeight.Bold
) {
    val openParen = fullName.indexOf('(')
    val closeParen = fullName.indexOf(')', openParen)

    val mainName = if (openParen > 0) fullName.substring(0, openParen).trim() else fullName
    val aliasName = if (openParen in 0 until closeParen) {
        fullName.substring(openParen, closeParen + 1).trim()
    } else if (openParen >= 0) {
        fullName.substring(openParen).trim()
    } else null

    if (aliasName == null) {
        Text(
            text = mainName,
            fontSize = primaryFontSize,
            fontWeight = primaryFontWeight,
            color = primaryColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
        )
    } else if (isRowLayout) {
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = mainName,
                fontSize = primaryFontSize,
                fontWeight = primaryFontWeight,
                color = primaryColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = aliasName,
                fontSize = secondaryFontSize,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic,
                color = if (secondaryColor != Color.Unspecified) secondaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    } else {
        Column(modifier = modifier) {
            Text(
                text = mainName,
                fontSize = primaryFontSize,
                fontWeight = primaryFontWeight,
                color = primaryColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = aliasName,
                fontSize = secondaryFontSize,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic,
                color = if (secondaryColor != Color.Unspecified) secondaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
