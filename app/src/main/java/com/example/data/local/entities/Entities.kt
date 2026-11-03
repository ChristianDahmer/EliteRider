package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trip_bookings")
data class TripBookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookingCode: String,
    val passengerName: String,
    val passengerPhone: String,
    val pickupAddress: String,
    val dropoffAddress: String,
    val scheduledEpochMillis: Long,
    val vehicleCategory: String,
    val vehicleModel: String,
    val vehiclePlate: String,
    val isArmored: Boolean,
    val flightNumber: String?,
    val flightStatus: String?,
    val amenitiesList: String,
    val basePrice: Double,
    val distanceKm: Double,
    val totalPrice: Double,
    val paymentMethod: String,
    val paymentStatus: String,
    val tripStatus: String,
    val chauffeurName: String,
    val chauffeurPhone: String,
    val chauffeurRating: Double,
    val ratingGiven: Int? = null,
    val reviewFeedback: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

@Entity(tableName = "fleet_vehicles")
data class FleetVehicleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plate: String,
    val modelName: String,
    val category: String,
    val year: Int,
    val color: String,
    val isArmored: Boolean,
    val ballisticLevel: String?,
    val status: String,
    val currentMileage: Int,
    val nextMaintenanceKm: Int,
    val assignedChauffeur: String,
    val fuelOrBatteryPercent: Int,
    val currentLocationName: String
)

@Entity(tableName = "chauffeurs")
data class ChauffeurEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val badgeNumber: String,
    val experienceYears: Int,
    val rating: Double,
    val tripsCompleted: Int,
    val languages: String,
    val securityCertification: String,
    val status: String
)

@Entity(tableName = "financial_transactions")
data class FinancialTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Long,
    val invoiceNumber: String,
    val clientName: String,
    val accountType: String,
    val amount: Double,
    val paymentMethod: String,
    val category: String,
    val dateEpochMillis: Long,
    val status: String
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val type: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val relatedTripId: Long? = null
)

@Entity(tableName = "support_messages")
data class SupportMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isAutomated: Boolean = false
)

@Entity(tableName = "fleet_inventory")
data class FleetInventoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemName: String,
    val category: String,
    val quantityInStock: Int,
    val minThreshold: Int,
    val unit: String
)
