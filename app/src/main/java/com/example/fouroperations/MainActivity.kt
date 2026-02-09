package com.example.fouroperations.ui.main

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.fouroperations.R
import com.example.fouroperations.ui.game.GameScreen
import com.example.fouroperations.ui.game.GameViewModel
import com.example.fouroperations.ui.result.ResultScreen
import com.example.fouroperations.ui.theme.FourOperationsTheme
import com.example.fouroperations.util.SoundManager
import com.example.fouroperations.util.UserPrefs
import com.example.fouroperations.util.UserProfile
import com.example.fouroperations.util.BillingManager
import com.example.fouroperations.ui.ads.AdBanner
import com.google.android.gms.ads.MobileAds

private enum class Route { USER_GATE, MENU, GAME, RESULT }

class MainActivity : ComponentActivity() {

    private val vm: GameViewModel by viewModels()
    private lateinit var sounds: SoundManager
    private var bgMusic: MediaPlayer? = null
    private var isMusicMuted = false
    private var isStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MobileAds.initialize(this) {}

        sounds = SoundManager(this)
        isMusicMuted = UserPrefs.isMusicMuted(this)

        bgMusic = MediaPlayer().apply {
            val attrs = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            setAudioAttributes(attrs)
            val afd = resources.openRawResourceFd(R.raw.bg_music)
            if (afd != null) {
                afd.use { descriptor ->
                    setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
                }
                isLooping = true
                setVolume(if (isMusicMuted) 0f else 0.8f, if (isMusicMuted) 0f else 0.8f)
                prepare()
            } else {
                Log.w("MainActivity", "bg_music resource not found; background music disabled.")
            }
        }

        setContent {
            FourOperationsTheme {
                AppRoot(
                    activity = this,
                    vm = vm,
                    sounds = sounds,
                    initialMusicMuted = isMusicMuted,
                    onMusicMutedChange = { muted ->
                        isMusicMuted = muted
                        if (muted) {
                            bgMusic?.pause()
                            bgMusic?.setVolume(0f, 0f)
                        } else {
                            bgMusic?.setVolume(0.2f, 0.2f)
                            if (isStarted) bgMusic?.start()
                        }
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        isStarted = true
        if (!isMusicMuted) bgMusic?.start()
    }

    override fun onStop() {
        isStarted = false
        bgMusic?.pause()
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        sounds.release()
        bgMusic?.release()
        bgMusic = null
    }
}

@Composable
private fun AppRoot(
    activity: ComponentActivity,
    vm: GameViewModel,
    sounds: SoundManager,
    initialMusicMuted: Boolean,
    onMusicMutedChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var route by remember { mutableStateOf(Route.USER_GATE) }
    val ui by vm.ui.collectAsState()

    var users by remember { mutableStateOf(listOf<UserProfile>()) }
    var activeUserId by remember { mutableStateOf("") }
    var isMusicMuted by remember { mutableStateOf(initialMusicMuted) }
    var adsRemoved by remember { mutableStateOf(false) }

    val billing = remember {
        BillingManager(
            appContext = context.applicationContext,
            onAdsRemoved = { adsRemoved = true }
        )
    }

    DisposableEffect(Unit) {
        billing.start()
        onDispose { billing.stop() }
    }

    LaunchedEffect(Unit) {
        vm.reset()
        users = UserPrefs.getUsers(context)
        activeUserId = UserPrefs.getActiveUserId(context)
        adsRemoved = UserPrefs.isAdsRemoved(context)
    }

    LaunchedEffect(ui.finished) {
        if (ui.finished) route = Route.RESULT
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            Crossfade(targetState = route, label = "route") { r ->
                when (r) {
                    Route.USER_GATE -> UserGateScreen(
                        users = users,
                        activeUserId = activeUserId,
                        maxScore = ui.maxQuestions,
                        onAddUser = { name ->
                            val newUser = UserPrefs.addUser(context, name)
                            users = UserPrefs.getUsers(context)
                            activeUserId = newUser.id
                        },
                        onSelectUser = {
                            activeUserId = it
                            UserPrefs.setActiveUserId(context, it)
                            route = Route.MENU
                        }
                    )

                    Route.MENU -> MenuScreen(
                        adsRemoved = adsRemoved,
                        onRemoveAds = { billing.launchRemoveAdsPurchase(activity) },
                        isMusicMuted = isMusicMuted,
                        onToggleMusicMuted = {
                            val newMuted = !isMusicMuted
                            isMusicMuted = newMuted
                            UserPrefs.setMusicMuted(context, newMuted)
                            onMusicMutedChange(newMuted)
                        },
                        onExit = {
                            route = Route.USER_GATE
                            vm.reset()
                        },
                        onPick = {
                            vm.start(it)
                            route = Route.GAME
                        }
                    )

                    Route.GAME -> GameScreen(
                        ui = ui,
                        onAnswer = vm::answer,
                        onCorrect = sounds::playCorrect,
                        onWrong = sounds::playWrong,
                        onNext = vm::nextQuestion,
                        onQuit = {
                            route = Route.MENU
                            vm.reset()
                        }
                    )

                    Route.RESULT -> ResultScreen(
                        stars = ui.stars,
                        max = ui.maxQuestions,
                        isMusicMuted = isMusicMuted,
                        onToggleMusicMuted = {
                            val newMuted = !isMusicMuted
                            isMusicMuted = newMuted
                            UserPrefs.setMusicMuted(context, newMuted)
                            onMusicMutedChange(newMuted)
                        },
                        onPlayAgain = {
                            vm.reset()
                            route = Route.USER_GATE
                        }
                    )
                }
            }
        }

        if (!adsRemoved) {
            AdBanner(modifier = Modifier.fillMaxWidth())
        }
    }
}
