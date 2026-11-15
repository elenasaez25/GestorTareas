package com.example.gestortareas.model

data class TareaApi(
    val userId: Int,
    val id: Int,
    val title: String,
    val completed: Boolean
)
