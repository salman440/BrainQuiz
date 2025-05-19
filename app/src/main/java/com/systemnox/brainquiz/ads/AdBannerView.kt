package com.systemnox.brainquiz.ads

import android.content.Context
import android.util.Log
import android.view.ViewGroup.LayoutParams
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.AdListener
import com.systemnox.brainquiz.utils.Constants
import com.google.android.gms.ads.AdView as GoogleAdView

//@Composable
//fun AdBannerView(context: Context, modifier: Modifier = Modifier) {
//    AndroidView(
//        modifier = modifier.fillMaxWidth(),
//        factory = {
//            GoogleAdView(context).apply {
//                setAdSize(AdSize.BANNER)
//                this.adUnitId = Constants.BANNER_AD_UNIT_ID
//                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
//                loadAd(AdRequest.Builder().build())
//
//                adListener = object : AdListener() {
//                    override fun onAdFailedToLoad(adError: LoadAdError) {
//                        // Handle failure if needed
//                        Log.e("loadAdError", "onAdFailedToLoad: ${adError.message}" )
//                        Toast.makeText(context, "Ad failed to load: ${adError.message}", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        }
//    )
//}


@Composable
fun AdBannerView(context: Context, modifier: Modifier = Modifier) {
    // Remember if ad has already been loaded
    var adLoaded by remember { mutableStateOf(false) }

    // Remember a single AdView instance
    val adView = remember {
        GoogleAdView(context).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = Constants.BANNER_AD_UNIT_ID
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
    }

    // Load the ad only once
    LaunchedEffect(Unit) {
        if (!adLoaded) {
//            Toast.makeText(context, "Trying to load ad...", Toast.LENGTH_SHORT).show()
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
            adView.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    adLoaded = true
                    Log.d("AdBannerView", "Ad loaded successfully")
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e("AdBannerView", "Ad failed to load: ${adError.message}")
//                    Toast.makeText(context, "Ad failed to load: ${adError.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { adView }
    )
}