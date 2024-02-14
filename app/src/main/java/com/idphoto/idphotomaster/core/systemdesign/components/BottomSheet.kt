package com.idphoto.idphotomaster.core.systemdesign.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.idphoto.idphotomaster.core.domain.model.InfoBottomSheetItem
import com.idphoto.idphotomaster.core.systemdesign.ui.theme.BackgroundColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = { onDismissRequest() },
        sheetState = sheetState,
        dragHandle = dragHandle,
        shape = shape,
        containerColor = BackgroundColor,
        contentColor = BackgroundColor
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationBottomSheet(
    onDismissRequest: () -> Unit,
    infoBottomSheetItem: InfoBottomSheetItem
) {
    val sheetState = rememberModalBottomSheetState(true)
    val scrollState = rememberScrollState()
    BottomSheet(onDismissRequest = onDismissRequest, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.6f)
        ) {
            Text(
                text = infoBottomSheetItem.title,
                style = MaterialTheme.typography.headlineMedium.copy(color = Color.Black),
                modifier = Modifier.padding(20.dp)
            )
            HtmlText(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                    .verticalScroll(scrollState),
                html = infoBottomSheetItem.description
            )
        }
    }
}