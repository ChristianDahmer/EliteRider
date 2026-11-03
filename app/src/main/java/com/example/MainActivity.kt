package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.LuxeHeader
import com.example.ui.components.RatingDialog
import com.example.ui.components.ReceiptDialog
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.LuxeDriveViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: LuxeDriveViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            LuxeDriveTheme(themeMode = themeMode) {
                MainApp(viewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: LuxeDriveViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val activeTrip by viewModel.activeTrip.collectAsStateWithLifecycle()
    val allTrips by viewModel.allTrips.collectAsStateWithLifecycle()
    val allVehicles by viewModel.allVehicles.collectAsStateWithLifecycle()
    val allChauffeurs by viewModel.allChauffeurs.collectAsStateWithLifecycle()
    val allInventory by viewModel.allInventory.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val allNotifications by viewModel.allNotifications.collectAsStateWithLifecycle()
    val supportMessages by viewModel.supportMessages.collectAsStateWithLifecycle()
    val backupState by viewModel.backupState.collectAsStateWithLifecycle()

    val selectedReceipt by viewModel.selectedReceiptTrip.collectAsStateWithLifecycle()
    val ratingTrip by viewModel.ratingTargetTrip.collectAsStateWithLifecycle()

    val palette = LuxeTheme.palette
    val unreadNotifs = allNotifications.count { !it.isRead }

    // Back handling: if not on home screen, return to home
    if (currentScreen != "home") {
        BackHandler {
            viewModel.navigateTo("home")
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            LuxeHeader(
                currentLanguage = currentLanguage,
                onLanguageSelected = { viewModel.setLanguage(it) },
                themeMode = themeMode,
                onToggleTheme = { viewModel.toggleThemeMode() },
                unreadNotificationsCount = unreadNotifs,
                onNotificationsClick = { viewModel.navigateTo("notifications") },
                isOffline = backupState.isOfflineModeActive,
                onSyncClick = { viewModel.triggerCloudBackup() }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = palette.surface,
                contentColor = palette.goldPrimary,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = currentScreen == "home",
                    onClick = { viewModel.navigateTo("home") },
                    icon = {
                        Icon(
                            if (currentScreen == "home") Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Início"
                        )
                    },
                    label = { Text(viewModel.t("nav_home"), fontSize = 9.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = palette.background,
                        indicatorColor = palette.goldPrimary,
                        selectedTextColor = palette.goldPrimary,
                        unselectedIconColor = palette.textSecondary,
                        unselectedTextColor = palette.textSecondary
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == "radar",
                    onClick = { viewModel.navigateTo("radar") },
                    icon = {
                        Icon(
                            if (currentScreen == "radar") Icons.Filled.FlightLand else Icons.Outlined.FlightLand,
                            contentDescription = "Radar Litoral"
                        )
                    },
                    label = { Text(viewModel.t("nav_radar"), fontSize = 9.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = palette.background,
                        indicatorColor = palette.goldPrimary,
                        selectedTextColor = palette.goldPrimary,
                        unselectedIconColor = palette.textSecondary,
                        unselectedTextColor = palette.textSecondary
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == "booking",
                    onClick = { viewModel.navigateTo("booking") },
                    icon = {
                        Icon(
                            if (currentScreen == "booking") Icons.Filled.AddCircle else Icons.Outlined.AddCircleOutline,
                            contentDescription = "Reservar"
                        )
                    },
                    label = { Text(viewModel.t("nav_booking"), fontSize = 9.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = palette.background,
                        indicatorColor = palette.goldPrimary,
                        selectedTextColor = palette.goldPrimary,
                        unselectedIconColor = palette.textSecondary,
                        unselectedTextColor = palette.textSecondary
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == "tracking",
                    onClick = { viewModel.navigateTo("tracking") },
                    icon = {
                        Icon(
                            if (currentScreen == "tracking") Icons.Filled.LocationOn else Icons.Outlined.LocationOn,
                            contentDescription = "Ao Vivo"
                        )
                    },
                    label = { Text(viewModel.t("nav_tracking"), fontSize = 9.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = palette.background,
                        indicatorColor = palette.goldPrimary,
                        selectedTextColor = palette.goldPrimary,
                        unselectedIconColor = palette.textSecondary,
                        unselectedTextColor = palette.textSecondary
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == "history",
                    onClick = { viewModel.navigateTo("history") },
                    icon = {
                        Icon(
                            if (currentScreen == "history") Icons.AutoMirrored.Filled.ReceiptLong else Icons.AutoMirrored.Outlined.ReceiptLong,
                            contentDescription = "Corridas"
                        )
                    },
                    label = { Text(viewModel.t("nav_history"), fontSize = 9.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = palette.background,
                        indicatorColor = palette.goldPrimary,
                        selectedTextColor = palette.goldPrimary,
                        unselectedIconColor = palette.textSecondary,
                        unselectedTextColor = palette.textSecondary
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == "fleet",
                    onClick = { viewModel.navigateTo("fleet") },
                    icon = {
                        Icon(
                            if (currentScreen == "fleet") Icons.Filled.DirectionsCar else Icons.Outlined.DirectionsCar,
                            contentDescription = "Frota"
                        )
                    },
                    label = { Text(viewModel.t("nav_fleet"), fontSize = 9.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = palette.background,
                        indicatorColor = palette.goldPrimary,
                        selectedTextColor = palette.goldPrimary,
                        unselectedIconColor = palette.textSecondary,
                        unselectedTextColor = palette.textSecondary
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == "profile",
                    onClick = { viewModel.navigateTo("profile") },
                    icon = {
                        Icon(
                            if (currentScreen == "profile") Icons.Filled.Person else Icons.Outlined.Person,
                            contentDescription = "Perfil"
                        )
                    },
                    label = { Text(viewModel.t("nav_profile"), fontSize = 9.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = palette.background,
                        indicatorColor = palette.goldPrimary,
                        selectedTextColor = palette.goldPrimary,
                        unselectedIconColor = palette.textSecondary,
                        unselectedTextColor = palette.textSecondary
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(palette.background)
        ) {
            when (currentScreen) {
                "home" -> HomeScreen(
                    viewModel = viewModel,
                    activeTrip = activeTrip,
                    onNavigateToBooking = { viewModel.navigateTo("booking") },
                    onNavigateToTracking = { viewModel.navigateTo("tracking") },
                    onNavigateToRadar = { viewModel.navigateTo("radar") },
                    onNavigateToFleet = { viewModel.navigateTo("fleet") },
                    onNavigateToFinancial = { viewModel.navigateTo("financial") },
                    onNavigateToSupport = { viewModel.navigateTo("support") }
                )
                "radar" -> CoastalRadarScreen(
                    viewModel = viewModel,
                    onNavigateToBooking = { viewModel.navigateTo("booking") },
                    onNavigateToSupport = { viewModel.navigateTo("support") }
                )
                "booking" -> BookingScreen(
                    viewModel = viewModel,
                    onBookingSuccess = { viewModel.navigateTo("tracking") }
                )
                "tracking" -> LiveTrackingScreen(
                    viewModel = viewModel,
                    activeTrip = activeTrip,
                    onNavigateToBooking = { viewModel.navigateTo("booking") },
                    onNavigateToSupport = { viewModel.navigateTo("support") }
                )
                "history" -> TripHistoryScreen(
                    viewModel = viewModel,
                    trips = allTrips,
                    onOpenReceipt = { viewModel.openReceipt(it) },
                    onOpenRating = { viewModel.openRating(it) },
                    onRebook = { trip ->
                        viewModel.updateBookingForm {
                            copy(
                                pickupAddress = trip.pickupAddress,
                                dropoffAddress = trip.dropoffAddress
                            )
                        }
                        viewModel.navigateTo("booking")
                    }
                )
                "fleet" -> FleetAdminScreen(
                    viewModel = viewModel,
                    vehicles = allVehicles,
                    chauffeurs = allChauffeurs,
                    inventory = allInventory
                )
                "financial" -> FinancialAnalyticsScreen(
                    viewModel = viewModel,
                    transactions = allTransactions
                )
                "support" -> ConciergeSupportScreen(
                    viewModel = viewModel,
                    messages = supportMessages
                )
                "notifications" -> NotificationsScreen(
                    viewModel = viewModel,
                    notifications = allNotifications,
                    onNavigateToTracking = { viewModel.navigateTo("tracking") }
                )
                "profile" -> ProfileSettingsScreen(
                    viewModel = viewModel
                )
            }

            // Receipts Dialog
            selectedReceipt?.let { trip ->
                ReceiptDialog(
                    trip = trip,
                    onDismiss = { viewModel.closeReceipt() }
                )
            }

            // Rating Dialog
            ratingTrip?.let { trip ->
                RatingDialog(
                    trip = trip,
                    onDismiss = { viewModel.closeRating() },
                    onSubmit = { stars, comment ->
                        viewModel.submitRating(trip.id, stars, comment)
                    }
                )
            }
        }
    }
}
