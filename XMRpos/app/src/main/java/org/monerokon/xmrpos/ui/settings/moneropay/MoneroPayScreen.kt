// MoneroPayScreen.kt
package org.monerokon.xmrpos.ui.settings.moneropay

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.monerokon.xmrpos.ui.settings.companyinformation.CompanyInformationViewModel
import kotlin.math.exp

@Composable
fun MoneroPayScreenRoot(viewModel: MoneroPayViewModel, navController: NavHostController) {
    viewModel.setNavController(navController)
    MoneroPayScreen(
        onBackClick = viewModel::navigateToMainSettings,
        confOptions = viewModel.confOptions,
        serverAddress = viewModel.serverAddress,
        useCallbacks = viewModel.useCallbacks,
        refreshInterval = viewModel.refreshInterval,
        conf = viewModel.conf,
        updateServerAddress = viewModel::updateServerAddress,
        updateUseCallbacks = viewModel::updateUseCallbacks,
        updateRefreshInterval = viewModel::updateRefreshInterval,
        updateConf = viewModel::updateConf,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoneroPayScreen(
    onBackClick: () -> Unit,
    confOptions: List<String>,
    serverAddress: String,
    useCallbacks: Boolean,
    refreshInterval: String,
    conf: String,
    updateServerAddress: (String) -> Unit,
    updateUseCallbacks: (Boolean) -> Unit,
    updateRefreshInterval: (String) -> Unit,
    updateConf: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                navigationIcon = {
                    IconButton(onClick = {onBackClick()}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Go back to previous screen"
                        )
                    }
                },
                title = {
                    Text("MoneroPay")
                }
            )
        },
    ) { innerPadding ->
        Column (
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 32.dp, vertical = 24.dp)
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextField(
                    value = serverAddress,
                    onValueChange = {updateServerAddress(it)},
                    label = { Text("Server address") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                FilledTonalButton (onClick = {}) {
                    Text("Test")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Use callbacks to this device", style = MaterialTheme.typography.labelLarge)
                Switch(checked = useCallbacks, onCheckedChange = {updateUseCallbacks(it)})
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Request interval", style = MaterialTheme.typography.labelLarge)
                TextField(
                    value = refreshInterval,
                    onValueChange = {updateRefreshInterval(it)},
                    label = { Text("Seconds") },
                    modifier = Modifier.width(130.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            ConfSelector(conf, confOptions, onConfSelected = {updateConf(it)}, modifier = Modifier.width(130.dp))
        }}
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfSelector(value: String, confs: List<String>, onConfSelected: (String) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Number of confirmations\nto mark as paid", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {expanded = !expanded}, modifier = modifier) {
                TextField(
                    modifier = Modifier.menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth(),
                    value = value,
                    enabled = true,
                    label = { Text("Conf") },
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded,
                        )
                    }
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = {expanded = false}) {
                    confs.forEach { conf ->
                        DropdownMenuItem(
                            text = { Text(conf) },
                            onClick = {
                                expanded = false
                                onConfSelected(conf)
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }
    }
}
