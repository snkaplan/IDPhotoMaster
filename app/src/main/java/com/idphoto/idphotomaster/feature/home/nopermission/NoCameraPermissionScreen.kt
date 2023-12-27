package com.idphoto.idphotomaster.feature.home.nopermission

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.systemdesign.icon.AppIcons
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.BackgroundColor
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.Blue
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.Pink

@Composable
fun NoCameraPermissionScreen(onRequestPermission: () -> Unit) {
    NoPermissionContent(
        onRequestPermission = onRequestPermission
    )
}

@Composable
private fun NoPermissionContent(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Surface(
            shape = CircleShape,
            color = BackgroundColor,
            modifier = Modifier
                .size(150.dp)
                .zIndex(1f)
        ) {
            Icon(
                imageVector = AppIcons.Camera,
                contentDescription = "No Permission Icon",
                modifier = Modifier.padding(6.dp),
                tint = Pink
            )
        }
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = BackgroundColor,
            modifier = Modifier
                .padding(horizontal = 50.dp)
                .offset(y = (-30).dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(id = R.string.camera_permission),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(top = 30.dp, start = 10.dp, end = 10.dp)
                )
                Button(
                    onClick = onRequestPermission, modifier = Modifier.padding(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue)
                ) {
                    Text(
                        text = stringResource(id = R.string.give_permission),
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(2.dp),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview_NoPermissionContent() {
    NoPermissionContent(
        onRequestPermission = {}
    )
}