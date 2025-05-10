package com.systemnox.brainquiz.ads

import android.content.Context
import android.view.ViewGroup.LayoutParams
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.AdListener
import com.systemnox.brainquiz.utils.Constants
import com.google.android.gms.ads.AdView as GoogleAdView

@Composable
fun AdBannerView(context: Context) {
    AndroidView(
        factory = {
            GoogleAdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = Constants.BANNER_AD_UNIT_ID
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                loadAd(AdRequest.Builder().build())

                adListener = object : AdListener() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        // Handle failure if needed
                    }
                }
            }
        }
    )
}