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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CoastalFlightPhase
import com.example.data.model.CoastalFlightRadarState
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CoastalRadarCanvas(
    radarState: CoastalFlightRadarState,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar_anim")

    // Radar sweep rotation
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep_angle"
    )

    // Aircraft glide slope animation
    val planeProgress by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "plane_progress"
    )

    // Coastal Beacon Pulse
    val beaconPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beacon_pulse"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(ObsidianSurface)
            .border(1.dp, ObsidianBorder, RoundedCornerShape(24.dp))
            .testTag("coastal_radar_container")
    ) {
        // Radar Dark Graphics Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val center = Offset(width / 2f, height / 2f)
            val maxRadius = minOf(width, height) * 0.44f

            // 1. Draw Radar Range Concentric Rings (10nm, 20nm, 30nm)
            drawCircle(
                color = GoldPrimary.copy(alpha = 0.08f),
                radius = maxRadius,
                center = center,
                style = Stroke(width = 1.5f)
            )
            drawCircle(
                color = GoldPrimary.copy(alpha = 0.12f),
                radius = maxRadius * 0.66f,
                center = center,
                style = Stroke(width = 1.5f)
            )
            drawCircle(
                color = GoldPrimary.copy(alpha = 0.16f),
                radius = maxRadius * 0.33f,
                center = center,
                style = Stroke(width = 1.5f)
            )

            // Radar Crosshairs / Nav Axis
            drawLine(
                color = GoldPrimary.copy(alpha = 0.15f),
                start = Offset(center.x - maxRadius, center.y),
                end = Offset(center.x + maxRadius, center.y),
                strokeWidth = 1f
            )
            drawLine(
                color = GoldPrimary.copy(alpha = 0.15f),
                start = Offset(center.x, center.y - maxRadius),
                end = Offset(center.x, center.y + maxRadius),
                strokeWidth = 1f
            )

            // 2. Draw Coastline Profile & Ocean Contour (Litoral Paulista & Baía)
            drawCoastlineContour(width, height)

            // 3. Draw Coastal Airport Runway / Beacon Point
            val runwayCenter = Offset(width * 0.68f, height * 0.58f)

            // Runway Beacon Pulse
            drawCircle(
                color = AccentGreen.copy(alpha = 0.25f),
                radius = 28f * beaconPulse,
                center = runwayCenter
            )
            drawCircle(
                color = AccentGreen,
                radius = 7f,
                center = runwayCenter
            )

            // Runway Orientation Line
            drawLine(
                color = GoldLight,
                start = Offset(runwayCenter.x - 22f, runwayCenter.y - 12f),
                end = Offset(runwayCenter.x + 22f, runwayCenter.y + 12f),
                strokeWidth = 5f,
                cap = StrokeCap.Round
            )

            // 4. Draw ILS / RNAV Flight Approach Corridor into Coastal Airport
            val entryPoint = Offset(width * 0.15f, height * 0.28f)
            val waypointCoast = Offset(width * 0.42f, height * 0.38f)

            val glidePath = Path().apply {
                moveTo(entryPoint.x, entryPoint.y)
                quadraticTo(waypointCoast.x, waypointCoast.y, runwayCenter.x, runwayCenter.y)
            }

            // Path glow
            drawPath(
                path = glidePath,
                color = AccentBlue.copy(alpha = 0.25f),
                style = Stroke(width = 10f, cap = StrokeCap.Round)
            )
            drawPath(
                path = glidePath,
                color = AccentBlue,
                style = Stroke(
                    width = 3f,
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                )
            )

            // 5. Draw Radar Rotating Sweep Beam
            rotate(degrees = sweepAngle, pivot = center) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            GoldPrimary.copy(alpha = 0.0f),
                            GoldPrimary.copy(alpha = 0.35f)
                        ),
                        center = center
                    ),
                    startAngle = 0f,
                    sweepAngle = 45f,
                    useCenter = true,
                    size = androidx.compose.ui.geometry.Size(maxRadius * 2, maxRadius * 2),
                    topLeft = Offset(center.x - maxRadius, center.y - maxRadius)
                )
            }

            // 6. Draw Aircraft Position on Approach Path
            val planeX = lerp(entryPoint.x, runwayCenter.x, planeProgress)
            val planeY = lerp(entryPoint.y, runwayCenter.y, planeProgress)
            val planePos = Offset(planeX, planeY)

            // Aircraft Blip & Halo
            drawCircle(
                color = GoldPrimary.copy(alpha = 0.45f),
                radius = 20f,
                center = planePos
            )
            drawCircle(
                color = ObsidianBackground,
                radius = 12f,
                center = planePos
            )
            drawCircle(
                color = GoldPrimary,
                radius = 8f,
                center = planePos
            )

            // Altitude / Vector Leader line
            drawLine(
                color = GoldPrimary,
                start = planePos,
                end = Offset(planePos.x + 28f, planePos.y - 20f),
                strokeWidth = 1.5f
            )
        }

        // HUD Top Layer: Real-Time Radar Metadata
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Live Radar Frequency Pill
            Surface(
                color = ObsidianBackground.copy(alpha = 0.85f),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(6.dp).background(AccentGreen, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RADAR COSTEIRO ATIVO • 1090 MHz",
                        color = TextPrimary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Weather & Coastal Wind Badge
            Surface(
                color = ObsidianBackground.copy(alpha = 0.85f),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Air,
                        contentDescription = "Vento",
                        tint = AccentBlue,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${radarState.temperatureC}°C • Vento 12kt",
                        color = TextPrimary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // HUD Bottom Floating Telemetry Card
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = radarState.flightNumber,
                            color = GoldLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(${radarState.selectedCoastalAirport.icao})",
                            color = AccentBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = "Destino: ${radarState.selectedCoastalAirport.airportName}",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("ALTITUDE", color = TextTertiary, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Text("${radarState.currentAltitudeFt} ft", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("VELOCIDADE", color = TextTertiary, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Text("${radarState.currentSpeedKmH} km/h", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("ETA COSTA", color = TextTertiary, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Text("${radarState.etaMinutes} min", color = GoldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun lerp(start: Float, end: Float, fraction: Float): Float {
    return start + (end - start) * fraction
}

private fun DrawScope.drawCoastlineContour(width: Float, height: Float) {
    val coastColor = Color(0xFF1E293B)
    val seaGlow = Color(0xFF0F172A)

    val coastPath = Path().apply {
        moveTo(width * 0.40f, 0f)
        cubicTo(
            width * 0.50f, height * 0.25f,
            width * 0.45f, height * 0.45f,
            width * 0.65f, height * 0.65f
        )
        cubicTo(
            width * 0.80f, height * 0.80f,
            width * 0.70f, height * 0.95f,
            width * 0.85f, height
        )
        lineTo(width, height)
        lineTo(width, 0f)
        close()
    }

    // Fill Ocean area
    drawPath(
        path = coastPath,
        color = seaGlow.copy(alpha = 0.5f)
    )

    // Outline Shoreline
    drawPath(
        path = coastPath,
        color = coastColor,
        style = Stroke(width = 3f)
    )

    // Islands / Archipelago (Ilhabela & Ilha de Santo Amaro)
    drawCircle(
        color = coastColor,
        radius = 18f,
        center = Offset(width * 0.76f, height * 0.32f)
    )
    drawCircle(
        color = coastColor,
        radius = 12f,
        center = Offset(width * 0.82f, height * 0.45f)
    )
}
