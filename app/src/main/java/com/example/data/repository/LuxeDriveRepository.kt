package com.example.data.repository

import com.example.data.local.dao.*
import com.example.data.local.entities.*
import com.example.data.model.VehicleCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class LuxeDriveRepository(
    private val tripDao: TripDao,
    private val fleetDao: FleetDao,
    private val financialDao: FinancialDao,
    private val notificationDao: NotificationDao,
    private val supportDao: SupportDao
) {
    val allTrips: Flow<List<TripBookingEntity>> = tripDao.getAllTrips()
    val activeTrip: Flow<TripBookingEntity?> = tripDao.getActiveTrip()
    val allVehicles: Flow<List<FleetVehicleEntity>> = fleetDao.getAllVehicles()
    val allChauffeurs: Flow<List<ChauffeurEntity>> = fleetDao.getAllChauffeurs()
    val allInventory: Flow<List<FleetInventoryEntity>> = fleetDao.getAllInventory()
    val allTransactions: Flow<List<FinancialTransactionEntity>> = financialDao.getAllTransactions()
    val allNotifications: Flow<List<NotificationEntity>> = notificationDao.getAllNotifications()
    val allSupportMessages: Flow<List<SupportMessageEntity>> = supportDao.getAllMessages()

    suspend fun createTripBooking(trip: TripBookingEntity): Long {
        val tripId = tripDao.insertTrip(trip)
        
        // Add corresponding financial transaction
        financialDao.insertTransaction(
            FinancialTransactionEntity(
                tripId = tripId,
                invoiceNumber = "INV-${System.currentTimeMillis().toString().takeLast(6)}",
                clientName = trip.passengerName,
                accountType = if (trip.paymentMethod == "CORPORATE") "CORPORATE" else "INDIVIDUAL",
                amount = trip.totalPrice,
                paymentMethod = trip.paymentMethod,
                category = trip.vehicleCategory,
                dateEpochMillis = System.currentTimeMillis(),
                status = if (trip.paymentMethod == "CORPORATE") "PENDING_BILLING" else "PAID"
            )
        )

        // Add real-time notification
        notificationDao.insertNotification(
            NotificationEntity(
                title = "Reserva VIP Confirmada",
                message = "Transfer ${trip.bookingCode} com ${trip.vehicleModel} agendado para ${trip.pickupAddress}.",
                type = "TRIP_STATUS",
                relatedTripId = tripId
            )
        )

        return tripId
    }

    suspend fun updateTripStatus(tripId: Long, status: String) {
        tripDao.updateTripStatus(tripId, status)
        val statusLabel = when (status) {
            "EN_ROUTE" -> "Chauffeur a caminho do ponto de embarque."
            "ARRIVED" -> "Chauffeur chegou ao local. Portão VIP pronto."
            "IN_PROGRESS" -> "Em deslocamento executivo com destino confirmado."
            "COMPLETED" -> "Transfer finalizado. Obrigado pela preferência."
            else -> "Status atualizado: $status"
        }
        notificationDao.insertNotification(
            NotificationEntity(
                title = "Atualização de Transfer",
                message = statusLabel,
                type = "TRIP_STATUS",
                relatedTripId = tripId
            )
        )
    }

    suspend fun rateTrip(tripId: Long, rating: Int, review: String) {
        tripDao.updateTripRating(tripId, rating, review)
    }

    suspend fun updateVehicleStatus(vehicleId: Long, status: String) {
        fleetDao.updateVehicleStatus(vehicleId, status)
    }

    suspend fun updateInventory(itemId: Long, newQuantity: Int) {
        fleetDao.updateInventoryQty(itemId, newQuantity)
    }

    suspend fun markNotificationsRead() {
        notificationDao.markAllAsRead()
    }

    suspend fun sendSupportMessage(text: String, isAutomated: Boolean = false, sender: String = "USER") {
        supportDao.insertMessage(
            SupportMessageEntity(
                sender = sender,
                text = text,
                timestamp = System.currentTimeMillis(),
                isAutomated = isAutomated
            )
        )
    }

    suspend fun seedInitialDataIfEmpty() {
        val existingVehicles = allVehicles.firstOrNull()
        if (existingVehicles.isNullOrEmpty()) {
            val sampleVehicles = listOf(
                FleetVehicleEntity(
                    plate = "LUX-7E88",
                    modelName = "Mercedes-Benz S 580 4MATIC Maybach Edition",
                    category = VehicleCategory.FIRST_CLASS_SEDAN.id,
                    year = 2025,
                    color = "Obsidian Black Metallic",
                    isArmored = false,
                    ballisticLevel = null,
                    status = "AVAILABLE",
                    currentMileage = 14200,
                    nextMaintenanceKm = 20000,
                    assignedChauffeur = "Alexandre Valença",
                    fuelOrBatteryPercent = 94,
                    currentLocationName = "Hotel Unique / Jardins"
                ),
                FleetVehicleEntity(
                    plate = "VIP-9988",
                    modelName = "BMW 760i xDrive Protection",
                    category = VehicleCategory.ARMORED_VIP.id,
                    year = 2024,
                    color = "Black Sapphire",
                    isArmored = true,
                    ballisticLevel = "Nível III-A Certificado Exército",
                    status = "IN_SERVICE",
                    currentMileage = 28500,
                    nextMaintenanceKm = 30000,
                    assignedChauffeur = "Carlos Eduardo Siqueira",
                    fuelOrBatteryPercent = 88,
                    currentLocationName = "Aeroporto GRU Terminal 3 VIP"
                ),
                FleetVehicleEntity(
                    plate = "EXE-4X40",
                    modelName = "Cadillac Escalade ESV Platinum",
                    category = VehicleCategory.EXECUTIVE_SUV.id,
                    year = 2025,
                    color = "Raven Black",
                    isArmored = false,
                    ballisticLevel = null,
                    status = "AVAILABLE",
                    currentMileage = 18900,
                    nextMaintenanceKm = 25000,
                    assignedChauffeur = "Fernando Montenegro",
                    fuelOrBatteryPercent = 76,
                    currentLocationName = "Faria Lima Financial Center"
                ),
                FleetVehicleEntity(
                    plate = "JET-6060",
                    modelName = "Mercedes-Benz V 300d Maybach Lounge",
                    category = VehicleCategory.EXECUTIVE_VAN.id,
                    year = 2024,
                    color = "Deep Navy / Obsidian",
                    isArmored = false,
                    ballisticLevel = null,
                    status = "AVAILABLE",
                    currentMileage = 32100,
                    nextMaintenanceKm = 40000,
                    assignedChauffeur = "Roberto Silveira",
                    fuelOrBatteryPercent = 82,
                    currentLocationName = "Hangar Executivo Catarina"
                )
            )
            fleetDao.insertVehicles(sampleVehicles)

            val sampleChauffeurs = listOf(
                ChauffeurEntity(
                    name = "Alexandre Valença",
                    phone = "+55 11 98822-1010",
                    badgeNumber = "VIP-042",
                    experienceYears = 12,
                    rating = 4.98,
                    tripsCompleted = 1420,
                    languages = "Português, English, Français",
                    securityCertification = "Direção Defensiva & Evasiva Tática",
                    status = "ACTIVE"
                ),
                ChauffeurEntity(
                    name = "Carlos Eduardo Siqueira",
                    phone = "+55 11 99744-8822",
                    badgeNumber = "ARM-019",
                    experienceYears = 16,
                    rating = 5.0,
                    tripsCompleted = 2100,
                    languages = "Português, English, Español",
                    securityCertification = "Escolta VIP Tática Nível Avançado",
                    status = "ON_TRIP"
                ),
                ChauffeurEntity(
                    name = "Fernando Montenegro",
                    phone = "+55 11 97133-5566",
                    badgeNumber = "VIP-088",
                    experienceYears = 9,
                    rating = 4.95,
                    tripsCompleted = 980,
                    languages = "Português, English",
                    securityCertification = "Conduta Executiva & Protocolo Internacional",
                    status = "ACTIVE"
                )
            )
            fleetDao.insertChauffeurs(sampleChauffeurs)

            val sampleInventory = listOf(
                FleetInventoryEntity(itemName = "Água San Pellegrino 500ml", category = "BEVERAGE", quantityInStock = 148, minThreshold = 40, unit = "un"),
                FleetInventoryEntity(itemName = "Champagne Moët & Chandon Brut", category = "PREMIUM_BEVERAGE", quantityInStock = 32, minThreshold = 10, unit = "garrafas"),
                FleetInventoryEntity(itemName = "Lenços Umedecidos Luxury Oshibori", category = "HYGIENE", quantityInStock = 220, minThreshold = 50, unit = "packs"),
                FleetInventoryEntity(itemName = "Cabos de Carregamento Ultra Rápido MagSafe", category = "TECH", quantityInStock = 18, minThreshold = 6, unit = "un")
            )
            fleetDao.insertInventory(sampleInventory)

            val now = System.currentTimeMillis()
            val sampleTrips = listOf(
                TripBookingEntity(
                    bookingCode = "LX-8942",
                    passengerName = "Dr. Christian Dahmer",
                    passengerPhone = "+55 11 99123-4567",
                    pickupAddress = "Hotel Fasano Jardins - R. Vittorio Fasano, 88",
                    dropoffAddress = "Aeroporto GRU Terminal 3 - Portão VIP Embarque",
                    scheduledEpochMillis = now + 1800000, // in 30 mins
                    vehicleCategory = VehicleCategory.ARMORED_VIP.id,
                    vehicleModel = "BMW 760i xDrive Protection",
                    vehiclePlate = "VIP-9988",
                    isArmored = true,
                    flightNumber = "AF457",
                    flightStatus = "No Horário (Partida 22:45)",
                    amenitiesList = "Wi-Fi 5G, Champagne Moët & Chandon, Chauffeur Poliglota, Blindagem Nível III-A",
                    basePrice = 450.0,
                    distanceKm = 34.5,
                    totalPrice = 1221.0,
                    paymentMethod = "CORPORATE",
                    paymentStatus = "AUTHORIZED",
                    tripStatus = "EN_ROUTE",
                    chauffeurName = "Carlos Eduardo Siqueira",
                    chauffeurPhone = "+55 11 99744-8822",
                    chauffeurRating = 5.0,
                    ratingGiven = null,
                    reviewFeedback = null,
                    createdAt = now - 3600000
                ),
                TripBookingEntity(
                    bookingCode = "LX-7721",
                    passengerName = "Embaixador Marc Dupont",
                    passengerPhone = "+33 6 12 34 56 78",
                    pickupAddress = "Consulado Geral da França - Av. Paulista",
                    dropoffAddress = "Palácio dos Bandeirantes - Morumbi",
                    scheduledEpochMillis = now - 86400000,
                    vehicleCategory = VehicleCategory.FIRST_CLASS_SEDAN.id,
                    vehicleModel = "Mercedes-Benz S 580 4MATIC Maybach Edition",
                    vehiclePlate = "LUX-7E88",
                    isArmored = false,
                    flightNumber = null,
                    flightStatus = null,
                    amenitiesList = "Wi-Fi 5G, Água Sparkling San Pellegrino, Climatização 21°C",
                    basePrice = 180.0,
                    distanceKm = 16.2,
                    totalPrice = 317.7,
                    paymentMethod = "CREDIT_CARD",
                    paymentStatus = "PAID",
                    tripStatus = "COMPLETED",
                    chauffeurName = "Alexandre Valença",
                    chauffeurPhone = "+55 11 98822-1010",
                    chauffeurRating = 4.98,
                    ratingGiven = 5,
                    reviewFeedback = "Service exceptionnel, chauffeur très courtois et véhicule impeccable.",
                    createdAt = now - 90000000,
                    completedAt = now - 82800000
                ),
                TripBookingEntity(
                    bookingCode = "LX-6610",
                    passengerName = "Diretoria Executiva Santander Corporate",
                    passengerPhone = "+55 11 98765-4321",
                    pickupAddress = "Torre Santander - Av. Pres. Juscelino Kubitschek",
                    dropoffAddress = "Hangar Executivo Catarina Airport",
                    scheduledEpochMillis = now - 172800000,
                    vehicleCategory = VehicleCategory.EXECUTIVE_VAN.id,
                    vehicleModel = "Mercedes-Benz V 300d Maybach Lounge",
                    vehiclePlate = "JET-6060",
                    isArmored = false,
                    flightNumber = "N788XP (Private Jet)",
                    flightStatus = "Aeronave em solo",
                    amenitiesList = "Wi-Fi 5G, Divisória de Privacidade, Água San Pellegrino",
                    basePrice = 320.0,
                    distanceKm = 52.0,
                    totalPrice = 1048.0,
                    paymentMethod = "CORPORATE",
                    paymentStatus = "PAID",
                    tripStatus = "COMPLETED",
                    chauffeurName = "Roberto Silveira",
                    chauffeurPhone = "+55 11 98111-2233",
                    chauffeurRating = 4.96,
                    ratingGiven = 5,
                    reviewFeedback = "Pontualidade britânica e conforto absoluto para nosso conselho executivo.",
                    createdAt = now - 180000000,
                    completedAt = now - 168000000
                )
            )

            sampleTrips.forEach { trip ->
                tripDao.insertTrip(trip)
            }

            val sampleTransactions = listOf(
                FinancialTransactionEntity(
                    tripId = 1,
                    invoiceNumber = "INV-2026-0894",
                    clientName = "Dr. Christian Dahmer / Corporate",
                    accountType = "CORPORATE",
                    amount = 1221.0,
                    paymentMethod = "CORPORATE",
                    category = "ARMORED_VIP",
                    dateEpochMillis = now,
                    status = "PENDING_BILLING"
                ),
                FinancialTransactionEntity(
                    tripId = 2,
                    invoiceNumber = "INV-2026-0772",
                    clientName = "Embaixador Marc Dupont",
                    accountType = "INDIVIDUAL",
                    amount = 317.7,
                    paymentMethod = "CREDIT_CARD",
                    category = "FIRST_CLASS_SEDAN",
                    dateEpochMillis = now - 86400000,
                    status = "PAID"
                ),
                FinancialTransactionEntity(
                    tripId = 3,
                    invoiceNumber = "INV-2026-0661",
                    clientName = "Santander Global Corporate Banking",
                    accountType = "CORPORATE",
                    amount = 1048.0,
                    paymentMethod = "CORPORATE",
                    category = "EXECUTIVE_VAN",
                    dateEpochMillis = now - 172800000,
                    status = "PAID"
                ),
                FinancialTransactionEntity(
                    tripId = 0,
                    invoiceNumber = "INV-2026-0590",
                    clientName = "McKinsey & Company Brazil",
                    accountType = "CORPORATE",
                    amount = 4850.0,
                    paymentMethod = "CORPORATE",
                    category = "FLEET_CONTRACT",
                    dateEpochMillis = now - 259200000,
                    status = "PAID"
                ),
                FinancialTransactionEntity(
                    tripId = 0,
                    invoiceNumber = "INV-2026-0412",
                    clientName = "Goldman Sachs Asset Management",
                    accountType = "CORPORATE",
                    amount = 7600.0,
                    paymentMethod = "CORPORATE",
                    category = "ARMORED_VIP",
                    dateEpochMillis = now - 432000000,
                    status = "PAID"
                )
            )
            financialDao.insertTransactions(sampleTransactions)

            val sampleNotifications = listOf(
                NotificationEntity(
                    title = "Chauffeur a Caminho",
                    message = "Seu motorista Carlos Eduardo (BMW 760i Blindado) está a 8 minutos do Hotel Fasano.",
                    type = "TRIP_STATUS",
                    timestamp = now - 600000,
                    isRead = false,
                    relatedTripId = 1
                ),
                NotificationEntity(
                    title = "Radar de Voo Conectado",
                    message = "Voo AF457 monitorado com sucesso. Horário de decolagem confirmado para 22:45.",
                    type = "FLIGHT_UPDATE",
                    timestamp = now - 1800000,
                    isRead = false,
                    relatedTripId = 1
                ),
                NotificationEntity(
                    title = "Fatura Corporativa Emitida",
                    message = "Fatura INV-2026-0772 foi processada e enviada para o departamento financeiro.",
                    type = "PAYMENT",
                    timestamp = now - 86400000,
                    isRead = true,
                    relatedTripId = 2
                )
            )
            notificationDao.insertNotifications(sampleNotifications)

            val sampleSupport = listOf(
                SupportMessageEntity(
                    sender = "SYSTEM",
                    text = "Bem-vindo ao canal exclusivo do Concierge LuxeDrive VIP 24/7. Como podemos servi-lo hoje?",
                    timestamp = now - 7200000,
                    isAutomated = true
                ),
                SupportMessageEntity(
                    sender = "USER",
                    text = "Olá! Gostaria de confirmar se o veículo blindado já tem as duas garrafas de San Pellegrino refrigeradas.",
                    timestamp = now - 3600000,
                    isAutomated = false
                ),
                SupportMessageEntity(
                    sender = "CONCIERGE",
                    text = "Prezado Dr. Christian, confirmamos que o BMW 760i (VIP-9988) do Chauffeur Carlos Eduardo já está abastecido com San Pellegrino e a cabine pré-climatizada a 21°C. Desejamos uma excelente viagem!",
                    timestamp = now - 3300000,
                    isAutomated = false
                )
            )
            supportDao.insertMessages(sampleSupport)
        }
    }
}
