package com.nmh.base.project.db.database

import android.content.Context
import androidx.room.Room
import com.nmh.base.project.db.dao.BudgetDao
import com.nmh.base.project.db.dao.TransactionDao
import com.nmh.base.project.db.local.LocalRepository
import com.nmh.base.project.db.local.LocalRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TransactionDatabase {
        return Room.databaseBuilder(
            context,
            TransactionDatabase::class.java,
            "transaction_database"
        ).build()
    }

    @Provides
    fun provideTransactionDao(db: TransactionDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideBudgetDao(db: TransactionDatabase): BudgetDao = db.budgetDao()

    @Provides
    fun provideLocalRepository(
        transactionDao: TransactionDao,
        budgetDao: BudgetDao
    ): LocalRepository {
        return LocalRepositoryImpl(transactionDao, budgetDao)
    }
}