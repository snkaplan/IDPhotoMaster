package com.idphoto.idphotomaster.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.idphoto.idphotomaster.R
import com.idphoto.idphotomaster.core.domain.model.AppLanguageItem
import com.idphoto.idphotomaster.core.systemdesign.components.BottomSheet
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.BackgroundColor
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.Blue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeLanguageBottomSheet(
    modifier: Modifier,
    onDismissRequest: () -> Unit,
    options: List<AppLanguageItem>?,
    onConfirm: (String?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(true)
    var selectedOptionCode by remember { mutableStateOf(options?.find { it.isCurrentLanguage }?.code) }
    val scrollState = rememberScrollState()
    BottomSheet(onDismissRequest = { onDismissRequest.invoke() }, sheetState = sheetState) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .background(color = BackgroundColor)
                .padding(horizontal = 24.dp, vertical = 12.dp),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.change_language),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Medium),
                    color = Color.Black
                )
                Icon(
                    modifier = Modifier.clickable {
                        onDismissRequest.invoke()
                    },
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close"
                )
            }
            Column {
                options?.forEach { option ->
                    LanguageItem(
                        option = option,
                        onClick = { selectedOptionCode = option.code },
                        isSelected = selectedOptionCode == option.code
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue),
                    onClick = { onConfirm.invoke(selectedOptionCode) }) {
                    Text(
                        text = "Confirm",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun LanguageItem(option: AppLanguageItem, onClick: (AppLanguageItem) -> Unit, isSelected: Boolean) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick.invoke(option) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onClick.invoke(option) },
            colors = RadioButtonDefaults.colors(
                selectedColor = Blue,
                unselectedColor = MaterialTheme.colorScheme.secondary
            )
        )
        Text(
            text = option.language,
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.bodyMedium.copy(color = Blue)
        )
    }
}