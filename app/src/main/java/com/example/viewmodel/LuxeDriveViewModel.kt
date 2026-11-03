package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entities.*
import com.example.data.model.*
import com.example.data.repository.LuxeDriveRepository
import com.example.localization.AppLanguage
import com.example.localization.LocaleManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.sin

data class BookingFormState(
    val pickupAddress: String = "Hotel Fasano Jardins - R. Vittorio Fasano, 88",
    val dropoffAddress: String = "Aeroporto de Guarujá / Santos (Base Aérea SBDN)",
    val flightNumber: String = "PR-VIP (Phenom 300E)",
    val isScheduled: Boolean = false,
    val scheduledTimeFormatted: String = "Hoje, 20:30",
    val scheduledEpochMillis: Long = System.currentTimeMillis() + 3600000,
    val selectedCategory: VehicleCategory = VehicleCategory.ARMORED_VIP,
    val isArmored: Boolean = true,
    val selectedAmenities: Set<LuxuryAmenity> = setOf(
        LuxuryAmenity.WIFI,
        LuxuryAmenity.SPARKLING_WATER,
        LuxuryAmenity.BILINGUAL_CHAUFFEUR,
        LuxuryAmenity.CLIMATE_PRECONDITION
    ),
    val selectedPayment: PaymentType = PaymentType.CORPORATE_BILLING,
    val passengerName: String = "Dr. Christian Dahmer",
    val passengerPhone: String = "+55 11 99123-4567",
    val estimatedDistanceKm: Double = 84.5,
    val specialNotes: String = "Aguardar com placa nominal executiva no saguão do Hangar VIP"
)

data class BackupState(
    val isSyncing: Boolean = false,
    val lastSyncTime: String = "Hoje, 08:24",
    val isOfflineModeActive: Boolean = false,
    val cloudStatusMessage: String = "Sincronizado com Nuvem Criptografada"
)

class LuxeDriveViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = LuxeDriveRepository(
        database.tripDao(),
        database.fleetDao(),
        database.financialDao(),
        database.notificationDao(),
        database.supportDao()
    )

    // Language state
    private val _currentLanguage = MutableStateFlow(AppLanguage.PORTUGUESE)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    // Global Theme & Night Transfer Low-Light Mode
    private val _themeMode = MutableStateFlow(AppThemeMode.NIGHT_TRANSFER)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    // Navigation state
    private val _currentScreen = MutableStateFlow("home")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // Account state
    private val _accountType = MutableStateFlow("CORPORATE")
    val accountType: StateFlow<String> = _accountType.asStateFlow()

    // Flows from DB
    val allTrips = repository.allTrips.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val activeTrip = repository.activeTrip.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val allVehicles = repository.allVehicles.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allChauffeurs = repository.allChauffeurs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allInventory = repository.allInventory.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allTransactions = repository.allTransactions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allNotifications = repository.allNotifications.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val supportMessages = repository.allSupportMessages.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Booking state
    private val _bookingForm = MutableStateFlow(BookingFormState())
    val bookingForm: StateFlow<BookingFormState> = _bookingForm.asStateFlow()

    // Backup & Offline State
    private val _backupState = MutableStateFlow(BackupState())
    val backupState: StateFlow<BackupState> = _backupState.asStateFlow()

    // Coastal & National Airport Flight Radar State
    private val _coastalRadarState = MutableStateFlow(CoastalFlightRadarState())
    val coastalRadarState: StateFlow<CoastalFlightRadarState> = _coastalRadarState.asStateFlow()

    // Live Monitored Client Flights List (FIDS / Chegadas de Clientes)
    private val _monitoredFlights = MutableStateFlow(
        listOf(
            MonitoredFlightItem(
                id = "FL-01",
                flightNumber = "PR-VIP",
                passengerName = "Dr. Christian Dahmer",
                airlineOrJetModel = "Embraer Phenom 300E",
                origin = "CGH Hangar Executivo",
                destinationAirport = CoastalAirport.GUARUJA_SANTOS,
                flightPhase = CoastalFlightPhase.COASTAL_DESCENT,
                scheduledTime = "20:30",
                estimatedArrival = "20:38",
                delayMinutes = 0,
                assignedChauffeur = "Alexandre Valença",
                assignedVehicle = "BMW 760i Protection III-A (VIP-9988)",
                gateOrHangar = "Hangar VIP Base Aérea"
            ),
            MonitoredFlightItem(
                id = "FL-02",
                flightNumber = "AF457",
                passengerName = "Embaixador Marc Dupont",
                airlineOrJetModel = "Air France Boeing 777-300ER",
                origin = "Paris CDG Terminal 2E",
                destinationAirport = CoastalAirport.GRU_VIP,
                flightPhase = CoastalFlightPhase.TOUCHDOWN,
                scheduledTime = "19:45",
                estimatedArrival = "19:42",
                delayMinutes = 0,
                assignedChauffeur = "Carlos Eduardo Siqueira",
                assignedVehicle = "Mercedes-Benz S 580 Maybach (LUX-7E88)",
                gateOrHangar = "Terminal 3 Portão VIP Embarque"
            ),
            MonitoredFlightItem(
                id = "FL-03",
                flightNumber = "LATAM 3302",
                passengerName = "Diretoria Santander Corporate",
                airlineOrJetModel = "Airbus A320neo",
                origin = "Brasília BSB Presidencial",
                destinationAirport = CoastalAirport.CONGONHAS_VIP,
                flightPhase = CoastalFlightPhase.DELAYED_WEATHER,
                scheduledTime = "21:00",
                estimatedArrival = "21:25",
                delayMinutes = 25,
                assignedChauffeur = "Fernando Montenegro",
                assignedVehicle = "Cadillac Escalade Platinum (EXE-4X40)",
                gateOrHangar = "Ala Executiva Congonhas"
            ),
            MonitoredFlightItem(
                id = "FL-04",
                flightNumber = "N788XP",
                passengerName = "Safra Private Wealth Delegation",
                airlineOrJetModel = "Gulfstream G650ER",
                origin = "New York JFK Private",
                destinationAirport = CoastalAirport.CATARINA_VIP,
                flightPhase = CoastalFlightPhase.CRUISE,
                scheduledTime = "22:15",
                estimatedArrival = "22:10",
                delayMinutes = 0,
                assignedChauffeur = "Roberto Silveira",
                assignedVehicle = "Mercedes-Benz V 300d Maybach Lounge (JET-6060)",
                gateOrHangar = "Hangar Catarina H-04"
            ),
            MonitoredFlightItem(
                id = "FL-05",
                flightNumber = "PP-HELI",
                passengerName = "Família Setubal / Corporate",
                airlineOrJetModel = "Helicóptero AgustaWestland AW109",
                origin = "Faria Lima Heliponto",
                destinationAirport = CoastalAirport.ILHABELA_HELI,
                flightPhase = CoastalFlightPhase.FINAL_APPROACH,
                scheduledTime = "19:15",
                estimatedArrival = "19:14",
                delayMinutes = 0,
                assignedChauffeur = "Alexandre Valença",
                assignedVehicle = "BMW 760i Protection III-A",
                gateOrHangar = "Heliponto Iate Clube Pátio VIP"
            )
        )
    )
    val monitoredFlights: StateFlow<List<MonitoredFlightItem>> = _monitoredFlights.asStateFlow()

    // ==========================================
    // Multi-System Enterprise Fleet Tracking & Telematics
    // Compatible with Sascar, Autotrac, Cobli, Geotab, Wialon, Traccar, Omnilink
    // ==========================================
    private val _fleetTrackingList = MutableStateFlow(
        listOf(
            VehicleTrackingData(
                vehicleId = 1L,
                plate = "VIP-9988",
                modelName = "BMW 760i xDrive Protection Nível III-A",
                assignedChauffeur = "Carlos Eduardo Siqueira",
                provider = TelematicsSystemProvider.AUTOTRAC,
                hardwareModel = "Autotrac SAT-Hybrid Pro / Terminal VIP",
                imeiOrSerial = "AT-9988-IRID-4G",
                firmwareVersion = "v4.12.8-SEC",
                simCardProvider = "Claro Corporate M2M + Iridium Satelital",
                satellites = 19,
                hdop = 0.7,
                latitude = -23.550520,
                longitude = -46.633308,
                speedKmH = 68,
                engineRpm = 1920,
                odometerKm = 28540.2,
                fuelOrBatteryPercent = 88,
                batteryVoltage = 14.1,
                coolantTempC = 90,
                instantConsumptionKmPerL = 11.2,
                ignitionStatus = IgnitionStatus.ON_MOVING,
                lockState = RemoteLockState.UNLOCKED,
                antiJammerState = AntiJammerState.NORMAL,
                geofenceState = GeofenceState.ALLOWED_CORRIDOR,
                isPanicAlarmActive = false,
                ecoDrivingScore = 98,
                harshBrakingEvents = 0,
                harshAccelerationEvents = 0,
                speedViolationEvents = 0,
                rawPacketPayload = ">SAT-AT4G;ID=VIP9988;EV=POS;LAT=-23.550520;LON=-46.633308;SPD=68;RPM=1920;IGN=1;JAM=0;LCK=0;SAT=19;HDOP=0.7;CRC=4F8A<",
                eventLogs = listOf(
                    TelemetryLogEvent("EV-101", "Agora", "AUTOTRAC", "Telemetria Híbrida 4G/Satélite Ativa", "Posição reportada via satélite Iridium sem perdas de pacote.", "SUCCESS"),
                    TelemetryLogEvent("EV-102", "12 min atrás", "AUTOTRAC", "Cerca Eletrônica VIP Reconhecida", "Entrada no Corredor Blindado Jardins-Marginal Pinheiros.", "INFO")
                )
            ),
            VehicleTrackingData(
                vehicleId = 2L,
                plate = "LUX-7E88",
                modelName = "Mercedes-Benz S 580 4MATIC Maybach",
                assignedChauffeur = "Alexandre Valença",
                provider = TelematicsSystemProvider.GEOTAB,
                hardwareModel = "Geotab GO9 Telematics Hub CAN-Bus",
                imeiOrSerial = "GEO-7E88-CANBUS",
                firmwareVersion = "v38.19.1-CAN",
                simCardProvider = "Vivo IoT Global Enterprise",
                satellites = 21,
                hdop = 0.6,
                latitude = -23.435556,
                longitude = -46.473056,
                speedKmH = 0,
                engineRpm = 780,
                odometerKm = 14210.5,
                fuelOrBatteryPercent = 92,
                batteryVoltage = 13.9,
                coolantTempC = 88,
                instantConsumptionKmPerL = 12.8,
                ignitionStatus = IgnitionStatus.ON_IDLE,
                lockState = RemoteLockState.UNLOCKED,
                antiJammerState = AntiJammerState.NORMAL,
                geofenceState = GeofenceState.AIRPORT_VIP_ZONE,
                isPanicAlarmActive = false,
                ecoDrivingScore = 99,
                harshBrakingEvents = 0,
                harshAccelerationEvents = 0,
                speedViolationEvents = 0,
                rawPacketPayload = "{\"geotab_device\":\"GO9-LUX7E88\",\"diag\":{\"rpm\":780,\"can_speed\":0,\"odometer\":14210500,\"fuel_pct\":92,\"dtc_count\":0,\"battery_volts\":13.9,\"ign\":true}}",
                eventLogs = listOf(
                    TelemetryLogEvent("EV-201", "Agora", "GEOTAB", "Diagnóstico CAN Bus 100% Saudável", "0 falhas de motor (DTC zero). Tensão 13.9V perfeitamente regulada.", "SUCCESS"),
                    TelemetryLogEvent("EV-202", "8 min atrás", "GEOTAB", "Entrada em Perímetro Aeroportuário", "Veículo posicionado no bolsão VIP do Terminal 3 GRU.", "INFO")
                )
            ),
            VehicleTrackingData(
                vehicleId = 3L,
                plate = "EXE-4X40",
                modelName = "Cadillac Escalade ESV Platinum",
                assignedChauffeur = "Fernando Montenegro",
                provider = TelematicsSystemProvider.COBLI,
                hardwareModel = "Cobli SmartLink 4G OBD + G-Force 3D",
                imeiOrSerial = "CBL-4X40-9921",
                firmwareVersion = "v7.4.2-COB",
                simCardProvider = "TIM IoT Multi-IMSI",
                satellites = 18,
                hdop = 0.8,
                latitude = -23.585500,
                longitude = -46.681500,
                speedKmH = 0,
                engineRpm = 0,
                odometerKm = 18940.0,
                fuelOrBatteryPercent = 76,
                batteryVoltage = 12.6,
                coolantTempC = 45,
                instantConsumptionKmPerL = 8.4,
                ignitionStatus = IgnitionStatus.OFF,
                lockState = RemoteLockState.UNLOCKED,
                antiJammerState = AntiJammerState.NORMAL,
                geofenceState = GeofenceState.ALLOWED_CORRIDOR,
                isPanicAlarmActive = false,
                ecoDrivingScore = 96,
                harshBrakingEvents = 0,
                harshAccelerationEvents = 0,
                speedViolationEvents = 0,
                rawPacketPayload = "POST /v1/telemetry HTTP/1.1\nHost: api.cobli.co\n{\"device_id\":\"CBL-4X40\",\"ignition\":false,\"g_force\":{\"x\":0.02,\"y\":0.01,\"z\":0.99},\"odometer\":18940,\"idle_time_min\":0}",
                eventLogs = listOf(
                    TelemetryLogEvent("EV-301", "15 min atrás", "COBLI", "Ignição Desligada na Base Faria Lima", "Parada segura detectada. Modo economia de bateria do rastreador.", "INFO")
                )
            ),
            VehicleTrackingData(
                vehicleId = 4L,
                plate = "JET-6060",
                modelName = "Mercedes-Benz V 300d Maybach Lounge",
                assignedChauffeur = "Roberto Silveira",
                provider = TelematicsSystemProvider.SASCAR,
                hardwareModel = "Sascar TCU-500 CAN Dual",
                imeiOrSerial = "SAS-6060-AVL34",
                firmwareVersion = "v3.4.11-SAS",
                simCardProvider = "Claro M2M Telemetria",
                satellites = 17,
                hdop = 0.9,
                latitude = -23.424167,
                longitude = -47.165278,
                speedKmH = 74,
                engineRpm = 1750,
                odometerKm = 32150.8,
                fuelOrBatteryPercent = 82,
                batteryVoltage = 14.0,
                coolantTempC = 89,
                instantConsumptionKmPerL = 13.5,
                ignitionStatus = IgnitionStatus.ON_MOVING,
                lockState = RemoteLockState.UNLOCKED,
                antiJammerState = AntiJammerState.NORMAL,
                geofenceState = GeofenceState.AIRPORT_VIP_ZONE,
                isPanicAlarmActive = false,
                ecoDrivingScore = 97,
                harshBrakingEvents = 0,
                harshAccelerationEvents = 0,
                speedViolationEvents = 0,
                rawPacketPayload = ">SASCAR_AVL;PLACA=JET6060;SPEED=74;RPM=1750;IGN=1;ODOM=32150;ECO_SCORE=97;LAT=-23.424167;LON=-47.165278;STATUS=NORMAL<",
                eventLogs = listOf(
                    TelemetryLogEvent("EV-401", "Agora", "SASCAR", "Eco-Driving Padrão Diamante", "Condução suave: sem aceleração ou frenagem brusca.", "SUCCESS")
                )
            ),
            VehicleTrackingData(
                vehicleId = 5L,
                plate = "ARM-8800",
                modelName = "Audi A8 L Security Blindado Nível III-A",
                assignedChauffeur = "Alexandre Valença",
                provider = TelematicsSystemProvider.OMNILINK,
                hardwareModel = "Omnilink Premium SafeLock com Anti-Jammer Tri-Band",
                imeiOrSerial = "OMN-8800-SAFE",
                firmwareVersion = "v6.1.0-RISK",
                simCardProvider = "Vivo + Claro Redundante",
                satellites = 20,
                hdop = 0.7,
                latitude = -23.928889,
                longitude = -46.299722,
                speedKmH = 52,
                engineRpm = 1520,
                odometerKm = 22400.0,
                fuelOrBatteryPercent = 90,
                batteryVoltage = 14.2,
                coolantTempC = 87,
                instantConsumptionKmPerL = 10.8,
                ignitionStatus = IgnitionStatus.ON_MOVING,
                lockState = RemoteLockState.UNLOCKED,
                antiJammerState = AntiJammerState.NORMAL,
                geofenceState = GeofenceState.AIRPORT_VIP_ZONE,
                isPanicAlarmActive = false,
                ecoDrivingScore = 99,
                harshBrakingEvents = 0,
                harshAccelerationEvents = 0,
                speedViolationEvents = 0,
                rawPacketPayload = ">OMNILINK_RISK;ID=ARM8800;STATUS=OK;JAMMER_SENSOR=0;LOCK=0;MACRO=03_CLIENTE_A_BORDO;LAT=-23.928889;LON=-46.299722;VAL=OK<",
                eventLogs = listOf(
                    TelemetryLogEvent("EV-501", "Agora", "OMNILINK", "Homologação de Gerenciadora de Risco 100%", "Checklist de segurança validado pela Pamcary/Buonny.", "SUCCESS"),
                    TelemetryLogEvent("EV-502", "5 min atrás", "OMNILINK", "Sensor Anti-Jammer em Monitoramento Contínuo", "Frequências 433MHz/GPS/GSM livres de interferência.", "INFO")
                )
            ),
            VehicleTrackingData(
                vehicleId = 6L,
                plate = "POR-9090",
                modelName = "Porsche Panamera Executive VIP",
                assignedChauffeur = "Carlos Eduardo Siqueira",
                provider = TelematicsSystemProvider.WIALON,
                hardwareModel = "Teltonika FMB640 CAN (Wialon IPS)",
                imeiOrSerial = "WIA-9090-TELT",
                firmwareVersion = "v03.28.02.Rev.00",
                simCardProvider = "Global IoT eSIM Multi-Network",
                satellites = 22,
                hdop = 0.6,
                latitude = -23.596000,
                longitude = -46.688000,
                speedKmH = 48,
                engineRpm = 1680,
                odometerKm = 11300.0,
                fuelOrBatteryPercent = 85,
                batteryVoltage = 14.1,
                coolantTempC = 91,
                instantConsumptionKmPerL = 9.8,
                ignitionStatus = IgnitionStatus.ON_MOVING,
                lockState = RemoteLockState.UNLOCKED,
                antiJammerState = AntiJammerState.NORMAL,
                geofenceState = GeofenceState.ALLOWED_CORRIDOR,
                isPanicAlarmActive = false,
                ecoDrivingScore = 98,
                harshBrakingEvents = 0,
                harshAccelerationEvents = 0,
                speedViolationEvents = 0,
                rawPacketPayload = "#D#270926;153000;2355.7600;S;04641.1800;W;48;140;760;22;14.1;1680;1;WialonIPS_2.0",
                eventLogs = listOf(
                    TelemetryLogEvent("EV-601", "Agora", "WIALON", "Pacote Wialon IPS #D# Sincronizado", "Transmissão TCP direta para servidor de telemetria.", "SUCCESS")
                )
            )
        )
    )
    val fleetTrackingList: StateFlow<List<VehicleTrackingData>> = _fleetTrackingList.asStateFlow()

    private val _selectedTrackingVehicle = MutableStateFlow<VehicleTrackingData?>(null)
    val selectedTrackingVehicle: StateFlow<VehicleTrackingData?> = _selectedTrackingVehicle.asStateFlow()

    private val _selectedProviderFilter = MutableStateFlow<TelematicsSystemProvider?>(null)
    val selectedProviderFilter: StateFlow<TelematicsSystemProvider?> = _selectedProviderFilter.asStateFlow()


    // Live Telematics simulation for real-time tracking
    private val _telematics = MutableStateFlow(
        LiveTelematics(
            currentSpeedKmH = 68,
            distanceRemainingKm = 14.2,
            etaMinutes = 18,
            driverLatitude = -23.928889,
            driverLongitude = -46.299722,
            headingDegrees = 45f,
            cabinTemperature = 21,
            wifiConnected = true,
            isArmoredSecure = true
        )
    )
    val telematics: StateFlow<LiveTelematics> = _telematics.asStateFlow()

    // Active receipt preview dialog
    private val _selectedReceiptTrip = MutableStateFlow<TripBookingEntity?>(null)
    val selectedReceiptTrip: StateFlow<TripBookingEntity?> = _selectedReceiptTrip.asStateFlow()

    // Rating dialog trip
    private val _ratingTargetTrip = MutableStateFlow<TripBookingEntity?>(null)
    val ratingTargetTrip: StateFlow<TripBookingEntity?> = _ratingTargetTrip.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
        startTelematicsSimulation()
        startCoastalFlightMonitoringRoutine()
        startFleetTelematicsLoop()
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
    }

    fun toggleThemeMode() {
        _themeMode.value = when (_themeMode.value) {
            AppThemeMode.NIGHT_TRANSFER -> AppThemeMode.CHAMPAGNE_LIGHT
            AppThemeMode.CHAMPAGNE_LIGHT -> AppThemeMode.NIGHT_TRANSFER
            AppThemeMode.SYSTEM_AUTO -> AppThemeMode.NIGHT_TRANSFER
        }
    }

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    fun setAccountType(type: String) {
        _accountType.value = type
    }

    fun updateBookingForm(update: BookingFormState.() -> BookingFormState) {
        _bookingForm.value = _bookingForm.value.update()
    }

    fun toggleAmenity(amenity: LuxuryAmenity) {
        val current = _bookingForm.value.selectedAmenities
        val updated = if (current.contains(amenity)) current - amenity else current + amenity
        _bookingForm.value = _bookingForm.value.copy(selectedAmenities = updated)
    }

    fun calculateBookingTotal(state: BookingFormState): Double {
        val base = state.selectedCategory.basePrice
        val distanceCost = state.estimatedDistanceKm * state.selectedCategory.pricePerKm
        val armorExtra = if (state.isArmored && !state.selectedCategory.isArmoredDefault) 250.0 else 0.0
        val amenitiesCost = state.selectedAmenities.sumOf { it.price }
        val airportFee = if (!state.flightNumber.isNullOrBlank()) 45.0 else 0.0
        return base + distanceCost + armorExtra + amenitiesCost + airportFee
    }

    fun confirmBooking(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val form = _bookingForm.value
            val total = calculateBookingTotal(form)
            val randomChauffeur = allChauffeurs.value.firstOrNull() ?: ChauffeurEntity(
                name = "Carlos Eduardo Siqueira",
                phone = "+55 11 99744-8822",
                badgeNumber = "ARM-019",
                experienceYears = 16,
                rating = 5.0,
                tripsCompleted = 2100,
                languages = "Português, English, Español",
                securityCertification = "Escolta VIP Tática Nível Avançado",
                status = "ON_TRIP"
            )

            val amenitiesString = form.selectedAmenities.joinToString(", ") { it.nameKey }

            val newTrip = TripBookingEntity(
                bookingCode = "LX-${(1000..9999).random()}",
                passengerName = form.passengerName,
                passengerPhone = form.passengerPhone,
                pickupAddress = form.pickupAddress,
                dropoffAddress = form.dropoffAddress,
                scheduledEpochMillis = if (form.isScheduled) form.scheduledEpochMillis else System.currentTimeMillis() + 600000,
                vehicleCategory = form.selectedCategory.id,
                vehicleModel = form.selectedCategory.sampleModel,
                vehiclePlate = "VIP-" + (1000..9999).random(),
                isArmored = form.isArmored || form.selectedCategory.isArmoredDefault,
                flightNumber = if (form.flightNumber.isNotBlank()) form.flightNumber else null,
                flightStatus = if (form.flightNumber.isNotBlank()) "Radar Litoral Ativo (${_coastalRadarState.value.flightPhase.title})" else null,
                amenitiesList = amenitiesString,
                basePrice = form.selectedCategory.basePrice,
                distanceKm = form.estimatedDistanceKm,
                totalPrice = total,
                paymentMethod = form.selectedPayment.id,
                paymentStatus = if (form.selectedPayment == PaymentType.CORPORATE_BILLING) "PENDING_BILLING" else "PAID",
                tripStatus = "EN_ROUTE",
                chauffeurName = randomChauffeur.name,
                chauffeurPhone = randomChauffeur.phone,
                chauffeurRating = randomChauffeur.rating,
                createdAt = System.currentTimeMillis()
            )

            repository.createTripBooking(newTrip)
            onSuccess()
            navigateTo("tracking")
        }
    }

    fun progressActiveTripStatus() {
        val current = activeTrip.value ?: return
        viewModelScope.launch {
            val nextStatus = when (current.tripStatus) {
                "SCHEDULED" -> "EN_ROUTE"
                "EN_ROUTE" -> "ARRIVED"
                "ARRIVED" -> "IN_PROGRESS"
                "IN_PROGRESS" -> "COMPLETED"
                else -> "COMPLETED"
            }
            repository.updateTripStatus(current.id, nextStatus)
        }
    }

    fun openReceipt(trip: TripBookingEntity) {
        _selectedReceiptTrip.value = trip
    }

    fun closeReceipt() {
        _selectedReceiptTrip.value = null
    }

    fun openRating(trip: TripBookingEntity) {
        _ratingTargetTrip.value = trip
    }

    fun closeRating() {
        _ratingTargetTrip.value = null
    }

    fun submitRating(tripId: Long, rating: Int, review: String) {
        viewModelScope.launch {
            repository.rateTrip(tripId, rating, review)
            closeRating()
        }
    }

    fun sendUserMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendSupportMessage(text, isAutomated = false, sender = "USER")
            delay(1200)
            val responseText = generateConciergeResponse(text)
            repository.sendSupportMessage(responseText, isAutomated = true, sender = "CONCIERGE")
        }
    }

    private fun generateConciergeResponse(userQuery: String): String {
        val q = userQuery.lowercase()
        return when {
            q.contains("litoral") || q.contains("praia") || q.contains("guarujá") || q.contains("santos") || q.contains("ubatuba") || q.contains("angra") ->
                "Nosso Radar Costeiro monitora a aproximação da sua aeronave no litoral em tempo real. O Chauffeur executivo já está posicionado no terminal de desembarque com a cabine blindada climatizada."
            q.contains("temperatura") || q.contains("ar") || q.contains("ac") ->
                "Anotado com prioridade VIP! Notificamos o chauffeur para manter a cabine perfeitamente refrigerada a 21°C."
            q.contains("voo") || q.contains("atraso") || q.contains("radar") || q.contains("pouso") ->
                "Nosso sistema de radar acompanha seu voo em tempo real. Não se preocupe: se houver qualquer alteração na aproximação costeira, seu chauffeur ajustará automaticamente o horário de espera sem custos extras."
            q.contains("champagne") || q.contains("bebida") || q.contains("agua") ->
                "Seu pedido especial de bebidas já foi registrado em nossa adega executiva e estará disponível a bordo."
            q.contains("blindado") || q.contains("seguranca") ->
                "Todos os nossos veículos blindados possuem certificação balística Nível III-A e nossos chauffeurs são treinados em direção tática evasiva."
            else ->
                "Sua solicitação foi recebida por nossa mesa de Operações Executivas 24h. Um especialista VIP está à sua inteira disposição."
        }
    }

    fun updateVehicleStatus(vehicleId: Long, status: String) {
        viewModelScope.launch {
            repository.updateVehicleStatus(vehicleId, status)
        }
    }

    fun updateInventoryQty(itemId: Long, newQty: Int) {
        viewModelScope.launch {
            repository.updateInventory(itemId, newQty)
        }
    }

    fun markNotificationsRead() {
        viewModelScope.launch {
            repository.markNotificationsRead()
        }
    }

    fun triggerCloudBackup() {
        viewModelScope.launch {
            _backupState.value = _backupState.value.copy(isSyncing = true)
            delay(1800)
            _backupState.value = _backupState.value.copy(
                isSyncing = false,
                lastSyncTime = "Agora mesmo (Criptografia AES-256)",
                cloudStatusMessage = "Backup em Nuvem 100% Sincronizado"
            )
        }
    }

    fun toggleOfflineMode() {
        val current = _backupState.value.isOfflineModeActive
        _backupState.value = _backupState.value.copy(
            isOfflineModeActive = !current,
            cloudStatusMessage = if (!current) "Operando em Modo Offline (Cache Local Seguro)" else "Sincronizado com Nuvem Criptografada"
        )
    }

    // ==========================================
    // Coastal & Airport Flight Radar Routines
    // ==========================================

    fun selectMonitoredFlight(flight: MonitoredFlightItem) {
        _coastalRadarState.value = _coastalRadarState.value.copy(
            flightNumber = flight.flightNumber,
            aircraftModel = flight.airlineOrJetModel,
            passengerName = flight.passengerName,
            originAirport = flight.origin,
            selectedCoastalAirport = flight.destinationAirport,
            flightPhase = flight.flightPhase,
            delayMinutes = flight.delayMinutes,
            assignedChauffeur = flight.assignedChauffeur,
            assignedVehicle = flight.assignedVehicle,
            meetSignText = "${flight.passengerName.uppercase()} • LUXEDRIVE VIP"
        )
    }

    fun setCoastalAirport(airport: CoastalAirport) {
        _coastalRadarState.value = _coastalRadarState.value.copy(selectedCoastalAirport = airport)
    }

    fun setFlightNumber(flightNumber: String) {
        _coastalRadarState.value = _coastalRadarState.value.copy(flightNumber = flightNumber)
    }

    fun simulateFlightDelay(delayMinutes: Int) {
        viewModelScope.launch {
            val airport = _coastalRadarState.value.selectedCoastalAirport
            _coastalRadarState.value = _coastalRadarState.value.copy(
                flightPhase = CoastalFlightPhase.DELAYED_WEATHER,
                delayMinutes = delayMinutes,
                etaMinutes = _coastalRadarState.value.etaMinutes + delayMinutes,
                windCondition = "Vento Costeiro 22kt Rajadas • Teto Baixo 1.200ft (Operação por Instrumentos)"
            )

            // Update flight item in list as well
            _monitoredFlights.value = _monitoredFlights.value.map {
                if (it.flightNumber == _coastalRadarState.value.flightNumber) {
                    it.copy(
                        flightPhase = CoastalFlightPhase.DELAYED_WEATHER,
                        delayMinutes = delayMinutes,
                        estimatedArrival = "Reagendado (+${delayMinutes}m)"
                    )
                } else it
            }

            // Emit Push Notification
            database.notificationDao().insertNotification(
                NotificationEntity(
                    title = "⚠️ Atraso de Voo no Aeroporto (+${delayMinutes}m)",
                    message = "Voo ${_coastalRadarState.value.flightNumber} atrasou devido às condições meteorológicas ou tráfego (${airport.region}). O horário do seu Chauffeur (${_coastalRadarState.value.assignedChauffeur}) foi reprogramado automaticamente sem taxa de espera.",
                    type = "FLIGHT_UPDATE",
                    relatedTripId = activeTrip.value?.id
                )
            )
        }
    }

    fun simulateCoastalDescent() {
        viewModelScope.launch {
            val airport = _coastalRadarState.value.selectedCoastalAirport
            _coastalRadarState.value = _coastalRadarState.value.copy(
                flightPhase = CoastalFlightPhase.COASTAL_DESCENT,
                currentAltitudeFt = 5800,
                currentSpeedKmH = 420,
                distanceToCoastKm = 12.0,
                etaMinutes = 8
            )

            _monitoredFlights.value = _monitoredFlights.value.map {
                if (it.flightNumber == _coastalRadarState.value.flightNumber) {
                    it.copy(flightPhase = CoastalFlightPhase.COASTAL_DESCENT, estimatedArrival = "Em 8 min")
                } else it
            }

            database.notificationDao().insertNotification(
                NotificationEntity(
                    title = "🛬 Descida no Espaço Aéreo",
                    message = "Aeronave ${_coastalRadarState.value.flightNumber} iniciou aproximação no ${airport.airportName}. Chauffeur posicionado no Hangar VIP.",
                    type = "FLIGHT_UPDATE",
                    relatedTripId = activeTrip.value?.id
                )
            )
        }
    }

    fun simulateCoastalLanding() {
        viewModelScope.launch {
            val airport = _coastalRadarState.value.selectedCoastalAirport
            _coastalRadarState.value = _coastalRadarState.value.copy(
                flightPhase = CoastalFlightPhase.TOUCHDOWN,
                currentAltitudeFt = 0,
                currentSpeedKmH = 45,
                distanceToCoastKm = 0.0,
                etaMinutes = 0
            )

            _monitoredFlights.value = _monitoredFlights.value.map {
                if (it.flightNumber == _coastalRadarState.value.flightNumber) {
                    it.copy(flightPhase = CoastalFlightPhase.TOUCHDOWN, estimatedArrival = "Pousou")
                } else it
            }

            database.notificationDao().insertNotification(
                NotificationEntity(
                    title = "🌟 Pouso no Aeroporto Confirmado!",
                    message = "Aeronave ${_coastalRadarState.value.flightNumber} pousou com sucesso em ${airport.airportName}. Seu Chauffeur ${_coastalRadarState.value.assignedChauffeur} está no pátio VIP aguardando o desembarque com a placa nominal.",
                    type = "FLIGHT_UPDATE",
                    relatedTripId = activeTrip.value?.id
                )
            )

            // Update active trip status if running
            val current = activeTrip.value
            if (current != null && current.tripStatus == "EN_ROUTE") {
                repository.updateTripStatus(current.id, "ARRIVED")
            }
        }
    }

    fun simulatePassengerMeet() {
        viewModelScope.launch {
            _coastalRadarState.value = _coastalRadarState.value.copy(
                flightPhase = CoastalFlightPhase.PASSENGER_MET
            )

            _monitoredFlights.value = _monitoredFlights.value.map {
                if (it.flightNumber == _coastalRadarState.value.flightNumber) {
                    it.copy(flightPhase = CoastalFlightPhase.PASSENGER_MET, estimatedArrival = "A Bordo")
                } else it
            }

            database.notificationDao().insertNotification(
                NotificationEntity(
                    title = "✨ Recepção VIP no Aeroporto Concluída",
                    message = "Passageiro a bordo do veículo executivo. Transfer VIP iniciado com sucesso.",
                    type = "TRIP_STATUS",
                    relatedTripId = activeTrip.value?.id
                )
            )

            val current = activeTrip.value
            if (current != null) {
                repository.updateTripStatus(current.id, "IN_PROGRESS")
            }
        }
    }

    fun t(key: String): String {
        return LocaleManager.getString(key, _currentLanguage.value)
    }

    private fun startTelematicsSimulation() {
        viewModelScope.launch {
            var step = 0
            while (true) {
                delay(3000)
                step++
                val active = activeTrip.value
                val speed = if (active?.tripStatus == "IN_PROGRESS" || active?.tripStatus == "EN_ROUTE") {
                    (55 + (sin(step.toDouble()) * 15).toInt()).coerceIn(40, 95)
                } else {
                    0
                }
                val dist = (_telematics.value.distanceRemainingKm - 0.4).coerceAtLeast(0.5)
                val eta = (dist / 0.8).toInt().coerceAtLeast(1)

                _telematics.value = _telematics.value.copy(
                    currentSpeedKmH = speed,
                    distanceRemainingKm = String.format("%.1f", dist).replace(',', '.').toDoubleOrNull() ?: dist,
                    etaMinutes = eta,
                    headingDegrees = ((step * 15) % 360).toFloat()
                )
            }
        }
    }

    private fun startCoastalFlightMonitoringRoutine() {
        viewModelScope.launch {
            var tick = 0
            while (true) {
                delay(5000)
                tick++
                val current = _coastalRadarState.value
                if (current.flightPhase == CoastalFlightPhase.COASTAL_DESCENT || current.flightPhase == CoastalFlightPhase.CRUISE) {
                    val newAlt = (current.currentAltitudeFt - 350).coerceAtLeast(1200)
                    val newDist = (current.distanceToCoastKm - 1.2).coerceAtLeast(0.5)
                    val newEta = (newDist / 1.5).toInt().coerceAtLeast(1)

                    _coastalRadarState.value = current.copy(
                        currentAltitudeFt = newAlt,
                        distanceToCoastKm = String.format("%.1f", newDist).replace(',', '.').toDoubleOrNull() ?: newDist,
                        etaMinutes = newEta,
                        lastRadarPulseEpoch = System.currentTimeMillis()
                    )
                }
            }
        }
    }

    // ==========================================
    // Multi-System Telematics & Fleet Tracking Operations
    // ==========================================

    fun selectTrackingVehicle(vehicle: VehicleTrackingData?) {
        _selectedTrackingVehicle.value = vehicle
    }

    fun setProviderFilter(provider: TelematicsSystemProvider?) {
        _selectedProviderFilter.value = provider
    }

    fun switchVehicleProvider(vehicleId: Long, newProvider: TelematicsSystemProvider) {
        viewModelScope.launch {
            _fleetTrackingList.value = _fleetTrackingList.value.map { v ->
                if (v.vehicleId == vehicleId) {
                    val updated = v.copy(
                        provider = newProvider,
                        hardwareModel = newProvider.defaultHardware
                    )
                    val newPayload = generateRawPacket(updated)
                    val newLog = TelemetryLogEvent(
                        id = "EV-${System.currentTimeMillis() % 10000}",
                        time = "Agora",
                        providerTag = newProvider.shortCode,
                        title = "Migração de Protocolo de Telemetria",
                        detail = "Veículo ${v.plate} agora sincronizado via ${newProvider.displayName} (${newProvider.protocolName}).",
                        severity = "SUCCESS"
                    )
                    updated.copy(
                        rawPacketPayload = newPayload,
                        eventLogs = listOf(newLog) + v.eventLogs.take(5)
                    )
                } else v
            }
            if (_selectedTrackingVehicle.value?.vehicleId == vehicleId) {
                _selectedTrackingVehicle.value = _fleetTrackingList.value.firstOrNull { it.vehicleId == vehicleId }
            }
            database.notificationDao().insertNotification(
                NotificationEntity(
                    title = "📡 Sistema de Telemetria Alterado",
                    message = "Veículo teve rastreador reconfigurado para protocolo ${newProvider.displayName} com sucesso.",
                    type = "FLEET_UPDATE"
                )
            )
        }
    }

    fun executeTrackingCommand(vehicleId: Long, command: TelemetryCommand, parameter: String = "") {
        viewModelScope.launch {
            val target = _fleetTrackingList.value.firstOrNull { it.vehicleId == vehicleId } ?: return@launch
            var notifTitle = ""
            var notifMsg = ""
            var eventTitle = ""
            var eventDetail = ""
            var eventSeverity = "INFO"

            val updatedVehicle = when (command) {
                TelemetryCommand.TOGGLE_LOCK -> {
                    val newLock = if (target.lockState == RemoteLockState.UNLOCKED) RemoteLockState.LOCKED else RemoteLockState.UNLOCKED
                    if (newLock == RemoteLockState.LOCKED) {
                        notifTitle = "🚨 Corte de Ignição Efetuado"
                        notifMsg = "Veículo ${target.plate} (${target.modelName}) teve corte elétrico de ignição acionado via ${target.provider.displayName}."
                        eventTitle = "Bloqueio de Ignição Ativado"
                        eventDetail = "Comando de bloqueio remoto executado com sucesso pela central."
                        eventSeverity = "DANGER"
                    } else {
                        notifTitle = "✅ Ignição Remota Liberada"
                        notifMsg = "Veículo ${target.plate} liberado para condução pelo Chauffeur."
                        eventTitle = "Ignição Liberada"
                        eventDetail = "Veículo pronto para partida normal."
                        eventSeverity = "SUCCESS"
                    }
                    target.copy(lockState = newLock)
                }
                TelemetryCommand.TRIGGER_PANIC_TEST -> {
                    notifTitle = "⚠️ Teste de Botão de Pânico SOS"
                    notifMsg = "Sinal de emergência silenciosa disparado do veículo ${target.plate} para a central ${target.provider.displayName}."
                    eventTitle = "Alerta de Pânico SOS Testado"
                    eventDetail = "Coordenadas de emergência transmitidas com prioridade máxima."
                    eventSeverity = "WARN"
                    target.copy(isPanicAlarmActive = !target.isPanicAlarmActive)
                }
                TelemetryCommand.TOGGLE_ANTI_JAMMER -> {
                    val newJammer = if (target.antiJammerState == AntiJammerState.NORMAL) AntiJammerState.JAMMING_DETECTED else AntiJammerState.NORMAL
                    if (newJammer == AntiJammerState.JAMMING_DETECTED) {
                        notifTitle = "🚨 ALERTA ANTI-JAMMER DETECTADO"
                        notifMsg = "Bloqueador RF detectado próximo ao veículo ${target.plate}. Protocolo de defesa blindada acionado."
                        eventTitle = "Detector Jammer Disparado"
                        eventDetail = "Ruído anômalo nas frequências 433MHz/GPS. Chauffeur alertado."
                        eventSeverity = "DANGER"
                    } else {
                        notifTitle = "🛡️ Jammer Normalizado"
                        notifMsg = "Ambiente eletromagnético normalizado para o veículo ${target.plate}."
                        eventTitle = "Anti-Jammer Seguro"
                        eventDetail = "Espectro RF restabelecido com 100% de sinal limpo."
                        eventSeverity = "SUCCESS"
                    }
                    target.copy(antiJammerState = newJammer)
                }
                TelemetryCommand.SEND_MACRO -> {
                    val macroText = parameter.ifBlank { "02 - No Hangar VIP Aguardando Desembarque" }
                    notifTitle = "📋 Macro de Bordo Enviada"
                    notifMsg = "Macro '$macroText' enviada para o terminal do veículo ${target.plate} via ${target.provider.displayName}."
                    eventTitle = "Macro Transmitida ao Chauffeur"
                    eventDetail = macroText
                    eventSeverity = "INFO"
                    target
                }
                TelemetryCommand.PING_LOCATION -> {
                    notifTitle = "📍 Ping de Localização Recebido"
                    notifMsg = "Posição de ${target.plate} atualizada: ${target.satellites} satélites com HDOP ${target.hdop}."
                    eventTitle = "Polling GPS 1-Segundo"
                    eventDetail = "Posição recalculada com telemetria milimétrica."
                    eventSeverity = "SUCCESS"
                    target.copy(lastUpdatedEpoch = System.currentTimeMillis())
                }
                TelemetryCommand.SIREN_ALARM -> {
                    notifTitle = "🚨 Sinalizador Ativado"
                    notifMsg = "Alerta sonoro e de luzes de cortesia acionado no veículo ${target.plate}."
                    eventTitle = "Acionamento de Sinalizador"
                    eventDetail = "Comando sonoro emitido para identificação rápida no bolsão VIP."
                    eventSeverity = "INFO"
                    target
                }
                TelemetryCommand.REFRESH_CAN -> {
                    notifTitle = "⚡ Leitura CAN Bus / OBD-II"
                    notifMsg = "Telemetria direta da ECU atualizada: ${target.engineRpm} RPM, Bateria ${target.batteryVoltage}V, Temp ${target.coolantTempC}°C."
                    eventTitle = "Scan de Barramento CAN Bus"
                    eventDetail = "Diagnósticos do motor sem anomalias (DTC Limpo)."
                    eventSeverity = "SUCCESS"
                    target
                }
            }

            val newLog = TelemetryLogEvent(
                id = "CMD-${System.currentTimeMillis() % 10000}",
                time = "Agora",
                providerTag = target.provider.shortCode,
                title = eventTitle,
                detail = eventDetail,
                severity = eventSeverity
            )

            val updatedWithLog = updatedVehicle.copy(
                eventLogs = listOf(newLog) + updatedVehicle.eventLogs.take(5),
                rawPacketPayload = generateRawPacket(updatedVehicle)
            )

            _fleetTrackingList.value = _fleetTrackingList.value.map {
                if (it.vehicleId == vehicleId) updatedWithLog else it
            }

            if (_selectedTrackingVehicle.value?.vehicleId == vehicleId) {
                _selectedTrackingVehicle.value = updatedWithLog
            }

            if (notifTitle.isNotBlank()) {
                database.notificationDao().insertNotification(
                    NotificationEntity(
                        title = notifTitle,
                        message = notifMsg,
                        type = "TELEMETRY_ALERT"
                    )
                )
            }
        }
    }

    private fun generateRawPacket(v: VehicleTrackingData): String {
        return when (v.provider) {
            TelematicsSystemProvider.SASCAR ->
                ">SASCAR_AVL;PLACA=${v.plate.replace("-", "")};SPD=${v.speedKmH};RPM=${v.engineRpm};IGN=${if (v.ignitionStatus != IgnitionStatus.OFF) 1 else 0};ODOM=${v.odometerKm.toInt()};ECO=${v.ecoDrivingScore};BAT=${v.batteryVoltage};LAT=${v.latitude};LON=${v.longitude}<"
            TelematicsSystemProvider.AUTOTRAC ->
                ">SAT-AT4G;ID=${v.plate.replace("-", "")};EV=CAN_REP;LAT=${v.latitude};LON=${v.longitude};SPD=${v.speedKmH};RPM=${v.engineRpm};IGN=${if (v.ignitionStatus != IgnitionStatus.OFF) 1 else 0};JAM=${if (v.antiJammerState == AntiJammerState.JAMMING_DETECTED) 1 else 0};LCK=${if (v.lockState == RemoteLockState.LOCKED) 1 else 0};SAT=${v.satellites};HDOP=${v.hdop};CRC=5F12<"
            TelematicsSystemProvider.COBLI ->
                "POST /v1/telemetry HTTP/1.1\nHost: api.cobli.co\n{\"device_id\":\"${v.imeiOrSerial}\",\"plate\":\"${v.plate}\",\"ignition\":${v.ignitionStatus != IgnitionStatus.OFF},\"speed\":${v.speedKmH},\"fuel_level\":${v.fuelOrBatteryPercent},\"score\":${v.ecoDrivingScore}}"
            TelematicsSystemProvider.GEOTAB ->
                "{\"geotab_hub\":\"${v.imeiOrSerial}\",\"plate\":\"${v.plate}\",\"diag\":{\"rpm\":${v.engineRpm},\"can_speed\":${v.speedKmH},\"odometer\":${(v.odometerKm * 1000).toInt()},\"coolant_c\":${v.coolantTempC},\"volts\":${v.batteryVoltage},\"dtc_zero\":true}}"
            TelematicsSystemProvider.WIALON ->
                "#D#270926;154500;${v.latitude};${v.longitude};${v.speedKmH};${v.engineRpm};${v.satellites};${v.batteryVoltage};${v.fuelOrBatteryPercent};WialonIPS_2.0"
            TelematicsSystemProvider.TRACCAR ->
                "TELTONIKA_AVL_CODEC8: 000000000000004208010000016B${v.plate.replace("-", "")}0101${v.speedKmH}${v.engineRpm}FFFF"
            TelematicsSystemProvider.OMNILINK ->
                ">OMNILINK_RISK;ID=${v.plate.replace("-", "")};STATUS=OK;JAM=${if (v.antiJammerState == AntiJammerState.JAMMING_DETECTED) 1 else 0};LOCK=${if (v.lockState == RemoteLockState.LOCKED) 1 else 0};BAT=${v.batteryVoltage}V;VAL=CERTIFIED<"
        }
    }

    private fun startFleetTelematicsLoop() {
        viewModelScope.launch {
            var step = 0
            while (true) {
                delay(4000)
                step++
                _fleetTrackingList.value = _fleetTrackingList.value.map { v ->
                    if (v.ignitionStatus == IgnitionStatus.ON_MOVING) {
                        val deltaLat = (sin(step.toDouble() * 0.1) * 0.0003)
                        val deltaLon = (sin((step + 2).toDouble() * 0.1) * 0.0003)
                        val spd = (50 + (sin(step.toDouble()) * 20).toInt()).coerceIn(30, 95)
                        val rpm = (1400 + spd * 12).coerceIn(1200, 2600)
                        val odo = v.odometerKm + 0.05
                        v.copy(
                            latitude = v.latitude + deltaLat,
                            longitude = v.longitude + deltaLon,
                            speedKmH = spd,
                            engineRpm = rpm,
                            odometerKm = String.format("%.1f", odo).replace(',', '.').toDoubleOrNull() ?: odo,
                            lastUpdatedEpoch = System.currentTimeMillis()
                        )
                    } else if (v.ignitionStatus == IgnitionStatus.ON_IDLE) {
                        v.copy(
                            engineRpm = 750 + (step % 4) * 15,
                            lastUpdatedEpoch = System.currentTimeMillis()
                        )
                    } else {
                        v
                    }
                }
                val currSelected = _selectedTrackingVehicle.value
                if (currSelected != null) {
                    _selectedTrackingVehicle.value = _fleetTrackingList.value.firstOrNull { it.vehicleId == currSelected.vehicleId }
                }
            }
        }
    }
}

