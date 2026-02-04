package com.example.fouroperations.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.example.fouroperations.model.Operation
import com.example.fouroperations.ui.game.GameScreen
import com.example.fouroperations.ui.game.GameViewModel
import com.example.fouroperations.ui.result.ResultScreen
import com.example.fouroperations.ui.theme.FourOperationsTheme
import com.example.fouroperations.util.SoundManager

private enum class Route { MENU, GAME, RESULT }

class MainActivity : ComponentActivity() {

    private val vm: GameViewModel by viewModels()
    private lateinit var sounds: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sounds = SoundManager(this)

        setContent {
            FourOperationsTheme {
                AppRoot(vm, sounds)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sounds.release()
    }
}

@Composable
private fun AppRoot(
    vm: GameViewModel,
    sounds: SoundManager
) {
    var route by remember { mutableStateOf(Route.MENU) }
    val ui by vm.ui.collectAsState()

    LaunchedEffect(Unit) {
        vm.reset()
    }

    LaunchedEffect(ui.finished) {
        if (ui.finished) route = Route.RESULT
    }

    AnimatedContent(
        targetState = route,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "route"
    ) { r ->
        when (r) {
            Route.MENU -> MenuScreen(
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
                onPlayAgain = {
                    vm.reset()
                    route = Route.MENU
                }
            )
        }
    }
}
