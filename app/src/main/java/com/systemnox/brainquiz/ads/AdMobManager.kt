package com.systemnox.brainquiz.ads

import android.app.Application
import com.google.android.gms.ads.MobileAds

object AdMobManager {
    fun initialize(application: Application) {
        MobileAds.initialize(application)
    }
}