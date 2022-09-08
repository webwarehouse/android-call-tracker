package ru.webwarehouse.calltracker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.webwarehouse.calltracker.repository.CallsRepository

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideSharedPrefs(@ApplicationContext context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    fun provideRepository(@ApplicationContext context: Context): CallsRepository =
        CallsRepository(provideSharedPrefs(context))

    /*@Provides
    fun provideUserInfo(@ApplicationContext context: Context): UserInfo = UserInfo(context)*/

}