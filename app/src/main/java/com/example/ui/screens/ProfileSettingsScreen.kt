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
import com.example.data.model.AppThemeMode
import com.example.localization.AppLanguage
import com.example.ui.theme.*
import com.example.viewmodel.LuxeDriveViewModel

@Composable
fun ProfileSettingsScreen(
    viewModel: LuxeDriveViewModel
) {
    val context = LocalContext.current
    val currentLang by viewModel.currentLanguage.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val accountType by viewModel.accountType.collectAsState()
    val backupState by viewModel.backupState.collectAsState()
    val palette = LuxeTheme.palette

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("profile_screen")
    ) {
        // User VIP Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.goldPrimary.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(palette.goldDark, CircleShape)
                            .border(2.dp, palette.goldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("CD", color = palette.goldLight, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Dr. Christian Dahmer", color = palette.textPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.Verified, contentDescription = null, tint = palette.goldPrimary, modifier = Modifier.size(16.dp))
                        }
                        Text("ID: LX-DIAMOND-9902", color = palette.textSecondary, fontSize = 11.sp)
                        Surface(
                            color = palette.goldPrimary,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "MEMBRO BLACK DIAMOND",
                                color = if (palette.isDark) Color.Black else Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION: THEME & LOW-LIGHT NIGHT TRANSFER MODE
        Text(
            text = "TEMA & ILUMINAÇÃO PARA TRANSFERS",
            color = palette.goldLight,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            text = "Controle de brilho e modos visuais sob medida para viagens noturnas e diurnas:",
            color = palette.textSecondary,
            fontSize = 10.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                AppThemeMode.entries.forEach { mode ->
                    val isSelected = themeMode == mode
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                viewModel.setThemeMode(mode)
                                Toast.makeText(
                                    context,
                                    "Tema alterado: ${mode.label}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .padding(vertical = 4.dp),
                        color = if (isSelected) palette.goldPrimary.copy(alpha = 0.12f) else Color.Transparent,
                        border = if (isSelected) BorderStroke(1.dp, palette.goldPrimary) else null,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(
                                            if (isSelected) palette.goldPrimary else palette.surfaceVariant,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (mode) {
                                            AppThemeMode.NIGHT_TRANSFER -> Icons.Default.DarkMode
                                            AppThemeMode.CHAMPAGNE_LIGHT -> Icons.Default.LightMode
                                            AppThemeMode.SYSTEM_AUTO -> Icons.Default.BrightnessAuto
                                        },
                                        contentDescription = null,
                                        tint = if (isSelected) (if (palette.isDark) Color.Black else Color.White) else palette.goldPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = mode.label,
                                        color = if (isSelected) palette.goldLight else palette.textPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = mode.subtitle,
                                        color = palette.textSecondary,
                                        fontSize = 10.sp,
                                        lineHeight = 13.sp
                                    )
                                }
                            }

                            if (isSelected) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = palette.goldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Low-Light Night Transfer OLED Glare Protection Callout
                Surface(
                    color = palette.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, palette.border)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Bedtime,
                            contentDescription = null,
                            tint = palette.goldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "O modo Noturno VIP utiliza tons escuros de baixo contraste (#090B0E) para preservar a visão noturna dos passageiros durante transfers em rodovias e aeroportos.",
                            color = palette.textSecondary,
                            fontSize = 10.sp,
                            lineHeight = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Account Type Selector (Corporate vs Individual)
        Text(
            text = "MODALIDADE DE FATURAMENTO",
            color = palette.goldLight,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(palette.surface, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { viewModel.setAccountType("CORPORATE") },
                color = if (accountType == "CORPORATE") palette.goldPrimary else Color.Transparent
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Business,
                        contentDescription = null,
                        tint = if (accountType == "CORPORATE") (if (palette.isDark) Color.Black else Color.White) else palette.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Corporativo B2B",
                        color = if (accountType == "CORPORATE") (if (palette.isDark) Color.Black else Color.White) else palette.textSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { viewModel.setAccountType("INDIVIDUAL") },
                color = if (accountType == "INDIVIDUAL") palette.goldPrimary else Color.Transparent
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = if (accountType == "INDIVIDUAL") (if (palette.isDark) Color.Black else Color.White) else palette.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "VIP Individual",
                        color = if (accountType == "INDIVIDUAL") (if (palette.isDark) Color.Black else Color.White) else palette.textSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Multi-Language Selector
        Text(
            text = "IDIOMA / MULTILINGUAL",
            color = palette.goldLight,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                AppLanguage.values().forEach { lang ->
                    val isSelected = currentLang == lang
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.setLanguage(lang) }
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(lang.flagEmoji, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = lang.displayName,
                                color = if (isSelected) palette.goldLight else palette.textPrimary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }

                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = palette.goldPrimary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Offline Mode & Local Cache
        Text(
            text = "MODO OFFLINE & PERSISTÊNCIA LOCAL",
            color = palette.goldLight,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Banco de Dados Offline Ativo", color = palette.textPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Permite acesso a todos os itinerários e contatos de chauffeurs em áreas remotas ou em voo.",
                            color = palette.textSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = backupState.isOfflineModeActive,
                        onCheckedChange = { viewModel.toggleOfflineMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = palette.goldPrimary,
                            checkedTrackColor = palette.goldDark
                        )
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = palette.border)

                // Cloud Backup Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(backupState.cloudStatusMessage, color = AccentGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Último backup: ${backupState.lastSyncTime}", color = palette.textTertiary, fontSize = 10.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.triggerCloudBackup()
                            Toast.makeText(context, "Sincronização em nuvem disparada!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, palette.goldPrimary),
                        modifier = Modifier.testTag("backup_button")
                    ) {
                        if (backupState.isSyncing) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = palette.goldPrimary, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.CloudSync, contentDescription = null, tint = palette.goldPrimary, modifier = Modifier.size(14.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Backup", color = palette.goldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Security & Compliance Accreditations
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = palette.surface,
            border = BorderStroke(1.dp, palette.border)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("CREDENCIAIS & CERTIFICAÇÕES DE SEGURANÇA", color = palette.goldLight, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(10.dp))
                SecurityRow("Blindagem Nível III-A Certificada", "Exército Brasileiro & ANVISA", palette)
                SecurityRow("Acesso Hangar Executivo & VIP T3", "Credencial ANAC / GRU Airport", palette)
                SecurityRow("Privacidade & Dados Criptografados", "Conformidade LGPD & ISO 27001", palette)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SecurityRow(title: String, subtitle: String, palette: LuxePalette) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = palette.goldPrimary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, color = palette.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = palette.textTertiary, fontSize = 10.sp)
        }
    }
}
