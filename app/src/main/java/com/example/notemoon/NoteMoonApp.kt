package com.example.notemoon

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.notemoon.alarm.receiver.AlarmNotifier
import com.example.notemoon.tasks.reminder.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application class. [HiltAndroidApp] bootstraps Hilt; implementing
 * [Configuration.Provider] lets WorkManager build reminder workers through
 * Hilt's [HiltWorkerFactory] so they can be constructor-injected.
 */
@HiltAndroidApp
class NoteMoonApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var alarmNotifier: AlarmNotifier

    override fun onCreate() {
        super.onCreate()
        notificationHelper.ensureChannel()
        alarmNotifier.ensureChannel()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
