package com.example.fouroperations.ui.game

import androidx.lifecycle.ViewModel
import com.example.fouroperations.model.Operation
import com.example.fouroperations.util.Problem
import com.example.fouroperations.util.RandomGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class GameUiState(
    val operation: Operation? = null,
    val stars: Int = 0,
    val errors: Int = 0,
    val questionCount: Int = 0,
    val maxQuestions: Int = 10,
    val current: Problem? = null,
    val options: List<Int> = emptyList(),
    val lastWasCorrect: Boolean? = null,
    val finished: Boolean = false
) {
    val isGameReady: Boolean
        get() = operation != null && current != null && options.isNotEmpty()
}

class GameViewModel : ViewModel() {

    private val _ui = MutableStateFlow(GameUiState())
    val ui: StateFlow<GameUiState> = _ui

    fun start(op: Operation) {
        _ui.value = GameUiState(operation = op)
        nextQuestion()
    }

    fun nextQuestion() {
        val op = _ui.value.operation ?: return

        if (_ui.value.questionCount >= _ui.value.maxQuestions) {
            _ui.update { it.copy(finished = true, lastWasCorrect = null) }
            return
        }

        val problem = RandomGenerator.generate(op)
        val options = RandomGenerator.generateOptions(problem.answer)

        _ui.update {
            it.copy(
                current = problem,
                options = options,
                questionCount = it.questionCount + 1,
                lastWasCorrect = null
            )
        }
    }

    fun answer(chosen: Int) {
        val p = _ui.value.current ?: return
        val correct = chosen == p.answer

        _ui.update {
            it.copy(
                stars = it.stars + if (correct) 1 else 0,
                errors = it.errors + if (correct) 0 else 1,
                lastWasCorrect = correct
            )
        }
    }

    fun reset() {
        _ui.value = GameUiState()
    }
}
