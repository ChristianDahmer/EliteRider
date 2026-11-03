package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ChauffeurEntity
import com.example.data.local.entities.FleetInventoryEntity
import com.example.data.local.entities.FleetVehicleEntity
import com.example.data.model.TelematicsSystemProvider
import com.example.data.model.VehicleTrackingData
import com.example.ui.components.MarketTelematicsHubHeader
import com.example.ui.components.TelematicsDetailsDialog
import com.example.ui.components.VehicleTrackingCard
import com.example.ui.theme.*
import com.example.viewmodel.LuxeDriveViewModel

@Composable
fun FleetAdminScreen(
    viewModel: LuxeDriveViewModel,
    vehicles: List<FleetVehicleEntity>,
    chauffeurs: List<ChauffeurEntity>,
    inventory: List<FleetInventoryEntity>
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) } // 0: Telemetria de Mercado, 1: Veículos, 2: Chauffeurs, 3: Inventário

    val fleetTrackingList by viewModel.fleetTrackingList.collectAsState()
    val selectedTrackingVehicle by viewModel.selectedTrackingVehicle.collectAsState()
    val selectedProviderFilter by viewModel.selectedProviderFilter.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(16.dp)
            .testTag("fleet_admin_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PAINEL ADMINISTRATIVO & TELEMETRIA",
                    color = GoldPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = viewModel.t("fleet_admin_title"),
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                color = AccentGreen.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, AccentGreen)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(6.dp).background(AccentGreen, CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("TELEMETRIA ONLINE", color = AccentGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick KPI Stats Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                title = "Total Frota",
                value = vehicles.size.toString(),
                subtitle = "Veículos VIP",
                modifier = Modifier.weight(1f),
                icon = Icons.Default.DirectionsCar,
                tint = GoldPrimary
            )
            StatCard(
                title = "Rastreamento",
                value = fleetTrackingList.size.toString(),
                subtitle = "Multissistemas",
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Sensors,
                tint = AccentGreen
            )
            StatCard(
                title = "Em Corrida",
                value = vehicles.count { it.status == "IN_SERVICE" }.toString(),
                subtitle = "Em trânsito",
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Navigation,
                tint = AccentBlue
            )
            StatCard(
                title = "Chauffeurs",
                value = chauffeurs.size.toString(),
                subtitle = "Certificados",
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Badge,
                tint = AccentPurple
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Sub Tabs: Telemetria (Mercado), Veículos, Chauffeurs, Inventário
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ObsidianSurface, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AdminTabButton("📡 Telemetria", selectedTab == 0, Modifier.weight(1f)) { selectedTab = 0 }
            AdminTabButton("Frota (${vehicles.size})", selectedTab == 1, Modifier.weight(1f)) { selectedTab = 1 }
            AdminTabButton("Chauffeurs (${chauffeurs.size})", selectedTab == 2, Modifier.weight(1f)) { selectedTab = 2 }
            AdminTabButton("Estoque", selectedTab == 3, Modifier.weight(1f)) { selectedTab = 3 }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedTab) {
            0 -> TelematicsMarketTab(
                trackingList = fleetTrackingList,
                selectedFilter = selectedProviderFilter,
                onFilterSelected = { viewModel.setProviderFilter(it) },
                onSelectVehicle = { viewModel.selectTrackingVehicle(it) }
            )
            1 -> VehiclesTab(vehicles, viewModel)
            2 -> ChauffeursTab(chauffeurs)
            3 -> InventoryTab(inventory, viewModel)
        }
    }

    // Telematics details modal dialog with remote commands and protocol viewer
    selectedTrackingVehicle?.let { vehicle ->
        TelematicsDetailsDialog(
            vehicle = vehicle,
            viewModel = viewModel,
            onDismiss = { viewModel.selectTrackingVehicle(null) }
        )
    }
}

@Composable
private fun TelematicsMarketTab(
    trackingList: List<VehicleTrackingData>,
    selectedFilter: TelematicsSystemProvider?,
    onFilterSelected: (TelematicsSystemProvider?) -> Unit,
    onSelectVehicle: (VehicleTrackingData) -> Unit
) {
    val filteredList = remember(trackingList, selectedFilter) {
        if (selectedFilter == null) trackingList else trackingList.filter { it.provider == selectedFilter }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Market Hub Header with Providers and Filter Chips
        MarketTelematicsHubHeader(
            selectedFilter = selectedFilter,
            onFilterSelected = onFilterSelected
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "VEÍCULOS MONITORADOS (${filteredList.size})",
                color = GoldLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "Atualização a cada 4s",
                color = AccentGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (filteredList.isEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                color = ObsidianSurface
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.SearchOff, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Nenhum veículo ativo neste sistema", color = TextSecondary, fontSize = 12.sp)
                }
            }
        } else {
            filteredList.forEach { v ->
                VehicleTrackingCard(
                    vehicle = v,
                    onOpenDetails = onSelectVehicle
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun AdminTabButton(title: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = if (isSelected) GoldPrimary else Color.Transparent
    ) {
        Text(
            text = title,
            color = if (isSelected) ObsidianBackground else TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun VehiclesTab(vehicles: List<FleetVehicleEntity>, viewModel: LuxeDriveViewModel) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        vehicles.forEach { vehicle ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp)),
                color = ObsidianSurface
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(vehicle.modelName, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Placa: ${vehicle.plate} • ${vehicle.year} • ${vehicle.color}", color = TextSecondary, fontSize = 11.sp)
                        }

                        Surface(
                            color = when (vehicle.status) {
                                "AVAILABLE" -> AccentGreen.copy(alpha = 0.15f)
                                "IN_SERVICE" -> AccentBlue.copy(alpha = 0.15f)
                                else -> AccentAmber.copy(alpha = 0.15f)
                            },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = when (vehicle.status) {
                                    "AVAILABLE" -> "DISPONÍVEL"
                                    "IN_SERVICE" -> "EM CORRIDA"
                                    "MAINTENANCE" -> "MANUTENÇÃO"
                                    else -> vehicle.status
                                },
                                color = when (vehicle.status) {
                                    "AVAILABLE" -> AccentGreen
                                    "IN_SERVICE" -> AccentBlue
                                    else -> AccentAmber
                                },
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Chauffeur: ${vehicle.assignedChauffeur}", color = TextTertiary, fontSize = 11.sp)
                        Text("Bateria/Combustível: ${vehicle.fuelOrBatteryPercent}%", color = GoldLight, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    if (vehicle.isArmored) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("🛡️ ${vehicle.ballisticLevel ?: "Blindagem Nível III-A"}", color = ArmoredBadgeColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick status change buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.updateVehicleStatus(vehicle.id, "AVAILABLE")
                                Toast.makeText(context, "${vehicle.plate} definido como DISPONÍVEL", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f).height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Disponível", fontSize = 10.sp, color = TextPrimary)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.updateVehicleStatus(vehicle.id, "MAINTENANCE")
                                Toast.makeText(context, "${vehicle.plate} enviado para REVISÃO", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f).height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Revisão", fontSize = 10.sp, color = AccentAmber)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ChauffeursTab(chauffeurs: List<ChauffeurEntity>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        chauffeurs.forEach { ch ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp)),
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
                                    .size(38.dp)
                                    .background(GoldDark, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(ch.badgeNumber, color = GoldLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(ch.name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("${ch.experienceYears} anos de experiência • ${ch.tripsCompleted} viagens", color = TextSecondary, fontSize = 10.sp)
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(ch.rating.toString(), color = GoldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("🗣️ Idiomas: ${ch.languages}", color = TextSecondary, fontSize = 11.sp)
                    Text("🎖️ Certificação: ${ch.securityCertification}", color = TextTertiary, fontSize = 10.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun InventoryTab(inventory: List<FleetInventoryEntity>, viewModel: LuxeDriveViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        inventory.forEach { item ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(14.dp)),
                color = ObsidianSurface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.itemName, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Estoque Mínimo: ${item.minThreshold} ${item.unit}", color = TextTertiary, fontSize = 10.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                if (item.quantityInStock > 0) {
                                    viewModel.updateInventoryQty(item.id, item.quantityInStock - 1)
                                }
                            },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Diminuir", tint = TextSecondary)
                        }

                        Text(
                            text = "${item.quantityInStock} ${item.unit}",
                            color = if (item.quantityInStock <= item.minThreshold) AccentRed else GoldLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )

                        IconButton(
                            onClick = {
                                viewModel.updateInventoryQty(item.id, item.quantityInStock + 1)
                            },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(Icons.Default.AddCircleOutline, contentDescription = "Aumentar", tint = GoldPrimary)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, ObsidianBorder, RoundedCornerShape(14.dp)),
        color = ObsidianSurface
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = TextTertiary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Text(subtitle, color = TextSecondary, fontSize = 9.sp)
        }
    }
}
