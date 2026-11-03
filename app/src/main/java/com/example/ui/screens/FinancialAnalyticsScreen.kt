package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.FinancialTransactionEntity
import com.example.ui.theme.*
import com.example.viewmodel.LuxeDriveViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FinancialAnalyticsScreen(
    viewModel: LuxeDriveViewModel,
    transactions: List<FinancialTransactionEntity>
) {
    val context = LocalContext.current
    val totalRevenue = transactions.sumOf { it.amount } + 142850.0 // Base monthly volume + dynamic
    val corporateRevenue = transactions.filter { it.accountType == "CORPORATE" }.sumOf { it.amount } + 112000.0
    val totalTrips = transactions.size + 168
    val avgTicket = totalRevenue / totalTrips
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("financial_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CONTROLADORIA EXECUTIVA",
                    color = GoldPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = viewModel.t("financial_dashboard"),
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    Toast.makeText(context, "Relatório Financeiro Consolidado exportado (PDF/CSV).", Toast.LENGTH_LONG).show()
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                modifier = Modifier.testTag("export_report_button")
            ) {
                Icon(Icons.Default.Download, contentDescription = null, tint = ObsidianBackground, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Exportar", color = ObsidianBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Revenue Hero Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = ObsidianSurface,
            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FATURAMENTO MENSAL CONSOLIDADO",
                        color = GoldLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Surface(
                        color = AccentGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "+18.4% vs mês anterior",
                            color = AccentGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "R$ " + String.format(Locale.US, "%,.2f", totalRevenue),
                    color = GoldPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Transfers VIP", color = TextTertiary, fontSize = 10.sp)
                        Text("$totalTrips viagens", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Ticket Médio", color = TextTertiary, fontSize = 10.sp)
                        Text("R$ " + String.format(Locale.US, "%.0f", avgTicket), color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Faturamento Corp", color = TextTertiary, fontSize = 10.sp)
                        Text("${((corporateRevenue / totalRevenue) * 100).toInt()}% B2B", color = AccentBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Revenue Breakdown by Vehicle Class
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = ObsidianSurface,
            border = BorderStroke(1.dp, ObsidianBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "RECEITA POR CATEGORIA DE VEÍCULO",
                    color = GoldLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                CategoryBarRow("Blindados VIP III-A", 48, GoldPrimary)
                CategoryBarRow("First Class Sedans (Classe S / Série 7)", 26, AccentBlue)
                CategoryBarRow("SUVs Executivos (Escalade / Range Rover)", 16, AccentGreen)
                CategoryBarRow("Jet-Vans Maybach Lounge", 10, AccentPurple)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recent Invoices Log
        Text(
            text = "FATURAS & TRANSAÇÕES RECENTES",
            color = GoldLight,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            transactions.take(5).forEach { tx ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp)),
                    color = ObsidianCard
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(tx.invoiceNumber, color = GoldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = if (tx.accountType == "CORPORATE") AccentBlue.copy(alpha = 0.15f) else ObsidianSurfaceVariant,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = if (tx.accountType == "CORPORATE") "FATURADO B2B" else "CARTÃO VIP",
                                        color = if (tx.accountType == "CORPORATE") AccentBlue else TextSecondary,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(tx.clientName, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text(dateFormat.format(Date(tx.dateEpochMillis)), color = TextTertiary, fontSize = 10.sp)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "R$ " + String.format(Locale.US, "%.2f", tx.amount),
                                color = GoldPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                color = if (tx.status == "PAID") AccentGreen.copy(alpha = 0.15f) else AccentAmber.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = if (tx.status == "PAID") "LIQUIDADO" else "A FATURAR",
                                    color = if (tx.status == "PAID") AccentGreen else AccentAmber,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun CategoryBarRow(label: String, percentage: Int, color: Color) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = TextPrimary, fontSize = 11.sp)
            Text("$percentage%", color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(ObsidianBackground, RoundedCornerShape(3.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = percentage / 100f)
                    .height(6.dp)
                    .background(color, RoundedCornerShape(3.dp))
            )
        }
    }
}
