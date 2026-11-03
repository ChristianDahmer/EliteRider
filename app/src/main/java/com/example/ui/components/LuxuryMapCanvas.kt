package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LiveTelematics
import com.example.ui.theme.*

@Composable
fun LuxuryMapCanvas(
    telematics: LiveTelematics,
    pickupText: String,
    dropoffText: String,
    modifier: Modifier = Modifier,
    isArmored: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val carProgressTransition = rememberInfiniteTransition(label = "carMove")
    val routeProgress by carProgressTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "routeProgress"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(ObsidianSurface)
            .border(1.dp, ObsidianBorder, RoundedCornerShape(24.dp))
            .testTag("luxury_map_container")
    ) {
        // Luxury Dark Map Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Draw Grid Streets / City blocks (Dark luxury aesthetic)
            drawMapGrid(width, height)

            // 2. Define executive route waypoints
            val startPoint = Offset(width * 0.20f, height * 0.75f)
            val waypoint1 = Offset(width * 0.35f, height * 0.65f)
            val waypoint2 = Offset(width * 0.45f, height * 0.40f)
            val waypoint3 = Offset(width * 0.70f, height * 0.35f)
            val endPoint = Offset(width * 0.82f, height * 0.20f)

            val routePath = Path().apply {
                moveTo(startPoint.x, startPoint.y)
                cubicTo(waypoint1.x, waypoint1.y, waypoint2.x, waypoint2.y, waypoint3.x, waypoint3.y)
                lineTo(endPoint.x, endPoint.y)
            }

            // Route outer glow
            drawPath(
                path = routePath,
                color = GoldPrimary.copy(alpha = 0.25f),
                style = Stroke(width = 16f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Route main golden highway line
            drawPath(
                path = routePath,
                color = GoldPrimary,
                style = Stroke(
                    width = 6f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    pathEffect = PathEffect.cornerPathEffect(12f)
                )
            )

            // 3. Draw Pickup Pin (Pulse effect)
            drawCircle(
                color = AccentBlue.copy(alpha = 0.3f),
                radius = 24f * pulseScale,
                center = startPoint
            )
            drawCircle(
                color = AccentBlue,
                radius = 12f,
                center = startPoint
            )
            drawCircle(
                color = Color.White,
                radius = 5f,
                center = startPoint
            )

            // 4. Draw Dropoff Pin (VIP Gold)
            drawCircle(
                color = GoldPrimary.copy(alpha = 0.35f),
                radius = 28f * pulseScale,
                center = endPoint
            )
            drawCircle(
                color = GoldPrimary,
                radius = 14f,
                center = endPoint
            )
            drawCircle(
                color = ObsidianBackground,
                radius = 6f,
                center = endPoint
            )

            // 5. Draw Animated Car on Route
            val carX = lerpCoordinates(startPoint.x, endPoint.x, routeProgress)
            val carY = lerpCoordinates(startPoint.y, endPoint.y, routeProgress) - 10f

            // Car halo
            drawCircle(
                color = GoldLight.copy(alpha = 0.4f),
                radius = 22f,
                center = Offset(carX, carY)
            )
            // Car body indicator
            drawCircle(
                color = ObsidianBackground,
                radius = 14f,
                center = Offset(carX, carY)
            )
            drawCircle(
                color = GoldPrimary,
                radius = 10f,
                center = Offset(carX, carY)
            )
        }

        // Overlays: Telematics & Map HUD
        // Top Badges (Live GPS, Armored, 5G)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Speedometer Pill
            Surface(
                color = ObsidianBackground.copy(alpha = 0.85f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(AccentGreen, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${telematics.currentSpeedKmH} km/h",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GPS LIVE",
                        color = GoldPrimary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Security Badges
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (isArmored) {
                    Surface(
                        color = ArmoredBadgeColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ArmoredBadgeColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Shield,
                                contentDescription = "Armored",
                                tint = ArmoredBadgeColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "III-A BLINDADO",
                                color = TextPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Surface(
                    color = ObsidianBackground.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Wifi,
                            contentDescription = "5G",
                            tint = AccentBlue,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "5G VIP",
                            color = TextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Bottom floating info card (ETA & Distance)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(12.dp),
            color = ObsidianBackground.copy(alpha = 0.92f),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CHEGADA ESTIMADA",
                        color = TextTertiary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${telematics.etaMinutes} min (${telematics.distanceRemainingKm} km)",
                        color = GoldPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Thermostat,
                        contentDescription = "Cabin Temp",
                        tint = AccentBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${telematics.cabinTemperature}°C",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

private fun lerpCoordinates(start: Float, end: Float, fraction: Float): Float {
    return start + (end - start) * fraction
}

private fun DrawScope.drawMapGrid(width: Float, height: Float) {
    val roadColor = Color(0xFF1B2230)
    val avenueColor = Color(0xFF242F42)

    // Horizontal roads
    drawLine(roadColor, Offset(0f, height * 0.25f), Offset(width, height * 0.25f), strokeWidth = 3f)
    drawLine(avenueColor, Offset(0f, height * 0.50f), Offset(width, height * 0.50f), strokeWidth = 6f)
    drawLine(roadColor, Offset(0f, height * 0.75f), Offset(width, height * 0.75f), strokeWidth = 3f)

    // Vertical roads
    drawLine(roadColor, Offset(width * 0.20f, 0f), Offset(width * 0.20f, height), strokeWidth = 3f)
    drawLine(avenueColor, Offset(width * 0.55f, 0f), Offset(width * 0.55f, height), strokeWidth = 6f)
    drawLine(roadColor, Offset(width * 0.80f, 0f), Offset(width * 0.80f, height), strokeWidth = 3f)

    // Diagonal arterial avenues
    drawLine(roadColor.copy(alpha = 0.7f), Offset(0f, height * 0.8f), Offset(width * 0.9f, 0f), strokeWidth = 4f)
    drawLine(roadColor.copy(alpha = 0.5f), Offset(width * 0.1f, height), Offset(width, height * 0.3f), strokeWidth = 4f)
}
