package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trip_bookings ORDER BY scheduledEpochMillis DESC")
    fun getAllTrips(): Flow<List<TripBookingEntity>>

    @Query("SELECT * FROM trip_bookings WHERE tripStatus != 'COMPLETED' AND tripStatus != 'CANCELLED' ORDER BY scheduledEpochMillis ASC LIMIT 1")
    fun getActiveTrip(): Flow<TripBookingEntity?>

    @Query("SELECT * FROM trip_bookings WHERE id = :id")
    suspend fun getTripById(id: Long): TripBookingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripBookingEntity): Long

    @Update
    suspend fun updateTrip(trip: TripBookingEntity)

    @Query("UPDATE trip_bookings SET tripStatus = :status WHERE id = :id")
    suspend fun updateTripStatus(id: Long, status: String)

    @Query("UPDATE trip_bookings SET ratingGiven = :rating, reviewFeedback = :feedback WHERE id = :id")
    suspend fun updateTripRating(id: Long, rating: Int, feedback: String)
}

@Dao
interface FleetDao {
    @Query("SELECT * FROM fleet_vehicles ORDER BY category, modelName")
    fun getAllVehicles(): Flow<List<FleetVehicleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicles(vehicles: List<FleetVehicleEntity>)

    @Update
    suspend fun updateVehicle(vehicle: FleetVehicleEntity)

    @Query("UPDATE fleet_vehicles SET status = :status WHERE id = :id")
    suspend fun updateVehicleStatus(id: Long, status: String)

    @Query("SELECT * FROM chauffeurs ORDER BY rating DESC")
    fun getAllChauffeurs(): Flow<List<ChauffeurEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChauffeurs(chauffeurs: List<ChauffeurEntity>)

    @Query("SELECT * FROM fleet_inventory ORDER BY itemName")
    fun getAllInventory(): Flow<List<FleetInventoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventory(items: List<FleetInventoryEntity>)

    @Query("UPDATE fleet_inventory SET quantityInStock = :qty WHERE id = :id")
    suspend fun updateInventoryQty(id: Long, qty: Int)
}

@Dao
interface FinancialDao {
    @Query("SELECT * FROM financial_transactions ORDER BY dateEpochMillis DESC")
    fun getAllTransactions(): Flow<List<FinancialTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: FinancialTransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(txs: List<FinancialTransactionEntity>)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM notifications")
    suspend fun clearAll()
}

@Dao
interface SupportDao {
    @Query("SELECT * FROM support_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<SupportMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: SupportMessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<SupportMessageEntity>)
}
