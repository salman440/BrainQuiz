package com.systemnox.brainquiz.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.systemnox.brainquiz.utils.Constants

object InterstitialAdManager {
    private var interstitialAd: InterstitialAd? = null

    fun loadAd(context: Context, adUnitId: String) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
                Log.d("InterstitialAd", "Ad Loaded")
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                interstitialAd = null
                Log.e("InterstitialAd", "Failed to load: ${adError.message}")
            }
        })
    }

    fun showAd(activity: Activity, onAdDismissed: () -> Unit) {
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                onAdDismissed()
                loadAd(activity, Constants.INTERSTITIAL_AD_UNIT_ID)
            }

            override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                Log.e("InterstitialAd", "Failed to show: ${adError.message}")
                onAdDismissed()
            }
        }

        if (interstitialAd != null) {
            interstitialAd?.show(activity)
        } else {
            onAdDismissed()
        }
    }
}