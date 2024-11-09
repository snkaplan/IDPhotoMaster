package com.idphoto.idphotomaster.core.systemdesign.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.Blue
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.White

@Composable
fun ScreenButton(modifier: Modifier = Modifier, text: String, onAction: () -> Unit, enabled: Boolean = true) {
    Button(
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = Blue),
        onClick = { onAction.invoke() },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(10.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(12.dp),
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold, color = White)
        )
    }
}