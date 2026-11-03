package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entities.TripBookingEntity
import com.example.data.model.VehicleCategory
import com.example.ui.theme.*
import com.example.viewmodel.LuxeDriveViewModel

@Composable
fun HomeScreen(
    viewModel: LuxeDriveViewModel,
    activeTrip: TripBookingEntity?,
    onNavigateToBooking: () -> Unit,
    onNavigateToTracking: () -> Unit,
    onNavigateToRadar: () -> Unit,
    onNavigateToFleet: () -> Unit,
    onNavigateToFinancial: () -> Unit,
    onNavigateToSupport: () -> Unit
) {
    val scrollState = rememberScrollState()
    val coastalRadar by viewModel.coastalRadarState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .verticalScroll(scrollState)
            .testTag("home_screen")
    ) {
        // Hero Card with Generated Luxury Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_limo),
                contentDescription = "Executive Luxury Limo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient scrim overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                ObsidianBackground.copy(alpha = 0.6f),
                                ObsidianBackground
                            )
                        )
                    )
            )

            // Hero Text
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Surface(
                    color = GoldDark.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, GoldPrimary)
                ) {
                    Text(
                        text = "PADRÃO DIAMANTE • 24/7",
                        color = GoldLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = viewModel.t("hero_vip_welcome"),
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = viewModel.t("hero_vip_subtitle"),
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }

        // Active Trip Alert Banner (if any)
        if (activeTrip != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigateToTracking() }
                    .border(1.dp, GoldPrimary, RoundedCornerShape(16.dp))
                    .testTag("active_ride_card"),
                color = ObsidianSurface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(GoldPrimary.copy(alpha = 0.2f), CircleShape)
                                .border(1.dp, GoldPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = viewModel.t("active_ride"),
                                    color = GoldPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(modifier = Modifier.size(6.dp).background(AccentGreen, CircleShape))
                            }
                            Text(
                                text = "${activeTrip.vehicleModel} • ${activeTrip.chauffeurName}",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Para: ${activeTrip.dropoffAddress}",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }

                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Acompanhar",
                        tint = GoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Coastal Flight Radar Feature Banner
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { onNavigateToRadar() }
                .border(1.dp, AccentBlue.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                .testTag("coastal_radar_home_card"),
            color = ObsidianSurfaceVariant
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(AccentBlue.copy(alpha = 0.2f), CircleShape)
                            .border(1.dp, AccentBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.FlightLand,
                            contentDescription = "Radar Litoral",
                            tint = AccentBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "RADAR DO LITORAL & POUSOS",
                                color = AccentBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(color = AccentGreen.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                Text(
                                    text = "1090 MHz",
                                    color = AccentGreen,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "${coastalRadar.flightNumber} • ${coastalRadar.selectedCoastalAirport.code} (${coastalRadar.selectedCoastalAirport.region.split(" • ")[0]})",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${coastalRadar.flightPhase.title} • ETA: ${coastalRadar.etaMinutes} min",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                Icon(
                    Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Abrir Radar",
                    tint = AccentBlue,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Enterprise Fleet Telematics Shortcut Banner (Sascar, Autotrac, Cobli, Geotab, Wialon, Omnilink)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { onNavigateToFleet() }
                .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
            color = ObsidianSurface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(GoldPrimary.copy(alpha = 0.15f), CircleShape)
                            .border(1.dp, GoldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Sensors,
                            contentDescription = "Telemetria de Mercado",
                            tint = GoldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "RASTREAMENTO MULTISSISTEMAS",
                                color = GoldLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(color = AccentGreen.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                Text(
                                    text = "CAN-BUS ATIVO",
                                    color = AccentGreen,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "Sascar • Autotrac • Cobli • Geotab • Wialon • Omnilink",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Telemetria avançada, corte de ignição, anti-jammer & score",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                Icon(
                    Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Abrir Telemetria",
                    tint = GoldPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Quick Booking Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    viewModel.updateBookingForm { copy(isScheduled = false) }
                    onNavigateToBooking()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("quick_book_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = ObsidianBackground, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(viewModel.t("quick_book_now"), color = ObsidianBackground, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            OutlinedButton(
                onClick = {
                    viewModel.updateBookingForm { copy(isScheduled = true) }
                    onNavigateToBooking()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("schedule_transfer_button"),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, GoldPrimary)
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(viewModel.t("schedule_transfer"), color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Luxury Fleet Showcase
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FROTA EXECUTIVA & BLINDADOS",
                    color = GoldLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                TextButton(onClick = onNavigateToFleet) {
                    Text("Gerenciar Frota", color = GoldPrimary, fontSize = 12.sp)
                }
            }

            // Fleet horizontal row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                VehicleCategory.values().forEach { cat ->
                    Surface(
                        modifier = Modifier
                            .width(230.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                viewModel.updateBookingForm { copy(selectedCategory = cat, isArmored = cat.isArmoredDefault) }
                                onNavigateToBooking()
                            }
                            .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp)),
                        color = ObsidianCard
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = if (cat.isArmoredDefault) ArmoredBadgeColor.copy(alpha = 0.2f) else ObsidianSurfaceVariant,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (cat.isArmoredDefault) "BLINDADO III-A" else "FIRST CLASS",
                                        color = if (cat.isArmoredDefault) ArmoredBadgeColor else GoldSecondary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Text(
                                    text = "R$ " + cat.basePrice.toInt(),
                                    color = GoldPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = viewModel.t(cat.titleKey),
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = cat.sampleModel,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("${cat.passengerCapacity} VIPs", color = TextTertiary, fontSize = 11.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Luggage, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("${cat.luggageCapacity} malas", color = TextTertiary, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // VIP Services Card Showcase
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(18.dp))
                .border(1.dp, ObsidianBorder, RoundedCornerShape(18.dp)),
            color = ObsidianSurface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "EXPERIÊNCIA A BORDO",
                            color = GoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Atendimento Executivo Personalizado",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    IconButton(
                        onClick = onNavigateToSupport,
                        modifier = Modifier.testTag("concierge_quick_button")
                    ) {
                        Icon(Icons.Default.HeadsetMic, contentDescription = "Concierge", tint = GoldPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    VipFeatureBadge(Icons.Default.FlightTakeoff, "Radar Voo", "Ajuste automático")
                    VipFeatureBadge(Icons.Default.LocalDrink, "Champagne", "Moët & Chandon")
                    VipFeatureBadge(Icons.Default.Shield, "Blindagem", "Nível III-A")
                    VipFeatureBadge(Icons.Default.Translate, "Poliglota", "Chauffeurs bilíngues")
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Quick Admin & Finance Shortcut
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onNavigateToFinancial() }
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(14.dp)),
                color = ObsidianCard
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(AccentGreen.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Finanças VIP", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Relatórios & NF-e", color = TextSecondary, fontSize = 10.sp)
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onNavigateToFleet() }
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(14.dp)),
                color = ObsidianCard
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(GoldPrimary.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Gestão Frota", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("4 Veículos Ativos", color = TextSecondary, fontSize = 10.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun VipFeatureBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(74.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(ObsidianCard, CircleShape)
                .border(1.dp, ObsidianBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(title, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = TextTertiary, fontSize = 9.sp, maxLines = 1)
    }
}
