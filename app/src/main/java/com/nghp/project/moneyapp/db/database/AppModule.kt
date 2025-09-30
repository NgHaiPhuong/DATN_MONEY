package com.nghp.project.moneyapp.db.database

import android.content.Context
import androidx.room.Room
import com.nghp.project.moneyapp.db.dao.BudgetDao
import com.nghp.project.moneyapp.db.dao.MessageDao
import com.nghp.project.moneyapp.db.dao.TransactionDao
import com.nghp.project.moneyapp.db.local.LocalRepository
import com.nghp.project.moneyapp.db.local.LocalRepositoryImpl
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
    fun provideMessageDao(db: TransactionDatabase): MessageDao = db.messageDao()

    @Provides
    fun provideLocalRepository(
        transactionDao: TransactionDao,
        budgetDao: BudgetDao,
        messageDao: MessageDao
    ): LocalRepository {
        return LocalRepositoryImpl(transactionDao, budgetDao, messageDao)
    }
}