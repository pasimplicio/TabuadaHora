package com.example.fouroperations.ui.ads

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

object AdsConfig {
    // TODO: Troque pelo seu Ad Unit ID (banner) antes de publicar.
    private const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val PROD_BANNER_AD_UNIT_ID = "REPLACE_ME"

    fun bannerAdUnitId(debug: Boolean = true): String {
        // Segurança: nunca publicar com TEST id.
        // Você deve substituir PROD_BANNER_AD_UNIT_ID em release.
        return if (debug) TEST_BANNER_AD_UNIT_ID else PROD_BANNER_AD_UNIT_ID
    }
}

@Composable
fun AdBanner(
    modifier: Modifier = Modifier,
    debug: Boolean = true,
    adUnitId: String = AdsConfig.bannerAdUnitId(debug)
) {
    // Segurança: evita publicar com placeholder e ficar tentando carregar anúncios inválidos.
    if (!debug && adUnitId == AdsConfig.PROD_BANNER_AD_UNIT_ID) return

    val context = LocalContext.current

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