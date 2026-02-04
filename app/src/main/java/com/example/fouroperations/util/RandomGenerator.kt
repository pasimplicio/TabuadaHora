package com.example.fouroperations.util

import com.example.fouroperations.model.Operation
import kotlin.random.Random

data class Problem(
    val a: Int,
    val b: Int,
    val op: Operation,
    val answer: Int
) {
    fun text(): String = "$a ${op.symbol} $b = ?"
}

object RandomGenerator {

    fun generate(op: Operation): Problem {
        return when (op) {
            Operation.ADD -> genAdd()
            Operation.SUB -> genSub()
            Operation.MUL -> genMul()
            Operation.DIV -> genDiv()
        }
    }

    private fun genAdd(): Problem {
        val a = Random.nextInt(0, 11) // 0..10
        val b = Random.nextInt(0, 11)
        return Problem(a, b, Operation.ADD, a + b)
    }

    private fun genSub(): Problem {
        // Garante resultado >= 0 e simples
        val a = Random.nextInt(0, 11)
        val b = Random.nextInt(0, 11)
        val max = maxOf(a, b)
        val min = minOf(a, b)
        return Problem(max, min, Operation.SUB, max - min)
    }

    private fun genMul(): Problem {
        val a = Random.nextInt(0, 6) // 0..5
        val b = Random.nextInt(0, 6)
        return Problem(a, b, Operation.MUL, a * b)
    }

    private fun genDiv(): Problem {
        // Divisão exata (sem resto), bem simples
        val b = Random.nextInt(1, 6) // 1..5
        val answer = Random.nextInt(0, 6) // 0..5
        val a = b * answer
        return Problem(a, b, Operation.DIV, answer)
    }

    fun generateOptions(correct: Int): List<Int> {
        val set = linkedSetOf(correct)
        // gera opções "perto" do correto, sem negativar
        while (set.size < 4) {
            val delta = Random.nextInt(-2, 3) // -2..2
            val candidate = (correct + delta).coerceAtLeast(0)
            set.add(candidate)
        }
        return set.shuffled()
    }
}
