package com.idphoto.idphotomaster.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.systemdesign.components.BottomSheet
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.BackgroundColor
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.Blue
import com.idphoto.idphotomaster.feature.login.components.UserInputTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterEmailBottomSheet(
    modifier: Modifier,
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(true)
    val mail = remember { mutableStateOf("") }
    BottomSheet(onDismissRequest = { onDismissRequest.invoke() }, sheetState = sheetState) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(color = BackgroundColor)
                .padding(horizontal = 24.dp, vertical = 12.dp),
        ) {
            Text(
                text = stringResource(id = R.string.forgot_password),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Medium),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(id = R.string.reset_password_description),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(10.dp))
            UserInputTextField(
                value = mail.value,
                errorMessageRes = null,
                onValueChange = { mail.value = it },
                placeholder = R.string.mail_hint
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue),
                onClick = {
                    if (mail.value.isEmpty().not()) {
                        onConfirm.invoke(mail.value)
                    }
                }) {
                Text(
                    text = stringResource(id = R.string.send),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
        }
    }
}