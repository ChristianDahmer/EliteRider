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
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.viewmodel.LuxeDriveViewModel
import java.util.Locale

@Composable
fun BookingScreen(
    viewModel: LuxeDriveViewModel,
    onBookingSuccess: () -> Unit
) {
    val context = LocalContext.current
    val form by viewModel.bookingForm.collectAsState()
    val scrollState = rememberScrollState()

    val totalFare = viewModel.calculateBookingTotal(form)

    val executiveLocations = listOf(
        "Hotel Fasano Jardins - R. Vittorio Fasano, 88",
        "Aeroporto GRU Terminal 3 VIP - Rod. Hélio Smidt",
        "Faria Lima Financial Center - Av. Brg. Faria Lima, 3477",
        "Hangar Executivo Catarina Airport - Rod. Castelo Branco, km 52",
        "Palácio Tangará - R. Dep. Laércio Corte, 1501",
        "Aeroporto de Congonhas CGH - Ala Executiva"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("booking_screen")
    ) {
        // Top Title
        Text(
            text = "RESERVA EXECUTIVA DE LUXO",
            color = GoldPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )
        Text(
            text = "Configuração do Transfer VIP",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Schedule Mode Toggle (Now vs Scheduled)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ObsidianSurface, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { viewModel.updateBookingForm { copy(isScheduled = false) } }
                    .testTag("tab_now"),
                color = if (!form.isScheduled) GoldPrimary else Color.Transparent
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Bolt,
                        contentDescription = null,
                        tint = if (!form.isScheduled) ObsidianBackground else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Solicitar Imediato",
                        color = if (!form.isScheduled) ObsidianBackground else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { viewModel.updateBookingForm { copy(isScheduled = true) } }
                    .testTag("tab_schedule"),
                color = if (form.isScheduled) GoldPrimary else Color.Transparent
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = if (form.isScheduled) ObsidianBackground else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Agendar Horário",
                        color = if (form.isScheduled) ObsidianBackground else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (form.isScheduled) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = ObsidianCard,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, ObsidianBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(form.scheduledTimeFormatted, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    TextButton(onClick = {
                        viewModel.updateBookingForm { copy(scheduledTimeFormatted = "Amanhã, 07:00") }
                        Toast.makeText(context, "Horário executivo ajustado para Amanhã, 07:00", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Alterar", color = GoldPrimary, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Itinerary Section (Pickup & Dropoff)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = ObsidianSurface,
            border = BorderStroke(1.dp, ObsidianBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Pickup Input
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TripOrigin, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("PONTO DE EMBARQUE", color = TextTertiary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = form.pickupAddress,
                            onValueChange = { viewModel.updateBookingForm { copy(pickupAddress = it) } },
                            modifier = Modifier.fillMaxWidth().testTag("pickup_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Dropoff Input
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("DESTINO EXECUTIVO", color = TextTertiary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = form.dropoffAddress,
                            onValueChange = { viewModel.updateBookingForm { copy(dropoffAddress = it) } },
                            modifier = Modifier.fillMaxWidth().testTag("dropoff_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick preset pills
                Text("Locais Executivos Frequentes:", color = TextTertiary, fontSize = 10.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    executiveLocations.take(4).forEach { loc ->
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.updateBookingForm { copy(dropoffAddress = loc) } },
                            color = ObsidianCard,
                            border = BorderStroke(1.dp, ObsidianBorder)
                        ) {
                            Text(
                                text = loc.split(" - ")[0],
                                color = TextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Flight Radar Integration Input
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
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
                        Icon(Icons.Default.Flight, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("RADAR DE VOO / TERMINAL VIP", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Surface(color = AccentBlue.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                        Text("AUTO-TRACKING", color = AccentBlue, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Insira o voo para que seu chauffeur acompanhe atrasos ou antecipações automaticamente sem cobrança de espera.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = form.flightNumber,
                    onValueChange = { viewModel.updateBookingForm { copy(flightNumber = it) } },
                    placeholder = { Text("Ex: AF457, AA904, JJ8084", color = TextTertiary, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("flight_number_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ObsidianBackground,
                        unfocusedContainerColor = ObsidianBackground,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = ObsidianBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Vehicle Category Selector
        Text(
            text = "ESCOLHA SUA CATEGORIA",
            color = GoldLight,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            VehicleCategory.values().forEach { cat ->
                val isSelected = form.selectedCategory == cat
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            viewModel.updateBookingForm {
                                copy(selectedCategory = cat, isArmored = cat.isArmoredDefault)
                            }
                        }
                        .border(
                            1.dp,
                            if (isSelected) GoldPrimary else ObsidianBorder,
                            RoundedCornerShape(16.dp)
                        )
                        .testTag("vehicle_cat_${cat.id}"),
                    color = if (isSelected) GoldPrimary.copy(alpha = 0.08f) else ObsidianCard
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(if (isSelected) GoldPrimary else ObsidianSurfaceVariant, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (cat.isArmoredDefault) Icons.Default.Shield else Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    tint = if (isSelected) ObsidianBackground else TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = viewModel.t(cat.titleKey),
                                        color = if (isSelected) GoldLight else TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (cat.isArmoredDefault) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(color = ArmoredBadgeColor, shape = RoundedCornerShape(4.dp)) {
                                            Text("III-A", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                        }
                                    }
                                }
                                Text(
                                    text = cat.sampleModel,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "${cat.passengerCapacity} Lugares • ${cat.luggageCapacity} Malas • Wi-Fi 5G",
                                    color = TextTertiary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "R$ " + cat.basePrice.toInt(),
                                color = if (isSelected) GoldPrimary else TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "base",
                                color = TextTertiary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Luxury Amenities Selection
        Text(
            text = "COMODIDADES & SERVIÇOS CONCIERGE",
            color = GoldLight,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            LuxuryAmenity.values().forEach { amenity ->
                val isChecked = form.selectedAmenities.contains(amenity)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { viewModel.toggleAmenity(amenity) }
                        .border(1.dp, if (isChecked) GoldPrimary.copy(alpha = 0.5f) else ObsidianBorder, RoundedCornerShape(12.dp)),
                    color = if (isChecked) ObsidianSurfaceVariant else ObsidianCard
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { viewModel.toggleAmenity(amenity) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = GoldPrimary,
                                    checkmarkColor = ObsidianBackground
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = viewModel.t(amenity.nameKey),
                                color = if (isChecked) TextPrimary else TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }

                        Text(
                            text = if (amenity.price > 0) "+ R$ ${amenity.price.toInt()}" else "Incluso",
                            color = if (amenity.price > 0) GoldPrimary else AccentGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Payment Method Selector
        Text(
            text = "FORMA DE PAGAMENTO SEGURO",
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
            PaymentType.values().forEach { payment ->
                val isSelected = form.selectedPayment == payment
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { viewModel.updateBookingForm { copy(selectedPayment = payment) } }
                        .border(1.dp, if (isSelected) GoldPrimary else ObsidianBorder, RoundedCornerShape(12.dp)),
                    color = if (isSelected) GoldPrimary.copy(alpha = 0.15f) else ObsidianCard
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (payment) {
                                PaymentType.CORPORATE_BILLING -> Icons.Default.Business
                                PaymentType.CREDIT_CARD -> Icons.Default.CreditCard
                                PaymentType.PIX -> Icons.Default.QrCode2
                                PaymentType.VIP_VOUCHER -> Icons.Default.Token
                                PaymentType.GOOGLE_PAY -> Icons.Default.Payments
                            },
                            contentDescription = null,
                            tint = if (isSelected) GoldPrimary else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = viewModel.t(payment.labelKey),
                            color = if (isSelected) GoldLight else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Total Price Summary Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = ObsidianSurface,
            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("RESUMO DA TARIFA EXECUTIVA", color = GoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tarifa Base (${viewModel.t(form.selectedCategory.titleKey)})", color = TextSecondary, fontSize = 12.sp)
                    Text("R$ " + String.format(Locale.US, "%.2f", form.selectedCategory.basePrice), color = TextPrimary, fontSize = 12.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Distância Estimada (${form.estimatedDistanceKm} km)", color = TextSecondary, fontSize = 12.sp)
                    Text("R$ " + String.format(Locale.US, "%.2f", form.estimatedDistanceKm * form.selectedCategory.pricePerKm), color = TextPrimary, fontSize = 12.sp)
                }
                if (form.selectedAmenities.any { it.price > 0 }) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Serviços Adicionais Selecionados", color = TextSecondary, fontSize = 12.sp)
                        Text("R$ " + String.format(Locale.US, "%.2f", form.selectedAmenities.sumOf { it.price }), color = TextPrimary, fontSize = 12.sp)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = ObsidianBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("VALOR TOTAL", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Impostos & Chauffeur inclusos", color = TextTertiary, fontSize = 10.sp)
                    }
                    Text(
                        text = "R$ " + String.format(Locale.US, "%.2f", totalFare),
                        color = GoldPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Confirm CTA
        Button(
            onClick = {
                viewModel.confirmBooking {
                    Toast.makeText(context, "Transfer Executivo confirmado! Chauffeur em deslocamento.", Toast.LENGTH_LONG).show()
                    onBookingSuccess()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("confirm_booking_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ObsidianBackground, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                viewModel.t("confirm_booking"),
                color = ObsidianBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
