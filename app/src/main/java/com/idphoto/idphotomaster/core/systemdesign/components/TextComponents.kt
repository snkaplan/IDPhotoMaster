package com.idphoto.idphotomaster.core.systemdesign.components

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.BackgroundColor

@Composable
fun DefaultText(text: String, modifier: Modifier, style: TextStyle, textAlign: TextAlign? = null) {
    Text(
        text = text,
        style = style,
        fontFamily = FontFamily.Monospace,
        modifier = modifier,
        textAlign = textAlign
    )
}

@Composable
fun DefaultText(
    text: AnnotatedString,
    modifier: Modifier,
    style: TextStyle,
    textAlign: TextAlign? = null
) {
    Text(
        text = text,
        style = style,
        fontFamily = FontFamily.Monospace,
        modifier = modifier,
        textAlign = textAlign
    )
}

@Composable
fun BoldTitleMedium(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    style: TextStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
) {
    DefaultText(
        text = text,
        modifier = modifier,
        style = style,
        textAlign = textAlign
    )
}

@Composable
fun BoldTitleMedium(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null,
    style: TextStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
) {
    DefaultText(
        text = text,
        modifier = modifier,
        style = style,
        textAlign = textAlign
    )
}


@Composable
fun TitleMedium(
    text: String, modifier: Modifier = Modifier, textAlign: TextAlign? = null,
    style: TextStyle = MaterialTheme.typography.titleMedium
) {
    DefaultText(
        text = text,
        modifier = modifier,
        style = style,
        textAlign = textAlign
    )
}

@Composable
fun BoldLabelMedium(
    text: String, modifier: Modifier = Modifier, textAlign: TextAlign? = null,
    style: TextStyle = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
) {
    DefaultText(
        text = text,
        modifier = modifier,
        style = style,
        textAlign = textAlign
    )
}

@Composable
fun HtmlText(html: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                settings.setSupportZoom(false)
            }
        },
        update = { webView ->
            webView.setBackgroundColor(BackgroundColor.toArgb())
            webView.loadData(html, "text/html", "UTF-8")
        })
}