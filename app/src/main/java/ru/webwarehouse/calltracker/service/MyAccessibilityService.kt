package ru.webwarehouse.calltracker.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import timber.log.Timber

class MyAccessibilityService : AccessibilityService() {

    init {
        Timber.i("init")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

    }

    override fun onInterrupt() {

    }

}