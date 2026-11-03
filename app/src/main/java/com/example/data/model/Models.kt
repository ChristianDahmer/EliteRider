package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class VehicleCategory(
    val id: String,
    val titleKey: String,
    val descKey: String,
    val sampleModel: String,
    val basePrice: Double,
    val pricePerKm: Double,
    val passengerCapacity: Int,
    val luggageCapacity: Int,
    val isArmoredDefault: Boolean
) {
    FIRST_CLASS_SEDAN(
        id = "FIRST_CLASS_SEDAN",
        titleKey = "first_class_sedan",
        descKey = "first_class_sedan_desc",
        sampleModel = "Mercedes-Benz S 580 4MATIC",
        basePrice = 180.0,
        pricePerKm = 8.5,
        passengerCapacity = 3,
        luggageCapacity = 3,
        isArmoredDefault = false
    ),
    EXECUTIVE_SUV(
        id = "EXECUTIVE_SUV",
        titleKey = "executive_suv",
        descKey = "executive_suv_desc",
        sampleModel = "Cadillac Escalade Platinum / Range Rover",
        basePrice = 240.0,
        pricePerKm = 11.0,
        passengerCapacity = 5,
        luggageCapacity = 5,
        isArmoredDefault = false
    ),
    ARMORED_VIP(
        id = "ARMORED_VIP",
        titleKey = "armored_vip",
        descKey = "armored_vip_desc",
        sampleModel = "BMW 760i Protection Nível III-A",
        basePrice = 450.0,
        pricePerKm = 18.0,
        passengerCapacity = 3,
        luggageCapacity = 3,
        isArmoredDefault = true
    ),
    EXECUTIVE_VAN(
        id = "EXECUTIVE_VAN",
        titleKey = "executive_van",
        descKey = "executive_van_desc",
        sampleModel = "Mercedes-Benz V 300d Maybach Edition",
        basePrice = 320.0,
        pricePerKm = 14.0,
        passengerCapacity = 6,
        luggageCapacity = 7,
        isArmoredDefault = false
    )
}

enum class LuxuryAmenity(
    val id: String,
    val nameKey: String,
    val price: Double,
    val iconName: String
) {
    WIFI("wifi", "amenity_wifi", 0.0, "Wifi"),
    SPARKLING_WATER("water", "amenity_water", 0.0, "LocalDrink"),
    CHAMPAGNE("champagne", "amenity_champagne", 150.0, "Celebration"),
    BILINGUAL_CHAUFFEUR("bilingual", "amenity_bilingual", 0.0, "Translate"),
    TERMINAL_MEET("terminal_meet", "amenity_terminal_meet", 50.0, "FlightTakeoff"),
    CLIMATE_PRECONDITION("ac_temp", "amenity_ac_temp", 0.0, "AcUnit"),
    PRIVACY_SCREEN("privacy_screen", "amenity_privacy_screen", 0.0, "Shield"),
    SECURITY_ESCORT("security_escort", "amenity_security_escort", 380.0, "Security")
}

enum class PaymentType(
    val id: String,
    val labelKey: String,
    val iconName: String
) {
    CORPORATE_BILLING("CORPORATE", "pay_corporate", "Business"),
    CREDIT_CARD("CREDIT_CARD", "pay_credit_card", "CreditCard"),
    PIX("PIX", "pay_pix", "QrCode2"),
    VIP_VOUCHER("VIP_VOUCHER", "pay_crypto", "Token"),
    GOOGLE_PAY("GOOGLE_PAY", "pay_google_pay", "Payments")
}

enum class AirportRegionCategory(val label: String) {
    ALL("Todos"),
    COASTAL("Litoral & Praias"),
    EXECUTIVE_HUBS("Hubs Executivos"),
    PRIVATE_JETS("Jatos Privados")
}

enum class CoastalAirport(
    val code: String,
    val icao: String,
    val airportName: String,
    val region: String,
    val runwayLength: String,
    val category: AirportRegionCategory,
    val lat: Double,
    val lng: Double
) {
    GUARUJA_SANTOS(
        code = "SSZ",
        icao = "SBDN",
        airportName = "Aeroporto de Guarujá / Santos (Base Aérea)",
        region = "Baixada Santista • SP",
        runwayLength = "1.390m Asfalto VIP",
        category = AirportRegionCategory.COASTAL,
        lat = -23.928889,
        lng = -46.299722
    ),
    UBATUBA(
        code = "UBT",
        icao = "SDUB",
        airportName = "Aeroporto Estadual de Ubatuba",
        region = "Litoral Norte • SP",
        runwayLength = "940m Asfalto Executivo",
        category = AirportRegionCategory.COASTAL,
        lat = -23.441944,
        lng = -45.074722
    ),
    ANGRA_PARATY(
        code = "JDR",
        icao = "SDAG",
        airportName = "Aeroporto de Angra dos Reis / Paraty",
        region = "Costa Verde • RJ",
        runwayLength = "1.012m Pista Litorânea",
        category = AirportRegionCategory.COASTAL,
        lat = -22.975278,
        lng = -44.307222
    ),
    CABO_FRIO(
        code = "CFB",
        icao = "SBCB",
        airportName = "Aeroporto Internacional de Cabo Frio",
        region = "Região dos Lagos • Búzios • RJ",
        runwayLength = "2.560m Internacional",
        category = AirportRegionCategory.COASTAL,
        lat = -22.923889,
        lng = -42.074444
    ),
    ILHABELA_HELI(
        code = "ILH",
        icao = "SIBH",
        airportName = "Heliponto VIP Iate Clube Ilhabela",
        region = "Ilhabela • Litoral Norte • SP",
        runwayLength = "Heliponto Homologado Noturno",
        category = AirportRegionCategory.COASTAL,
        lat = -23.778056,
        lng = -45.358056
    ),
    FLORIANOPOLIS(
        code = "FLN",
        icao = "SBFL",
        airportName = "Aeroporto Internacional de Florianópolis (Terminal VIP)",
        region = "Litoral de Santa Catarina • SC",
        runwayLength = "2.400m Jet VIP",
        category = AirportRegionCategory.COASTAL,
        lat = -27.670556,
        lng = -48.5525
    ),
    NAVEGANTES(
        code = "NVT",
        icao = "SBNF",
        airportName = "Aeroporto Internacional de Navegantes / Balneário Camboriú",
        region = "Costa Esmeralda • SC",
        runwayLength = "1.701m Executivo",
        category = AirportRegionCategory.COASTAL,
        lat = -26.880278,
        lng = -48.651389
    ),
    GRU_VIP(
        code = "GRU",
        icao = "SBGR",
        airportName = "Aeroporto Internacional de Guarulhos (Terminal 3 VIP)",
        region = "São Paulo Hub Internacional • SP",
        runwayLength = "3.700m Internacional",
        category = AirportRegionCategory.EXECUTIVE_HUBS,
        lat = -23.435556,
        lng = -46.473056
    ),
    CONGONHAS_VIP(
        code = "CGH",
        icao = "SBSP",
        airportName = "Aeroporto de Congonhas (Hangar Executivo VIP)",
        region = "São Paulo Centro Executivo • SP",
        runwayLength = "1.940m Asfalto Ranhurado",
        category = AirportRegionCategory.EXECUTIVE_HUBS,
        lat = -23.626111,
        lng = -46.656389
    ),
    CATARINA_VIP(
        code = "JHF",
        icao = "SBJH",
        airportName = "São Paulo Catarina International Executive Airport",
        region = "São Roque / Alphaville • SP",
        runwayLength = "2.470m Jatos Intercontinentais",
        category = AirportRegionCategory.PRIVATE_JETS,
        lat = -23.424167,
        lng = -47.165278
    )
}

enum class CoastalFlightPhase(
    val title: String,
    val description: String,
    val badgeColor: Long = 0xFF10B981
) {
    CRUISE("Em Rota de Cruzeiro", "Navegação estável a 28.000 pés - Rumo ao destino", 0xFF38BDF8),
    COASTAL_DESCENT("Descida no Espaço Aéreo", "Início de aproximação - 6.500 pés rumo à pista", 0xFF6366F1),
    DELAYED_WEATHER("Atraso Meteorológico / Tráfego", "Teto baixo ou tráfego aéreo. Chauffeur notificado com tolerância de espera gratuita.", 0xFFF59E0B),
    FINAL_APPROACH("Aproximação Final na Pista", "Trem de pouso baixado e travado - Pouso em 3 minutos", 0xFFEAB308),
    TOUCHDOWN("Aeronave Pousou na Pista", "Taxiando para o Hangar Executivo / Terminal VIP", 0xFF10B981),
    PASSENGER_MET("Recepção VIP Concluída", "Passageiro a bordo do blindado climatizado com Champagne", 0xFFD4AF37)
}

data class MonitoredFlightItem(
    val id: String,
    val flightNumber: String,
    val passengerName: String,
    val airlineOrJetModel: String,
    val origin: String,
    val destinationAirport: CoastalAirport,
    val flightPhase: CoastalFlightPhase,
    val scheduledTime: String,
    val estimatedArrival: String,
    val delayMinutes: Int,
    val assignedChauffeur: String,
    val assignedVehicle: String,
    val gateOrHangar: String
)

data class CoastalFlightRadarState(
    val flightNumber: String = "PR-VIP (Phenom 300E)",
    val aircraftModel: String = "Embraer Phenom 300E Luxury Jet",
    val passengerName: String = "Dr. Christian Dahmer",
    val originAirport: String = "São Paulo Congonhas (CGH) - Hangar Executivo",
    val selectedCoastalAirport: CoastalAirport = CoastalAirport.GUARUJA_SANTOS,
    val flightPhase: CoastalFlightPhase = CoastalFlightPhase.COASTAL_DESCENT,
    val currentAltitudeFt: Int = 6800,
    val currentSpeedKmH: Int = 460,
    val distanceToCoastKm: Double = 18.4,
    val etaMinutes: Int = 12,
    val delayMinutes: Int = 0,
    val windCondition: String = "12 kt Sudeste (135°) • Teto Operacional CAVOK",
    val temperatureC: Int = 26,
    val assignedChauffeur: String = "Alexandre Valença",
    val assignedVehicle: String = "BMW 760i Protection Nível III-A (VIP-9988)",
    val meetSignText: String = "DR. CHRISTIAN DAHMER • LUXEDRIVE VIP",
    val isAutoSyncActive: Boolean = true,
    val lastRadarPulseEpoch: Long = System.currentTimeMillis()
)

data class RoutePoint(
    val lat: Double,
    val lng: Double,
    val address: String
)

data class LiveTelematics(
    val currentSpeedKmH: Int,
    val distanceRemainingKm: Double,
    val etaMinutes: Int,
    val driverLatitude: Double,
    val driverLongitude: Double,
    val headingDegrees: Float,
    val cabinTemperature: Int = 21,
    val wifiConnected: Boolean = true,
    val isArmoredSecure: Boolean = true
)

data class FinancialMetrics(
    val monthlyTotalRevenue: Double,
    val ridesCount: Int,
    val averageTicket: Double,
    val corporatePercentage: Double,
    val growthRatePercent: Double,
    val revenueByVehicle: Map<String, Double>,
    val monthlyHistory: List<Pair<String, Double>>
)

enum class TelematicsSystemProvider(
    val displayName: String,
    val protocolName: String,
    val shortCode: String,
    val description: String,
    val defaultHardware: String
) {
    SASCAR(
        displayName = "Sascar / Michelin",
        protocolName = "Sascar AVL Telematics v3.4",
        shortCode = "SASCAR",
        description = "Telemetria avançada, Eco-Drive e monitoramento de ignição/paradas",
        defaultHardware = "Sascar TCU-500 CAN Dual"
    ),
    AUTOTRAC(
        displayName = "Autotrac Prime",
        protocolName = "Autotrac Superfleet Híbrido",
        shortCode = "AUTOTRAC",
        description = "Satelital Iridium + 4G, macros de bordo e controle de teclado de segurança",
        defaultHardware = "Autotrac SAT-Hybrid Pro"
    ),
    COBLI(
        displayName = "Cobli Fleet IoT",
        protocolName = "Cobli REST Telemetry API",
        shortCode = "COBLI",
        description = "Sensor inercial 3D (G-Force), videotelemática e gestão de tempo ocioso",
        defaultHardware = "Cobli SmartLink 4G OBD"
    ),
    GEOTAB(
        displayName = "Geotab GO9",
        protocolName = "Geotab Curve Logging CAN Bus",
        shortCode = "GEOTAB",
        description = "Diagnósticos OBD-II profundos, RPM, DTC e telemetria de fábrica",
        defaultHardware = "Geotab GO9 Telematics Hub"
    ),
    WIALON(
        displayName = "Wialon (Gurtam)",
        protocolName = "Wialon IPS 2.0 Universal",
        shortCode = "WIALON",
        description = "Protocolo universal aberto de rastreamento com telemetria TCP/UDP",
        defaultHardware = "Teltonika FMB640 CAN"
    ),
    TRACCAR(
        displayName = "Traccar GPS",
        protocolName = "Teltonika Codec 8 / 8E Extended",
        shortCode = "TRACCAR",
        description = "Hub GPS multi-hardware (Suntech, Teltonika, Queclink, Meitrack)",
        defaultHardware = "Suntech ST340 / Teltonika"
    ),
    OMNILINK(
        displayName = "Omnilink / Risco",
        protocolName = "Omnilink Risk Manager Protocol",
        shortCode = "OMNILINK",
        description = "Homologação de Gerenciadoras de Risco (Buonny/Pamcary), anti-jammer e trava",
        defaultHardware = "Omnilink Premium SafeLock"
    )
}

enum class IgnitionStatus(val label: String, val colorHex: Long) {
    OFF("Desligada", 0xFF94A3B8),
    ON_IDLE("Ligada (Parado)", 0xFFF59E0B),
    ON_MOVING("Ligada (Em Movimento)", 0xFF10B981)
}

enum class RemoteLockState(val label: String, val colorHex: Long) {
    UNLOCKED("Liberado", 0xFF10B981),
    LOCK_PENDING("Bloqueio Enviado...", 0xFFF59E0B),
    LOCKED("Ignição Cortada / Bloqueado", 0xFFEF4444)
}

enum class AntiJammerState(val label: String, val colorHex: Long) {
    NORMAL("Normal (Sem Interferência)", 0xFF10B981),
    JAMMING_DETECTED("Alerta: Bloqueador RF Detectado!", 0xFFEF4444)
}

enum class GeofenceState(val label: String, val colorHex: Long) {
    ALLOWED_CORRIDOR("Dentro do Perímetro VIP Autorizado", 0xFF10B981),
    AIRPORT_VIP_ZONE("Área Aeroportuária / Hangar VIP", 0xFF38BDF8),
    DEVIATION_ALERT("Alerta: Desvio de Rota Não Planejado!", 0xFFEF4444)
}

enum class TelemetryCommand(val label: String, val iconKey: String) {
    PING_LOCATION("Solicitar Posição Imediata (Ping)", "LocationOn"),
    TOGGLE_LOCK("Bloquear / Liberar Ignição Remota", "Lock"),
    TRIGGER_PANIC_TEST("Teste de Botão de Pânico Silencioso", "Warning"),
    SIREN_ALARM("Acionar Alarme Sonoro / Visual", "NotificationsActive"),
    SEND_MACRO("Enviar Macro de Viagem de Bordo", "Send"),
    REFRESH_CAN("Atualizar Leitura de Barramento CAN", "Sensors"),
    TOGGLE_ANTI_JAMMER("Simular Detecção Anti-Jammer", "Security")
}

data class TelemetryLogEvent(
    val id: String,
    val time: String,
    val providerTag: String,
    val title: String,
    val detail: String,
    val severity: String // "INFO", "SUCCESS", "WARN", "DANGER"
)

data class VehicleTrackingData(
    val vehicleId: Long,
    val plate: String,
    val modelName: String,
    val assignedChauffeur: String,
    val provider: TelematicsSystemProvider,
    val hardwareModel: String,
    val imeiOrSerial: String,
    val firmwareVersion: String,
    val simCardProvider: String,
    val satellites: Int,
    val hdop: Double,
    val latitude: Double,
    val longitude: Double,
    val speedKmH: Int,
    val engineRpm: Int,
    val odometerKm: Double,
    val fuelOrBatteryPercent: Int,
    val batteryVoltage: Double,
    val coolantTempC: Int,
    val instantConsumptionKmPerL: Double,
    val ignitionStatus: IgnitionStatus,
    val lockState: RemoteLockState,
    val antiJammerState: AntiJammerState,
    val geofenceState: GeofenceState,
    val isPanicAlarmActive: Boolean,
    val ecoDrivingScore: Int,
    val harshBrakingEvents: Int,
    val harshAccelerationEvents: Int,
    val speedViolationEvents: Int,
    val rawPacketPayload: String,
    val lastUpdatedEpoch: Long = System.currentTimeMillis(),
    val eventLogs: List<TelemetryLogEvent> = emptyList()
)

enum class AppThemeMode(
    val label: String,
    val subtitle: String,
    val isDarkDefault: Boolean
) {
    NIGHT_TRANSFER(
        label = "Noturno VIP (OLED)",
        subtitle = "Preto profundo Obsidian com redução de ofuscamento para viagens noturnas",
        isDarkDefault = true
    ),
    CHAMPAGNE_LIGHT(
        label = "Champagne Light",
        subtitle = "Tons claros sofisticados de marfim e ouro para transfers diurnos",
        isDarkDefault = false
    ),
    SYSTEM_AUTO(
        label = "Automático (Sistema)",
        subtitle = "Sincroniza automaticamente com as preferências de iluminação do Android",
        isDarkDefault = true
    )
}


