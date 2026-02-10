package com.example.fouroperations.ui.ads

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.fouroperations.BuildConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

object AdsConfig {
    private const val PLACEHOLDER_AD_UNIT_ID = "REPLACE_ME"
    private const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

    fun bannerAdUnitId(): String {
        return if (BuildConfig.DEBUG) TEST_BANNER_AD_UNIT_ID else BuildConfig.AD_BANNER_UNIT_ID
    }

    fun isValidAdUnitId(adUnitId: String): Boolean {
        return adUnitId.isNotBlank() && adUnitId != PLACEHOLDER_AD_UNIT_ID
    }
}

@Composable
fun AdBanner(
    modifier: Modifier = Modifier
) {
    val adUnitId = AdsConfig.bannerAdUnitId()
    if (!AdsConfig.isValidAdUnitId(adUnitId)) return

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            AdView(ctx).apply {
                setAdSize(AdSize.BANNER)
                setAdUnitId(adUnitId)
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                loadAd(AdRequest.Builder().build())
            }
        },
        update = { _ ->
            // Nada por enquanto
        }
    )
}
