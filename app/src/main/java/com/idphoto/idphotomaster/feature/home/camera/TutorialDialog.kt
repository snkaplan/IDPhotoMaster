package com.idphoto.idphotomaster.feature.home.camera

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.idphoto.idphotomaster.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorialDialog(onViewEvent: (CameraViewEvent) -> Unit) {
    BasicAlertDialog(onDismissRequest = {
        onViewEvent.invoke(CameraViewEvent.OnTutorialClosed)
    }) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(15.dp))
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier
                    .padding(top = 12.dp, end = 12.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .clickable {
                        onViewEvent.invoke(CameraViewEvent.OnTutorialClosed)
                    }
                    .align(Alignment.End),
                imageVector = Icons.Filled.Close,
                contentDescription = "Close",
                tint = Color.White
            )
            Image(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Red)
                    .size(400.dp),
                contentScale = ContentScale.FillBounds,
                painter = painterResource(id = R.drawable.ic_camera_tutorial),
                contentDescription = "Tutorial Image"
            )
        }
    }
}