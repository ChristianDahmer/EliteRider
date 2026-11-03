package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.viewmodel.LuxeDriveViewModel

@Composable
fun MarketTelematicsHubHeader(
    selectedFilter: TelematicsSystemProvider?,
    onFilterSelected: (TelematicsSystemProvider?) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, GoldPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
        color = ObsidianSurface
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(GoldPrimary.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "HUB DE MONITORAMENTO DE MERCADO",
                            color = GoldPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Compatibilidade Multi-Sistemas & CAN-Bus",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    color = AccentGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, AccentGreen.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "7 PROTOCOLOS HOMOLOGADOS",
                        color = AccentGreen,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Integração bidirecional com os principais players de telemetria e gerenciamento de risco do Brasil e do mundo: Sascar, Autotrac, Cobli, Geotab, Wialon (Gurtam), Traccar e Omnilink (Pamcary/Buonny).",
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips
            Text(
                text = "Filtrar por Sistema Ativo:",
                color = TextTertiary,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    ProviderFilterChip(
                        label = "Todos (${TelematicsSystemProvider.entries.size})",
                        isSelected = selectedFilter == null,
                        onClick = { onFilterSelected(null) }
                    )
                }
                items(TelematicsSystemProvider.entries) { provider ->
                    ProviderFilterChip(
                        label = provider.displayName,
                        isSelected = selectedFilter == provider,
                        onClick = { onFilterSelected(provider) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProviderFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = if (isSelected) GoldPrimary else ObsidianCard,
        border = BorderStroke(1.dp, if (isSelected) GoldPrimary else ObsidianBorder)
    ) {
        Text(
            text = label,
            color = if (isSelected) ObsidianBackground else TextSecondary,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun VehicleTrackingCard(
    vehicle: VehicleTrackingData,
    onOpenDetails: (VehicleTrackingData) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp)),
        color = ObsidianSurface
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Vehicle Title & Provider Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = vehicle.plate,
                            color = GoldLight,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(vehicle.ignitionStatus.colorHex), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = vehicle.ignitionStatus.label,
                            color = Color(vehicle.ignitionStatus.colorHex),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = vehicle.modelName,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    color = GoldDark.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = vehicle.provider.displayName,
                        color = GoldLight,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Hardware & Chauffeur Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Chauffeur: ${vehicle.assignedChauffeur}",
                    color = TextTertiary,
                    fontSize = 10.sp
                )
                Text(
                    text = "Hardware: ${vehicle.hardwareModel}",
                    color = AccentBlue,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // CAN-Bus Telemetry Data Grid
            Surface(
                color = ObsidianCard,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, ObsidianBorder.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TelemetricPill("Velocidade", "${vehicle.speedKmH} km/h", Icons.Default.Speed, GoldPrimary)
                        TelemetricPill("RPM Motor", "${vehicle.engineRpm}", Icons.Default.SettingsSuggest, AccentAmber)
                        TelemetricPill("Odômetro", "${vehicle.odometerKm} km", Icons.Default.LinearScale, TextPrimary)
                        TelemetricPill("Bateria ECU", "${vehicle.batteryVoltage}V", Icons.Default.BatteryChargingFull, AccentGreen)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TelemetricPill("Nível Tanque", "${vehicle.fuelOrBatteryPercent}%", Icons.Default.LocalGasStation, GoldLight)
                        TelemetricPill("Temp Motor", "${vehicle.coolantTempC}°C", Icons.Default.Thermostat, AccentAmber)
                        TelemetricPill("Satélites GPS", "${vehicle.satellites} (HDOP ${vehicle.hdop})", Icons.Default.GpsFixed, AccentBlue)
                        TelemetricPill("Eco Score", "${vehicle.ecoDrivingScore}/100", Icons.Default.Star, AccentGreen)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Safety & Risk Manager Indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Anti-Jammer Badge
                Surface(
                    color = if (vehicle.antiJammerState == AntiJammerState.NORMAL) AccentGreen.copy(alpha = 0.12f) else AccentRed.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, if (vehicle.antiJammerState == AntiJammerState.NORMAL) AccentGreen.copy(alpha = 0.3f) else AccentRed),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                            tint = Color(vehicle.antiJammerState.colorHex),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (vehicle.antiJammerState == AntiJammerState.NORMAL) "Anti-Jammer OK" else "ALERTA JAMMER",
                            color = Color(vehicle.antiJammerState.colorHex),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Remote Lock Badge
                Surface(
                    color = if (vehicle.lockState == RemoteLockState.UNLOCKED) AccentGreen.copy(alpha = 0.12f) else AccentRed.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, if (vehicle.lockState == RemoteLockState.UNLOCKED) AccentGreen.copy(alpha = 0.3f) else AccentRed),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (vehicle.lockState == RemoteLockState.UNLOCKED) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(vehicle.lockState.colorHex),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = vehicle.lockState.label,
                            color = Color(vehicle.lockState.colorHex),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action: Details & Remote Commands
            Button(
                onClick = { onOpenDetails(vehicle) },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .testTag("telematics_details_btn_${vehicle.plate}")
            ) {
                Icon(
                    Icons.Default.Tune,
                    contentDescription = null,
                    tint = ObsidianBackground,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Comandos & Telemetria Avançada",
                    color = ObsidianBackground,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TelemetricPill(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(10.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text(label, color = TextTertiary, fontSize = 8.sp)
        }
        Text(value, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TelematicsDetailsDialog(
    vehicle: VehicleTrackingData,
    viewModel: LuxeDriveViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var macroInput by remember { mutableStateOf("02 - No Hangar VIP Aguardando Desembarque") }
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, GoldPrimary, RoundedCornerShape(20.dp)),
            color = ObsidianSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TELEMETRIA DE MERCADO • ${vehicle.plate}",
                            color = GoldPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = vehicle.modelName,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 1: Switch Provider Protocol
                Text(
                    text = "SELECIONAR SISTEMA DE MONITORAMENTO",
                    color = GoldLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Troque o protocolo ativo para testar a comunicação com a central de rastreamento:",
                    color = TextSecondary,
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(TelematicsSystemProvider.entries) { prov ->
                        val isCurrent = vehicle.provider == prov
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    if (!isCurrent) {
                                        viewModel.switchVehicleProvider(vehicle.vehicleId, prov)
                                        Toast.makeText(
                                            context,
                                            "Protocolo alterado para ${prov.displayName}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                            color = if (isCurrent) GoldPrimary else ObsidianCard,
                            border = BorderStroke(1.dp, if (isCurrent) GoldLight else ObsidianBorder)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                                Text(
                                    text = prov.displayName,
                                    color = if (isCurrent) ObsidianBackground else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = prov.shortCode,
                                    color = if (isCurrent) ObsidianBackground.copy(alpha = 0.8f) else GoldLight,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Remote Control Commands
                Text(
                    text = "COMANDOS REMOTOS (TELEMETRIA ATIVA)",
                    color = GoldLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Command Buttons Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Lock Toggle Button
                    OutlinedButton(
                        onClick = {
                            viewModel.executeTrackingCommand(vehicle.vehicleId, TelemetryCommand.TOGGLE_LOCK)
                            Toast.makeText(
                                context,
                                if (vehicle.lockState == RemoteLockState.UNLOCKED) "Comando de corte de ignição enviado!" else "Comando de liberação enviado!",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (vehicle.lockState == RemoteLockState.LOCKED) AccentRed.copy(alpha = 0.15f) else Color.Transparent
                        ),
                        border = BorderStroke(1.dp, if (vehicle.lockState == RemoteLockState.LOCKED) AccentRed else GoldPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            if (vehicle.lockState == RemoteLockState.UNLOCKED) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = null,
                            tint = if (vehicle.lockState == RemoteLockState.LOCKED) AccentRed else GoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (vehicle.lockState == RemoteLockState.UNLOCKED) "Bloquear Ignição" else "Liberar Ignição",
                            color = if (vehicle.lockState == RemoteLockState.LOCKED) AccentRed else TextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Anti-Jammer Test Button
                    OutlinedButton(
                        onClick = {
                            viewModel.executeTrackingCommand(vehicle.vehicleId, TelemetryCommand.TOGGLE_ANTI_JAMMER)
                            Toast.makeText(
                                context,
                                if (vehicle.antiJammerState == AntiJammerState.NORMAL) "Simulação Anti-Jammer: Bloqueador Detectado!" else "Anti-Jammer Seguro",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (vehicle.antiJammerState == AntiJammerState.JAMMING_DETECTED) AccentRed.copy(alpha = 0.15f) else Color.Transparent
                        ),
                        border = BorderStroke(1.dp, if (vehicle.antiJammerState == AntiJammerState.JAMMING_DETECTED) AccentRed else AccentGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                            tint = if (vehicle.antiJammerState == AntiJammerState.JAMMING_DETECTED) AccentRed else AccentGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Teste Jammer",
                            color = if (vehicle.antiJammerState == AntiJammerState.JAMMING_DETECTED) AccentRed else TextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Panic SOS Test Button
                    OutlinedButton(
                        onClick = {
                            viewModel.executeTrackingCommand(vehicle.vehicleId, TelemetryCommand.TRIGGER_PANIC_TEST)
                            Toast.makeText(context, "Sinal de Pânico SOS transmitido!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (vehicle.isPanicAlarmActive) AccentRed.copy(alpha = 0.2f) else Color.Transparent
                        ),
                        border = BorderStroke(1.dp, if (vehicle.isPanicAlarmActive) AccentRed else AccentAmber),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pânico SOS", color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    // Ping GPS Button
                    OutlinedButton(
                        onClick = {
                            viewModel.executeTrackingCommand(vehicle.vehicleId, TelemetryCommand.PING_LOCATION)
                            Toast.makeText(context, "Ping GPS 1-Segundo concluído!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, AccentBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ping GPS", color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    // CAN Refresh Button
                    OutlinedButton(
                        onClick = {
                            viewModel.executeTrackingCommand(vehicle.vehicleId, TelemetryCommand.REFRESH_CAN)
                            Toast.makeText(context, "Varredura CAN-Bus ECU atualizada!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, GoldPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Sensors, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Scan CAN", color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 3: Send Travel Macro (Autotrac / Omnilink standard)
                Text(
                    text = "TRANSMISSÃO DE MACRO DE VIAGEM",
                    color = GoldLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = macroInput,
                        onValueChange = { macroInput = it },
                        modifier = Modifier.weight(1f),
                        textStyle = LocalTextStyle.current.copy(fontSize = 11.sp, color = TextPrimary),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedContainerColor = ObsidianCard,
                            unfocusedContainerColor = ObsidianCard
                        ),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            viewModel.executeTrackingCommand(vehicle.vehicleId, TelemetryCommand.SEND_MACRO, macroInput)
                            Toast.makeText(context, "Macro transmitida com sucesso!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = ObsidianBackground, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 4: Raw Telemetry Packet / Webhook Payload
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PACOTE DE DADOS / DATAGRAMA BRUTO",
                            color = GoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Padrão: ${vehicle.provider.protocolName}",
                            color = TextTertiary,
                            fontSize = 9.sp
                        )
                    }

                    TextButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Telemetria", vehicle.rawPacketPayload)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Datagrama copiado para a área de transferência!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copiar", color = GoldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    color = Color(0xFF090A0D),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = vehicle.rawPacketPayload,
                        color = AccentGreen,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 5: Telemetry Event Log History
                Text(
                    text = "HISTÓRICO RECENTE DE EVENTOS DE TELEMETRIA",
                    color = GoldLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    vehicle.eventLogs.forEach { log ->
                        Surface(
                            color = ObsidianCard,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, ObsidianBorder.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = when (log.severity) {
                                        "SUCCESS" -> AccentGreen.copy(alpha = 0.2f)
                                        "WARN" -> AccentAmber.copy(alpha = 0.2f)
                                        "DANGER" -> AccentRed.copy(alpha = 0.2f)
                                        else -> AccentBlue.copy(alpha = 0.2f)
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = log.providerTag,
                                        color = when (log.severity) {
                                            "SUCCESS" -> AccentGreen
                                            "WARN" -> AccentAmber
                                            "DANGER" -> AccentRed
                                            else -> AccentBlue
                                        },
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(log.title, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(log.detail, color = TextSecondary, fontSize = 10.sp)
                                }

                                Text(log.time, color = TextTertiary, fontSize = 9.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
