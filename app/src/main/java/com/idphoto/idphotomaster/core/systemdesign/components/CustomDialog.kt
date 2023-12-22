package com.idphoto.idphotomaster.core.systemdesign.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun CustomDialog(dialogItem: MutableState<DialogItem?>) {
    AlertDialog(
        onDismissRequest = {
            dialogItem.value = null
        },
        title = {
            dialogItem.value?.title?.let { Text(text = it) }
        },
        text = {
            dialogItem.value?.description?.let { Text(it) }
        },
        confirmButton = {
            dialogItem.value?.confirmText?.let {
                Button(
                    onClick = {
                        dialogItem.value = null
                    }) {
                    Text(it)
                }
            }
        },
        dismissButton = {
            dialogItem.value?.dismissText?.let {
                Button(onClick = {
                    dialogItem.value = null
                }) {
                    Text(it)
                }
            }
        }
    )
}

data class DialogItem(
    val title: String?,
    val description: String?,
    val confirmText: String?,
    val dismissText: String?,
)