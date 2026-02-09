# fix_references.ps1
Write-Host "=== CORRIGINDO REFERÊNCIAS ===" -ForegroundColor Cyan

# 1. Corrigir AdBanner.kt
$adBannerPath = "app\src\main\java\com\example\fouroperations\ui\ads\AdBanner.kt"
if (Test-Path $adBannerPath) {
    $content = Get-Content $adBannerPath -Raw
    # Remover referências a BuildConfig
    $content = $content -replace "BuildConfig\.DEBUG", "true"
    $content = $content -replace "import.*BuildConfig", ""
    $content = $content -replace "if \(!BuildConfig\.DEBUG &&", "if (!debug &&"
    
    $newContent = @'
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
'@
    
    $newContent | Out-File -FilePath $adBannerPath -Encoding UTF8
    Write-Host "  ✅ AdBanner.kt corrigido" -ForegroundColor Green
}

# 2. Verificar se FredokaFamily está disponível
$componentsPath = "app\src\main\java\com\example\fouroperations\ui\components"
if (-not (Test-Path $componentsPath)) {
    New-Item -ItemType Directory -Path $componentsPath -Force | Out-Null
}

# 3. Criar arquivo Fonts.kt se necessário
$fontsPath = "$componentsPath\Fonts.kt"
if (-not (Test-Path $fontsPath)) {
    $fontsContent = @'
package com.example.fouroperations.ui.components

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.fouroperations.R

val FredokaFamily = FontFamily(
    Font(R.font.fredoka_bold_font, weight = FontWeight.Bold)
)
'@
    $fontsContent | Out-File -FilePath $fontsPath -Encoding UTF8
    Write-Host "  ✅ Fonts.kt criado" -ForegroundColor Green
}

# 4. Corrigir imports nos arquivos problemáticos
$filesToFix = @(
    @{
        Path = "app\src\main\java\com\example\fouroperations\ui\main\MenuScreen.kt"
        Search = "import com\.example\.fouroperations\.ui\.components\.ThreeDButton"
        Replace = "import com.example.fouroperations.ui.components.FredokaFamily`nimport com.example.fouroperations.ui.components.ThreeDButton"
    }
)

foreach ($file in $filesToFix) {
    if (Test-Path $file.Path) {
        $content = Get-Content $file.Path -Raw
        if ($content -match $file.Search) {
            $content = $content -replace $file.Search, $file.Replace
            $content | Out-File -FilePath $file.Path -Encoding UTF8
            Write-Host "  ✅ $($file.Path) corrigido" -ForegroundColor Green
        }
    }
}

Write-Host "`n=== TENTANDO COMPILAR NOVAMENTE ===" -ForegroundColor Yellow
& .\gradlew.bat clean assembleDebug --stacktrace