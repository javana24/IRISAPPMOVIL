package com.example.irisappmovil.domain

import kotlin.math.floor
import kotlin.math.pow

object LevelSystem {
    private const val MAX_LEVEL = 500

    fun xpForLevel(level: Int): Int {
        val safeLevel = level.coerceIn(1, MAX_LEVEL)
        return (120.0 * safeLevel.toDouble().pow(1.35)).toInt()
    }

    fun levelForXp(totalXp: Int): Int {
        if (totalXp <= 0) return 1
        var level = 1
        while (level < MAX_LEVEL && xpForLevel(level + 1) <= totalXp) {
            level++
        }
        return level
    }

    fun rankForLevel(level: Int): String = when (level) {
        in 1..9 -> "Rank S-01"
        in 10..24 -> "Cadete Táctica"
        in 25..49 -> "Operative I"
        in 50..99 -> "Operative II"
        in 100..159 -> "Cyber-Infiltrator"
        in 160..239 -> "Neo-Kyoto"
        in 240..349 -> "Level Omega"
        else -> "IRIS Vanguard"
    }

    fun progressToNextLevel(totalXp: Int): Float {
        val level = levelForXp(totalXp)
        val currentLevelXp = xpForLevel(level)
        val nextLevelXp = xpForLevel((level + 1).coerceAtMost(MAX_LEVEL))
        if (nextLevelXp <= currentLevelXp) return 1f
        return ((totalXp - currentLevelXp).toFloat() / (nextLevelXp - currentLevelXp).toFloat()).coerceIn(0f, 1f)
    }
}
