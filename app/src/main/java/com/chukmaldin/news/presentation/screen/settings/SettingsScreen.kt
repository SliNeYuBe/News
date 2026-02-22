package com.chukmaldin.news.presentation.screen.settings

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.chukmaldin.news.R

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    onFinished: () -> Unit
) {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            viewModel.processCommand(SettingsCommand.UpdateNotificationEnabled(it))
        }
    )

    val state = viewModel.state.collectAsState()
    when (val currentState = state.value) {
        is SettingsState.Configuration -> {
            Scaffold(
                modifier.fillMaxWidth(),
                topBar = {
                    SettingsTopBar(
                        onFinished = {
                            viewModel.processCommand(SettingsCommand.Back)
                        }
                    )
                }
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = innerPadding
                ) {
                    item(key = 0) {
                        SettingCard(
                            title = stringResource(R.string.search_language),
                            subtitle = stringResource(R.string.select_language_for_news_search)
                        ) {
                            SettingDropdown(
                                options = currentState.languages,
                                selectOption = currentState.language,
                                label = {
                                    it.displayLanguage()
                                },
                                onClick = {
                                    viewModel.processCommand(SettingsCommand.UpdateLanguage(it))
                                }
                            )
                        }
                    }
                    item(1) {
                        SettingCard(
                            title = stringResource(R.string.update_interval),
                            subtitle = stringResource(R.string.how_often_to_update_news)
                        ) {
                            SettingDropdown(
                                options = currentState.intervals,
                                selectOption = currentState.interval,
                                label = {
                                    it.displayInterval()
                                },
                                onClick = {
                                    viewModel.processCommand(SettingsCommand.UpdateInterval(it))
                                }
                            )
                        }
                    }
                    item(2) {
                        SettingCard(
                            title = stringResource(R.string.notifications),
                            subtitle = stringResource(R.string.show_notification_about_new_articles)
                        ) {
                            SettingSwitch(
                                checked = currentState.notificationsEnabled,
                                onCheckedChange = { enabled ->
                                    if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        viewModel.processCommand(
                                            SettingsCommand.UpdateNotificationEnabled(enabled)
                                        )
                                    }
                                }
                            )
                        }
                    }
                    item(3) {
                        SettingCard(
                            title = stringResource(R.string.update_only_via_wi_fi),
                            subtitle = stringResource(R.string.save_mobile_data)
                        ) {
                            SettingSwitch(
                                checked = currentState.wifiOnly,
                                onCheckedChange = {
                                    viewModel.processCommand(SettingsCommand.UpdateWifiOnly(it))
                                }
                            )
                        }
                    }
                }
            }
        }

        is SettingsState.Finished -> {
            LaunchedEffect(key1 = Unit) {
                onFinished()
            }
        }

        is SettingsState.Initial -> {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar(
    modifier: Modifier = Modifier,
    onFinished: () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(text = stringResource(R.string.settings))
        },
        navigationIcon = {
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        onFinished()
                    }
                    .padding(8.dp),
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back)
            )
        }
    )
}

@Composable
private fun SettingCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),

        ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun SettingSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        modifier = modifier,
        checked = checked,
        onCheckedChange = onCheckedChange
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> SettingDropdown(
    options: List<T>,
    selectOption: T,
    label: @Composable (T) -> String,
    onClick: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val textFieldState = rememberTextFieldState(label(selectOption))

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = true
                )
                .fillMaxWidth(),
            state = textFieldState,
            readOnly = true,
            lineLimits = TextFieldLineLimits.SingleLine,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = MenuDefaults.containerColor,
            shape = MenuDefaults.shape
        ) {
            options.forEachIndexed { index, option ->
                val optionLabel = label(option)
                DropdownMenuItem(
                    text = {
                        Text(
                            optionLabel,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    onClick = {
                        textFieldState.setTextAndPlaceCursorAtEnd(optionLabel)
                        onClick(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}