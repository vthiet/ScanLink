package com.example.scanlink.features.authentication.presentation.component

import android.app.DatePickerDialog
import android.icu.util.Calendar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun DatePickerField(
    value: String,
    label: String = "Date of birth",
    errorResId: Int? = null,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current

    val datePickerDialog = remember {
        val today = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val date = "%02d/%02d/%04d".format(day, month + 1, year)
                onDateSelected(date)
            },
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        )
    }

    val showDatePicker = {
        if (value.isNotBlank()) {
            try {
                val parts = value.split("/")
                if (parts.size == 3) {
                    val day = parts[0].toInt()
                    val month = parts[1].toInt() - 1
                    val year = parts[2].toInt()

                    datePickerDialog.updateDate(year, month, day)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val today = Calendar.getInstance()
            datePickerDialog.updateDate(
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
            )
        }
        datePickerDialog.show()
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker() },
            label = { Text(label) },
            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = showDatePicker) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null
                    )
                }
            },
            singleLine = true,
            isError = errorResId != null
        )

        errorResId?.let {
            Text(
                text = stringResource(id = it),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }

}