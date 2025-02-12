@file:OptIn(ExperimentalMaterial3Api::class)

package dev.goblingroup.jetpackcomposedialogspractice

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.goblingroup.jetpackcomposedialogspractice.ui.theme.JetpackComposeDialogsPracticeTheme
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetpackComposeDialogsPracticeTheme {
                val snackBarHostState = remember {
                    SnackbarHostState()
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(snackBarHostState)
                    }
                ) { innerPadding ->
                    MyApp(
                        modifier = Modifier.padding(innerPadding),
                        snackBarHostState = snackBarHostState
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyApp(
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState
) {
    var openAlertDialog by rememberSaveable { mutableStateOf(false) }
    var openCustomDialog by rememberSaveable { mutableStateOf(false) }
    var customDialogClosable by rememberSaveable { mutableStateOf(false) }
    var openDatePickerDialog by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var showSelectedDateResult by rememberSaveable { mutableStateOf(false) }
    var openTimePickerDialog by rememberSaveable { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState()
    var showSelectedTimeResult by rememberSaveable { mutableStateOf(false) }
    var addUserDialog by rememberSaveable { mutableStateOf(false) }
    var bottomSheetList by rememberSaveable {
        mutableStateOf(emptyList<User>())
    }
    val scope = rememberCoroutineScope()
    var snackBarResultToast by remember {
        mutableStateOf("")
    }
    var showUserList by rememberSaveable {
        mutableStateOf(false)
    }
    var autoCloseOnUserAdded by rememberSaveable {
        mutableStateOf(false)
    }

    when {
        openAlertDialog -> {
            DisplayAlertDialog(
                onDismiss = {
                    openAlertDialog = false
                }
            )
        }

        openCustomDialog -> {
            CustomDialog(
                customDialogClosable = customDialogClosable,
                onDismiss = {
                    openCustomDialog = false
                },
                onClosableChange = {
                    customDialogClosable = it
                }
            )
        }

        openDatePickerDialog -> {
            DisplayDatePickerDialog(
                datePickerState = datePickerState,
                onDateSelected = { selectedDate ->
                    openDatePickerDialog = false
                    showSelectedDateResult = true
                },
                onDismiss = {
                    openDatePickerDialog = false
                }
            )
        }

        openTimePickerDialog -> {
            DisplayTimePickerDialog(
                timePickerState = timePickerState,
                onTimeSelected = {
                    openTimePickerDialog = false
                    showSelectedTimeResult = true
                },
                onDismiss = {
                    openTimePickerDialog = false
                }
            )
        }

        addUserDialog -> {
            AddUserDialog(
                autoClose = autoCloseOnUserAdded,
                onAutoCloseChange = {
                    autoCloseOnUserAdded = it
                },
                onUserAdded = {
                    bottomSheetList = bottomSheetList + it
                    if (autoCloseOnUserAdded) {
                        addUserDialog = false
                    }
                },
                onDismiss = {
                    addUserDialog = false
                }
            )
        }

        snackBarResultToast.isNotEmpty() -> {
            ToastMessage(message = snackBarResultToast)
            snackBarResultToast = ""
        }

        showUserList -> {
            DisplayUsers(
                userList = bottomSheetList,
                onDelete = {
                    bottomSheetList = bottomSheetList - it
                },
                onDismiss = {
                    showUserList = false
                }
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 80.dp),
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(showSelectedDateResult) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = formatDate(datePickerState.selectedDateMillis)
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .align(Alignment.CenterVertically)
                        .clickable {
                            showSelectedDateResult = false
                        }
                )
            }
        }
        AnimatedVisibility(showSelectedTimeResult) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Hour: ${timePickerState.hour}\nMinute: ${timePickerState.minute}"
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .align(Alignment.CenterVertically)
                        .clickable {
                            showSelectedTimeResult = false
                        }
                )
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
                openAlertDialog = true
            }
        ) {
            Text(
                text = "Alert dialog"
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
                openCustomDialog = true
            }
        ) {
            Text(
                text = "Custom dialog"
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
                openDatePickerDialog = true
            }
        ) {
            Text(
                text = "Date picker dialog"
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
                openTimePickerDialog = true
            }
        ) {
            Text(
                text = "Time picker dialog"
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement
                .spacedBy(5.dp)
        ) {
            Button(
                modifier = Modifier
                    .weight(1f),
                onClick = {
                    addUserDialog = true
                }
            ) {
                Text(
                    text = "Add user"
                )
            }
            Button(
                modifier = Modifier
                    .weight(1f),
                onClick = {
                    showUserList = true
                }
            ) {
                Text(
                    text = "User list"
                )
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
                scope.launch {
                    val result = snackBarHostState
                        .showSnackbar(
                            message = "This is an experiment of using snack bar",
                            actionLabel = "Action",
                            withDismissAction = true,
                            duration = SnackbarDuration.Long
                        )
                    snackBarResultToast = when (result) {
                        SnackbarResult.Dismissed -> {
                            "SnackBar dismissed"
                        }

                        SnackbarResult.ActionPerformed -> {
                            "SnackBar action performed"
                        }
                    }
                }
            }
        ) {
            Text(
                text = "Snack bar"
            )
        }
    }
}

@Composable
fun DisplayAlertDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = "Ok")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = "Cancel")
            }
        },
        title = {
            Text(
                text = "Alert dialog title",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "This is an alert dialog with buttons and rounded corners"
            )
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun CustomDialog(
    customDialogClosable: Boolean,
    onDismiss: () -> Unit,
    onClosableChange: (Boolean) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = customDialogClosable,
            dismissOnClickOutside = customDialogClosable,
            usePlatformDefaultWidth = true,
            decorFitsSystemWindows = true
        )
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        horizontal = 30.dp,
                        vertical = 20.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "This is a test for custom dialog",
                    color = Color.Blue,
                    fontSize = 20.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = customDialogClosable,
                        onCheckedChange = {
                            onClosableChange(it)
                        }
                    )
                    Text(
                        text = "Close dialog on back press or outside click"
                    )
                }
                Button(
                    onClick = onDismiss
                ) {
                    Text(
                        text = "Close dialog"
                    )
                }
            }
        }
    }
}

@Composable
fun DisplayDatePickerDialog(
    datePickerState: DatePickerState,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                }
            ) {
                Text(text = "Ok")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = "Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatDate(time: Long?): String {
    return time?.let {
        val localDate = Instant.ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        localDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
    } ?: "No date selected"
}

@Composable
fun DisplayTimePickerDialog(
    timePickerState: TimePickerState,
    onTimeSelected: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onTimeSelected
            ) {
                Text(text = "Ok")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = "Cancel")
            }
        },
        title = {
            Text(text = "Select time")
        },
        text = {
            TimePicker(
                state = timePickerState
            )
        }
    )
}

@Composable
fun AddUserDialog(
    autoClose: Boolean,
    onAutoCloseChange: (Boolean) -> Unit,
    onUserAdded: (User) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    ModalBottomSheet(
        modifier = Modifier
            .fillMaxWidth(),
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        var name by rememberSaveable {
            mutableStateOf("")
        }
        var phoneNumber by rememberSaveable {
            mutableStateOf("")
        }
        var showErrors by rememberSaveable {
            mutableStateOf(false)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 10.dp
                ),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = name,
                placeholder = {
                    Text(text = "Your name")
                },
                onValueChange = {
                    name = it
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                isError = showErrors && name.isEmpty(),
                singleLine = true,
                supportingText = {
                    if (showErrors && name.isEmpty()) {
                        Text(text = "Name is required")
                    }
                }
            )
            val keyboardController = LocalSoftwareKeyboardController.current
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = phoneNumber,
                placeholder = {
                    Text(
                        text = "Your phone number"
                    )
                },
                onValueChange = {
                    phoneNumber = it
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        if (name.isNotEmpty() && phoneNumber.isNotEmpty()) {
                            onUserAdded(User(name, phoneNumber))
                            name = ""
                            phoneNumber = ""
                            showErrors = false
                        } else {
                            showErrors = true
                        }
                    }
                ),
                isError = showErrors && phoneNumber.isEmpty(),
                singleLine = true,
                supportingText = {
                    if (showErrors && phoneNumber.isEmpty()) {
                        Text(text = "Phone number is required")
                    }
                }
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    if (name.isNotEmpty() && phoneNumber.isNotEmpty()) {
                        onUserAdded(User(name, phoneNumber))
                        name = ""
                        phoneNumber = ""
                        showErrors = false
                    } else {
                        showErrors = true
                    }
                }
            ) {
                Text(
                    text = "Add user"
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = autoClose,
                    onCheckedChange = {
                        onAutoCloseChange(it)
                    }
                )
                Text(text = "Close dialog on user added")
            }
        }
    }
}

@Composable
fun DisplayUsers(
    userList: List<User>,
    onDelete: (User) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        modifier = Modifier
            .fillMaxWidth(),
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items = userList) { user ->
                UserItem(
                    user = user,
                    onDelete = {
                        onDelete(user)
                    }
                )
            }
        }
    }
}

@Composable
fun UserItem(
    user: User,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = user.phoneNumber,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color.Red
                )
            }
        }
    }
}

@Composable
fun ToastMessage(message: String) {
    Toast.makeText(LocalContext.current, message, Toast.LENGTH_SHORT).show()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetpackComposeDialogsPracticeTheme {
        MyApp(
            modifier = Modifier
                .fillMaxSize(),
            snackBarHostState = SnackbarHostState()
        )
    }
}