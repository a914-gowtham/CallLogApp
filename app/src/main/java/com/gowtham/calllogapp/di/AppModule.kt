package com.gowtham.calllogapp.di

import android.content.ContentValues
import android.content.Context
import androidx.room.OnConflictStrategy
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gowtham.calllogapp.db.ContactDatabase
import com.gowtham.calllogapp.db.data.Contact
import com.gowtham.calllogapp.utils.Constants
import com.gowtham.calllogapp.utils.Constants.DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideChatUserDb(@ApplicationContext context: Context): ContactDatabase{
       return Room.databaseBuilder(context,ContactDatabase::class.java,
           DB_NAME)
           .fallbackToDestructiveMigration()
           .addCallback(roomCallBack).build()
    }

    object roomCallBack : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            try {
                for (contact in Constants.LIST_OF_CONTACTS){
                    val contentValue=ContentValues()
                    contentValue.put("mobile",contact.mobile)
                    contentValue.put("name",contact.name)
                    contentValue.put("image",contact.image)
                    contentValue.put("isFavorite",contact.isFavorite)
                    contentValue.put("isSelected",contact.isFavorite)
                    contentValue.put("isSelectionMode",contact.isSelectionMode)
                    contentValue.put("isExternalContact",contact.isExternalContact)
                    db.insert("Contact",OnConflictStrategy.REPLACE,contentValue)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    @Singleton
    @Provides
    fun provideCallLogDao(db: ContactDatabase) = db.getCallDao()

    @Singleton
    @Provides
    fun provideContactDao(db: ContactDatabase) = db.getContactDao()
}