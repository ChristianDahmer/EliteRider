package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.TripBookingEntity
import com.example.ui.theme.*
import com.example.viewmodel.LuxeDriveViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TripHistoryScreen(
    viewModel: LuxeDriveViewModel,
    trips: List<TripBookingEntity>,
    onOpenReceipt: (TripBookingEntity) -> Unit,
    onOpenRating: (TripBookingEntity) -> Unit,
    onRebook: (TripBookingEntity) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy • HH:mm", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(16.dp)
            .testTag("trip_history_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "HISTÓRICO EXECUTIVO",
                    color = GoldPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = viewModel.t("trip_history_title"),
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                color = ObsidianSurfaceVariant,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, ObsidianBorder)
            ) {
                Text(
                    text = "${trips.size} Transfers",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (trips.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Nenhuma viagem registrada no histórico.", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(trips, key = { it.id }) { trip ->
                    TripHistoryCard(
                        trip = trip,
                        formattedDate = dateFormat.format(Date(trip.scheduledEpochMillis)),
                        onOpenReceipt = { onOpenReceipt(trip) },
                        onOpenRating = { onOpenRating(trip) },
                        onRebook = { onRebook(trip) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TripHistoryCard(
    trip: TripBookingEntity,
    formattedDate: String,
    onOpenReceipt: () -> Unit,
    onOpenRating: () -> Unit,
    onRebook: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
            .testTag("trip_card_${trip.id}"),
        color = ObsidianSurface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Card top: Code + Date + Status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(trip.bookingCode, color = GoldLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(formattedDate, color = TextTertiary, fontSize = 10.sp)
                }

                Surface(
                    color = when (trip.tripStatus) {
                        "COMPLETED" -> AccentGreen.copy(alpha = 0.15f)
                        "EN_ROUTE", "IN_PROGRESS" -> GoldPrimary.copy(alpha = 0.15f)
                        else -> AccentBlue.copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(
                        1.dp,
                        when (trip.tripStatus) {
                            "COMPLETED" -> AccentGreen
                            "EN_ROUTE", "IN_PROGRESS" -> GoldPrimary
                            else -> AccentBlue
                        }
                    )
                ) {
                    Text(
                        text = when (trip.tripStatus) {
                            "COMPLETED" -> "CONCLUÍDO"
                            "EN_ROUTE" -> "A CAMINHO"
                            "ARRIVED" -> "NO LOCAL"
                            "IN_PROGRESS" -> "EM TRÂNSITO"
                            else -> "AGENDADO"
                        },
                        color = when (trip.tripStatus) {
                            "COMPLETED" -> AccentGreen
                            "EN_ROUTE", "IN_PROGRESS" -> GoldPrimary
                            else -> AccentBlue
                        },
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = ObsidianBorder)

            // Addresses
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.TripOrigin, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(trip.pickupAddress, color = TextPrimary, fontSize = 12.sp, maxLines = 1)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(trip.dropoffAddress, color = TextPrimary, fontSize = 12.sp, maxLines = 1)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Vehicle and Chauffeur
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("${trip.vehicleModel} • ${trip.vehiclePlate}", color = TextSecondary, fontSize = 11.sp)
                    Text("Chauffeur: ${trip.chauffeurName}", color = TextTertiary, fontSize = 10.sp)
                }

                Text(
                    text = "R$ " + String.format(Locale.US, "%.2f", trip.totalPrice),
                    color = GoldPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Rating banner if already rated
            if (trip.ratingGiven != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = ObsidianBackground,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..trip.ratingGiven) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(12.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = trip.reviewFeedback ?: "Avaliação 5 Estrelas",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenReceipt,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("receipt_btn_${trip.id}"),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, GoldPrimary)
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Nota Fiscal", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                if (trip.ratingGiven == null && trip.tripStatus == "COMPLETED") {
                    Button(
                        onClick = onOpenRating,
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("rate_btn_${trip.id}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = ObsidianBackground, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Avaliar", color = ObsidianBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onRebook,
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("rebook_btn_${trip.id}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceVariant)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Repetir", color = TextPrimary, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
