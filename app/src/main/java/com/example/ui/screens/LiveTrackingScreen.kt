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
import com.example.data.local.entities.TripBookingEntity
import com.example.ui.components.LuxuryMapCanvas
import com.example.ui.theme.*
import com.example.viewmodel.LuxeDriveViewModel

@Composable
fun LiveTrackingScreen(
    viewModel: LuxeDriveViewModel,
    activeTrip: TripBookingEntity?,
    onNavigateToBooking: () -> Unit,
    onNavigateToSupport: () -> Unit
) {
    val context = LocalContext.current
    val telematics by viewModel.telematics.collectAsState()

    if (activeTrip == null) {
        // Empty State: No active ride
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBackground)
                .padding(24.dp)
                .testTag("empty_tracking_view"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(ObsidianSurface, CircleShape)
                    .border(1.dp, GoldPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = GoldPrimary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "NENHUMA CORRIDA EM ANDAMENTO",
                color = GoldPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Solicite ou agende um transfer executivo para acompanhar o trajeto e a telemetria ao vivo.",
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onNavigateToBooking,
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("request_transfer_empty_button")
            ) {
                Icon(Icons.Default.AddLocation, contentDescription = null, tint = ObsidianBackground, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Solicitar Novo Transfer", color = ObsidianBackground, fontWeight = FontWeight.Bold)
            }
        }
    } else {
        // Active Tracking View
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBackground)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .testTag("active_tracking_view")
        ) {
            // Status Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "STATUS DO TRANSFER VIP",
                        color = GoldPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = when (activeTrip.tripStatus) {
                            "SCHEDULED" -> "Reserva Agendada"
                            "EN_ROUTE" -> "Chauffeur a Caminho"
                            "ARRIVED" -> "Chauffeur no Local"
                            "IN_PROGRESS" -> "Em Deslocamento VIP"
                            else -> "Transfer Concluído"
                        },
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = ObsidianCard,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = activeTrip.bookingCode,
                        color = GoldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Luxury Dark GPS Map Canvas
            LuxuryMapCanvas(
                telematics = telematics,
                pickupText = activeTrip.pickupAddress,
                dropoffText = activeTrip.dropoffAddress,
                isArmored = activeTrip.isArmored,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Chauffeur & Vehicle Profile Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = ObsidianSurface,
                border = BorderStroke(1.dp, ObsidianBorder)
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
                                    .size(50.dp)
                                    .background(GoldDark, CircleShape)
                                    .border(1.5.dp, GoldPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = activeTrip.chauffeurName.split(" ").map { it.first() }.take(2).joinToString(""),
                                    color = GoldLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = activeTrip.chauffeurName,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${activeTrip.chauffeurRating} • Chauffeur Executivo",
                                        color = GoldLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        // Call Chauffeur Button
                        IconButton(
                            onClick = {
                                Toast.makeText(context, "Ligando para ${activeTrip.chauffeurName} (${activeTrip.chauffeurPhone})", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .background(GoldPrimary.copy(alpha = 0.15f), CircleShape)
                                .border(1.dp, GoldPrimary, CircleShape)
                                .testTag("call_chauffeur_button")
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Ligar", tint = GoldPrimary, modifier = Modifier.size(20.dp))
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = ObsidianBorder)

                    // Vehicle info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("VEÍCULO OFICIAL", color = TextTertiary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(activeTrip.vehicleModel, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        Surface(
                            color = ObsidianBackground,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, ObsidianBorder)
                        ) {
                            Text(
                                text = activeTrip.vehiclePlate,
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Quick Buttons: Concierge Desk & SOS Security
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToSupport,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("chat_concierge_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, ObsidianBorder)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Chat Concierge", color = TextPrimary, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        Toast.makeText(context, "Alerta SOS transmitido para a Central de Segurança VIP.", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("sos_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed.copy(alpha = 0.2f)),
                    border = BorderStroke(1.dp, AccentRed)
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = AccentRed, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SOS Segurança", color = AccentRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Trip Simulation Action
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = ObsidianCard,
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("SIMULADOR DE TELEMETRIA", color = GoldLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("Avançar status da corrida em tempo real", color = TextSecondary, fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            viewModel.progressActiveTripStatus()
                            Toast.makeText(context, "Status da corrida atualizado!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("advance_status_button")
                    ) {
                        Text("Avançar Status", color = ObsidianBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
