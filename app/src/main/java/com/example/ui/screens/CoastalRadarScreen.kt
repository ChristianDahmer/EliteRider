package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import com.example.data.model.CoastalAirport
import com.example.data.model.CoastalFlightPhase
import com.example.ui.components.CoastalRadarCanvas
import com.example.ui.theme.*
import com.example.viewmodel.LuxeDriveViewModel

@Composable
fun CoastalRadarScreen(
    viewModel: LuxeDriveViewModel,
    onNavigateToBooking: () -> Unit,
    onNavigateToSupport: () -> Unit
) {
    val context = LocalContext.current
    val radarState by viewModel.coastalRadarState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("coastal_radar_screen")
    ) {
        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "RADAR AEROPORTUÁRIO COSTEIRO",
                    color = GoldPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = viewModel.t("coastal_radar_title"),
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
                    Text("RADAR ATIVO", color = AccentGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Luxury Dark Radar Canvas
        CoastalRadarCanvas(
            radarState = radarState,
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Coastal Airport Selector Carousel
        Text(
            text = "AEROPORTOS & HELIPONTOS DO LITORAL",
            color = GoldLight,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CoastalAirport.values().forEach { airport ->
                val isSelected = radarState.selectedCoastalAirport == airport
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            viewModel.setCoastalAirport(airport)
                            Toast.makeText(context, "Radar sintonizado: ${airport.airportName}", Toast.LENGTH_SHORT).show()
                        }
                        .border(
                            1.dp,
                            if (isSelected) GoldPrimary else ObsidianBorder,
                            RoundedCornerShape(12.dp)
                        )
                        .testTag("airport_chip_${airport.code}"),
                    color = if (isSelected) GoldPrimary.copy(alpha = 0.15f) else ObsidianCard
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = airport.code,
                                color = if (isSelected) GoldLight else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "(${airport.icao})",
                                color = if (isSelected) GoldSecondary else TextTertiary,
                                fontSize = 10.sp
                            )
                        }
                        Text(
                            text = airport.region,
                            color = TextSecondary,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Flight Phase & Status Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = ObsidianSurface,
            border = BorderStroke(
                1.dp,
                Color(radarState.flightPhase.badgeColor).copy(alpha = 0.6f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "STATUS DA APROXIMAÇÃO",
                            color = TextTertiary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = radarState.flightPhase.title,
                            color = Color(radarState.flightPhase.badgeColor),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (radarState.delayMinutes > 0) {
                        Surface(
                            color = AccentAmber.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, AccentAmber)
                        ) {
                            Text(
                                text = "+${radarState.delayMinutes} MIN ATRASO",
                                color = AccentAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = radarState.flightPhase.description,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = ObsidianBorder)

                // Technical Weather & Runway Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("CONDIÇÃO METEOROLÓGICA", color = TextTertiary, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Text(radarState.windCondition, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("PISTA HOMOLOGADA", color = TextTertiary, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Text(radarState.selectedCoastalAirport.runwayLength, color = GoldLight, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chauffeur Seaside Meet & Greet Plaque Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = ObsidianSurface,
            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(GoldDark, CircleShape)
                                .border(1.dp, GoldPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = GoldLight, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("RECEPÇÃO VIP NO HANGAR DO LITORAL", color = GoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Chauffeur: ${radarState.assignedChauffeur}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    IconButton(
                        onClick = onNavigateToSupport,
                        modifier = Modifier.testTag("coastal_concierge_btn")
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = "Concierge", tint = GoldPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Digital Executive Plaque Preview
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = ObsidianBackground,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "LUXEDRIVE VIP CHAUFFEUR SERVICE",
                            color = GoldSecondary,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = radarState.meetSignText,
                            color = GoldLight,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Veículo: ${radarState.assignedVehicle}",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Interactive Coastal Routine Simulator Controls
        Text(
            text = "ROTINA DE MONITORAMENTO & SIMULAÇÃO DE VOO",
            color = GoldLight,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Simulate Descent
            OutlinedButton(
                onClick = {
                    viewModel.simulateCoastalDescent()
                    Toast.makeText(context, "Simulação: Aeronave iniciando descida sobre o litoral!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("sim_descent_button"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AccentBlue)
            ) {
                Icon(Icons.Default.FlightLand, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("1. Simular Descida no Espaço Aéreo Costeiro", color = AccentBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            // Simulate Weather Delay
            OutlinedButton(
                onClick = {
                    viewModel.simulateFlightDelay(25)
                    Toast.makeText(context, "Alerta: Atraso de 25m por teto costeiro registrado. Chauffeur notificado!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("sim_delay_button"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AccentAmber)
            ) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("2. Simular Atraso de Voo (+25 min - Teto no Litoral)", color = AccentAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            // Simulate Landing Touchdown
            Button(
                onClick = {
                    viewModel.simulateCoastalLanding()
                    Toast.makeText(context, "Aeronave pousou na pista costeira! Chauffeur a postos no Hangar.", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("sim_landing_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ObsidianBackground, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("3. Simular Pouso na Pista Costeira / Hangar VIP", color = ObsidianBackground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            // Simulate Passenger Met
            Button(
                onClick = {
                    viewModel.simulatePassengerMeet()
                    Toast.makeText(context, "Recepção VIP concluída! Transfer costeiro em andamento.", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("sim_meet_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Icon(Icons.Default.Celebration, contentDescription = null, tint = ObsidianBackground, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("4. Simular Desembarque & Embarque no Blindado", color = ObsidianBackground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Booking Action for Coastal Transfer
        Button(
            onClick = {
                viewModel.updateBookingForm {
                    copy(
                        pickupAddress = "${radarState.selectedCoastalAirport.airportName} (Hangar VIP)",
                        dropoffAddress = "Resort / Condomínio Fechado no Litoral",
                        flightNumber = radarState.flightNumber
                    )
                }
                onNavigateToBooking()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("book_coastal_transfer_btn"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ObsidianCard),
            border = BorderStroke(1.dp, GoldPrimary)
        ) {
            Icon(Icons.Default.AddLocationAlt, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Agendar Transfer para este Aeroporto do Litoral", color = GoldPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}
