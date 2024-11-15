package com.example.ejercicio_1.data

data class PostModel(
  val id: Int,
  var title: String,
  var body: String,
  var tags: List<String>,  // Añadido para los tags
  var reactions: Reactions,  // Añadido para las reacciones
  var views: Int,
  var userId: Int
)

data class Reactions(
  val likes: Int,
  val dislikes: Int
)
