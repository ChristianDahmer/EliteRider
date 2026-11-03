package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.*
import com.example.data.local.entities.*

@Database(
    entities = [
        TripBookingEntity::class,
        FleetVehicleEntity::class,
        ChauffeurEntity::class,
        FinancialTransactionEntity::class,
        NotificationEntity::class,
        SupportMessageEntity::class,
        FleetInventoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun fleetDao(): FleetDao
    abstract fun financialDao(): FinancialDao
    abstract fun notificationDao(): NotificationDao
    abstract fun supportDao(): SupportDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "luxedrive_vip_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
