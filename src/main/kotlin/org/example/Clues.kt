package org.example

import kotlinx.serialization.Serializable

@Serializable
data class Clues(
    val columns : List<List<Int>>,
    val rows : List<List<Int>>
)