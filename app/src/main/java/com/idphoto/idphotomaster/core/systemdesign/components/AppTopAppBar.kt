package com.idphoto.idphotomaster.core.systemdesign.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: AnnotatedString,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    leftIcon: ImageVector? = null,
    rightIcon: ImageVector? = null,
    onRightIconClicked: (() -> Unit)? = null,
    onLeftIconClicked: (() -> Unit)? = null
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
        modifier = modifier.fillMaxWidth(),
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
        title = {
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leftIcon != null) {
                    Icon(
                        imageVector = leftIcon,
                        contentDescription = "Icon",
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .scale(0.9f)
                            .clickable {
                                onLeftIconClicked?.invoke()
                            }
                    )
                }
                BoldTitleMedium(
                    modifier = Modifier.padding(start = 5.dp),
                    text = title,
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
                )
            }
        },
        actions = {
            if (rightIcon != null) {
                IconButton(onClick = {
                    onRightIconClicked?.invoke()
                }) {
                    Icon(
                        imageVector = rightIcon,
                        contentDescription = "Right Icon"
                    )
                }
            }
        }
    )
}