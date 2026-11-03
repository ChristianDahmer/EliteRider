package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
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
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.TripBookingEntity
import com.example.ui.theme.*

@Composable
fun RatingDialog(
    trip: TripBookingEntity,
    onDismiss: () -> Unit,
    onSubmit: (Int, String) -> Unit
) {
    var selectedStars by remember { mutableStateOf(5) }
    var reviewText by remember { mutableStateOf("") }
    val quickTags = listOf("Pontualidade Britânica", "Chauffeur Impecável", "Veículo Esterilizado", "Condução Suave & Discreta", "Conexão 5G Excelente")
    var selectedTag by remember { mutableStateOf(quickTags[0]) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                .testTag("rating_dialog_container"),
            color = ObsidianSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AVALIAÇÃO DE QUALIDADE VIP",
                        color = GoldPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Como foi seu transfer com ${trip.chauffeurName}?",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = trip.vehicleModel,
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Interactive 5 stars
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= selectedStars) Icons.Default.Star else Icons.Outlined.Star,
                            contentDescription = "$i Estrelas",
                            tint = if (i <= selectedStars) GoldPrimary else ObsidianBorder,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { selectedStars = i }
                                .testTag("star_$i")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Quick tags
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickTags.take(3).forEach { tag ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTag = tag },
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedTag == tag) GoldPrimary.copy(alpha = 0.15f) else ObsidianCard,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (selectedTag == tag) GoldPrimary else ObsidianBorder
                            )
                        ) {
                            Text(
                                text = tag,
                                color = if (selectedTag == tag) GoldLight else TextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    placeholder = { Text("Comentários executivos adicionais (opcional)", color = TextTertiary, fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .testTag("review_input_field"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ObsidianBackground,
                        unfocusedContainerColor = ObsidianBackground,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = ObsidianBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val finalComment = if (reviewText.isNotBlank()) "$selectedTag - $reviewText" else selectedTag
                        onSubmit(selectedStars, finalComment)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_rating_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("Salvar Avaliação Executiva", color = ObsidianBackground, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
