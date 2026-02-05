package com.example.fouroperations.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.fouroperations.model.Operation
import com.example.fouroperations.ui.game.GameScreen
import com.example.fouroperations.ui.game.GameViewModel
import com.example.fouroperations.ui.result.ResultScreen
import com.example.fouroperations.ui.theme.FourOperationsTheme
import com.example.fouroperations.util.SoundManager
import com.example.fouroperations.util.UserPrefs
import com.example.fouroperations.util.UserProfile

private enum class Route { USER_GATE, MENU, GAME, RESULT }

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
    val context = LocalContext.current
    var route by remember { mutableStateOf(Route.USER_GATE) }
    val ui by vm.ui.collectAsState()
    var users by remember { mutableStateOf(listOf<UserProfile>()) }
    var activeUserId by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        vm.reset()
        users = UserPrefs.getUsers(context)
        activeUserId = UserPrefs.getActiveUserId(context)
    }

    LaunchedEffect(ui.finished) {
        if (ui.finished) {
            val op = ui.operation
            if (op != null && activeUserId.isNotBlank()) {
                users = UserPrefs.updateBestScore(
                    context = context,
                    userId = activeUserId,
                    operationId = op.name,
                    score = ui.stars
                )
            }
            route = Route.RESULT
        }
    }

    AnimatedContent(
        targetState = route,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "route"
    ) { r ->
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
                onSelectUser = { userId ->
                    activeUserId = userId
                    UserPrefs.setActiveUserId(context, userId)
                    route = Route.MENU
                }
            )
            Route.MENU -> MenuScreen(
                onPick = {
                    vm.start(it)
                    route = Route.GAME
                },
                onExit = {
                    route = Route.USER_GATE
                }
            )
            Route.GAME -> GameScreen(
                ui = ui,
                onAnswer = vm::answer,
                onCorrect = sounds::playCorrect,
                onWrong = sounds::playWrong,
                onNext = vm::nextQuestion,
                onQuit = {
                    route = Route.USER_GATE
                    vm.reset()
                }
            )
            Route.RESULT -> ResultScreen(
                stars = ui.stars,
                max = ui.maxQuestions,
                onPlayAgain = {
                    vm.reset()
                    route = Route.USER_GATE
                }
            )
        }
    }
}
