package com.example.ejercicio_1.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ejercicio_1.data.PostApiService
import com.example.ejercicio_1.data.PostModel
import com.example.ejercicio_1.data.Reactions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Composable para mostrar el listado de posts
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ContenidoPostsListado(
  navController: NavHostController,
  servicio: PostApiService
) {
  var posts by remember { mutableStateOf(emptyList<PostModel>()) }
  var isLoading by remember { mutableStateOf(false) }
  var hasMorePosts by remember { mutableStateOf(true) }
  var skip by remember { mutableStateOf(0) }

  if (!isLoading && hasMorePosts) {
    isLoading = true
    CoroutineScope(Dispatchers.IO).launch {
      try {
        val response = servicio.getPosts(skip = skip)
        if (response.isSuccessful) {
          val postsList = response.body()?.posts ?: emptyList()
          posts += postsList
          skip += postsList.size
          hasMorePosts = postsList.isNotEmpty()
        } else {
          Log.e("ContenidoPostsListado", "Error fetching posts: ${response.message()}")
        }
      } catch (e: Exception) {
        Log.e("ContenidoPostsListado", "Error fetching posts: $e")
      } finally {
        isLoading = false
      }
    }
  }

  LazyColumn(
    modifier = Modifier.padding(16.dp)
  ) {
    item {
      // Header Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("Id", modifier = Modifier.weight(1f))
        Text("Title", modifier = Modifier.weight(3f))
        Text("Acciones", modifier = Modifier.weight(1f), textAlign = TextAlign.End)
      }
      Spacer(modifier = Modifier.height(8.dp))
    }

    items(posts) { post ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp)
          .clickable {
            navController.navigate("postVer/${post.id}")
          },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("ID: ${post.id}", modifier = Modifier.weight(1f))
        Text(post.title, modifier = Modifier.weight(3f))

        Row(
          modifier = Modifier.weight(1f),
          horizontalArrangement = Arrangement.End
        ) {
          IconButton(onClick = { navController.navigate("postVer/${post.id}") }) {
            Icon(imageVector = Icons.Filled.Edit, contentDescription = "Editar")
          }
          IconButton(onClick = { navController.navigate("postDel/${post.id}") }) {
            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Eliminar")
          }
        }
      }
    }

    if (isLoading) {
      item {
        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
      }
    }
  }
}

// Composable para crear un nuevo post
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ContenidoPostNuevo(
  navController: NavHostController,
  servicio: PostApiService
) {
  var title by remember { mutableStateOf("") }
  var body by remember { mutableStateOf("") }
  var views by remember { mutableStateOf(0) }
  var userId by remember { mutableStateOf(0) }
  var tags by remember { mutableStateOf("") }
  var reactionsLikes by remember { mutableStateOf(0) }
  var reactionsDislikes by remember { mutableStateOf(0) }
  var isLoading by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    TextField(
      value = title,
      onValueChange = { title = it },
      label = { Text("Title") },
      modifier = Modifier.fillMaxWidth()
    )

    TextField(
      value = body,
      onValueChange = { body = it },
      label = { Text("Body") },
      modifier = Modifier.fillMaxWidth()
    )

    TextField(
      value = views.toString(),
      onValueChange = { views = it.toIntOrNull() ?: 0 },
      label = { Text("Views") },
      modifier = Modifier.fillMaxWidth()
    )

    TextField(
      value = userId.toString(),
      onValueChange = { userId = it.toIntOrNull() ?: 0 },
      label = { Text("User ID") },
      modifier = Modifier.fillMaxWidth()
    )

    TextField(
      value = tags,
      onValueChange = { tags = it },
      label = { Text("Tags") },
      modifier = Modifier.fillMaxWidth()
    )

    TextField(
      value = reactionsLikes.toString(),
      onValueChange = { reactionsLikes = it.toIntOrNull() ?: 0 },
      label = { Text("Likes") },
      modifier = Modifier.fillMaxWidth()
    )

    TextField(
      value = reactionsDislikes.toString(),
      onValueChange = { reactionsDislikes = it.toIntOrNull() ?: 0 },
      label = { Text("Dislikes") },
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(16.dp))

    Button(
      onClick = {
        isLoading = true
        CoroutineScope(Dispatchers.IO).launch {
          try {
            // Creamos el nuevo post con todos los datos
            val newPost = PostModel(
              id = 0, // Id se genera automáticamente
              title = title,
              body = body,
              views = views,
              userId = userId,
              tags = tags.split(",").map { it.trim() }, // Las etiquetas se separan por coma
              reactions = Reactions(likes = reactionsLikes, dislikes = reactionsDislikes) // Creamos el objeto Reactions
            )
            val response = servicio.createPost(newPost)
            if (response.isSuccessful) {
              // Navegar a la pantalla de lista de posts
              navController.navigate("posts")
            } else {
              Log.e("ContenidoPostNuevo", "Error creating post: ${response.message()}")
            }
          } catch (e: Exception) {
            Log.e("ContenidoPostNuevo", "Error creating post: $e")
          } finally {
            isLoading = false
          }
        }
      },
      modifier = Modifier.fillMaxWidth(),
      enabled = !isLoading
    ) {
      if (isLoading) {
        CircularProgressIndicator()
      } else {
        Text("Create Post")
      }
    }
  }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ContenidoPostEditar(
  navController: NavHostController,
  servicio: PostApiService,
  postId: Int = 0
) {
  var post by remember { mutableStateOf(PostModel(0, "", "", listOf(), Reactions(0, 0), 0, 0)) }
  var isLoading by remember { mutableStateOf(true) }
  var isPostUpdated by remember { mutableStateOf(false) } // Variable to track if post was updated

  // Fetch the post when the postId changes
  if (postId != 0 && isLoading) {
    CoroutineScope(Dispatchers.IO).launch {
      try {
        val response = servicio.getPost(postId.toString())
        if (response.isSuccessful) {
          post = response.body() ?: PostModel(0, "", "", listOf(), Reactions(0, 0), 0, 0)
        }
      } catch (e: Exception) {
        Log.e("ContenidoPostEditar", "Error fetching post: $e")
      } finally {
        isLoading = false
      }
    }
  }

  if (isLoading) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      CircularProgressIndicator()
    }
  } else {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
      // Allow editing the title and body
      TextField(
        value = post.title,
        onValueChange = { post.title = it },
        label = { Text("Title") },
        enabled = true
      )
      Spacer(modifier = Modifier.height(8.dp))
      TextField(
        value = post.body,
        onValueChange = { post.body = it },
        label = { Text("Body") },
        enabled = true
      )
      Spacer(modifier = Modifier.height(8.dp))
      TextField(
        value = post.views.toString(),
        onValueChange = { post.views = it.toIntOrNull() ?: 0 },
        label = { Text("Views") }
      )
      Spacer(modifier = Modifier.height(8.dp))
      TextField(
        value = post.userId.toString(),
        onValueChange = { post.userId = it.toIntOrNull() ?: 0 },
        label = { Text("User ID") }
      )
      Spacer(modifier = Modifier.height(8.dp))
      TextField(
        value = post.tags.joinToString(", "),
        onValueChange = { post.tags = it.split(",").map { tag -> tag.trim() } },
        label = { Text("Tags") }
      )
      Spacer(modifier = Modifier.height(8.dp))
      TextField(
        value = post.reactions.likes.toString(),
        onValueChange = { post.reactions = post.reactions.copy(likes = it.toIntOrNull() ?: 0) },
        label = { Text("Likes") }
      )
      Spacer(modifier = Modifier.height(8.dp))
      TextField(
        value = post.reactions.dislikes.toString(),
        onValueChange = { post.reactions = post.reactions.copy(dislikes = it.toIntOrNull() ?: 0) },
        label = { Text("Dislikes") }
      )
      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = {
          isLoading = true
          CoroutineScope(Dispatchers.IO).launch {
            try {
              // Simulate update (this will not persist unless handled server-side)
              val response = servicio.updatePost(post)
              if (response.isSuccessful) {
                // Update the UI to reflect changes immediately
                isPostUpdated = true
              } else {
                Log.e("ContenidoPostEditar", "Error updating post: ${response.message()}")
              }
            } catch (e: Exception) {
              Log.e("ContenidoPostEditar", "Error updating post: $e")
            } finally {
              isLoading = false
            }
          }
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = !isLoading
      ) {
        if (isLoading) {
          CircularProgressIndicator()
        } else {
          Text("Update Post")
        }
      }
    }
  }

  // Navigate to posts if the post was updated
  LaunchedEffect(isPostUpdated) {
    if (isPostUpdated) {
      // Return to posts page after successful update
      navController.navigate("posts") {
        popUpTo("posts") { inclusive = true } // Ensure we remove previous layers
      }
    }
  }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ContenidoPostEliminar(
  navController: NavHostController,
  servicio: PostApiService,
  postId: Int = 0
) {
  var isLoading by remember { mutableStateOf(false) }
  var navigateToPosts by remember { mutableStateOf(false) }

  if (navigateToPosts) {
    navController.navigate("posts")
  }

  if (isLoading) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      CircularProgressIndicator()
    }
  } else {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
      // Usamos TextStyle personalizado en lugar de h6
      Text(
        text = "Are you sure you want to delete this post?",
        style = TextStyle(
          fontSize = 20.sp, // Tamaño de fuente más grande
          fontWeight = FontWeight.Bold, // Negrita
          color = MaterialTheme.colorScheme.onSurface // Color que se adapta al tema
        ),
        modifier = Modifier.padding(bottom = 16.dp)
      )

      Button(
        onClick = {
          isLoading = true
          CoroutineScope(Dispatchers.IO).launch {
            try {
              val response = servicio.deletePost(postId)
              if (response.isSuccessful) {
                navigateToPosts = true
              } else {
                Log.e("ContenidoPostEliminar", "Error deleting post: ${response.message()}")
              }
            } catch (e: Exception) {
              Log.e("ContenidoPostEliminar", "Error deleting post: $e")
            } finally {
              isLoading = false
            }
          }
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = !isLoading
      ) {
        if (isLoading) {
          CircularProgressIndicator()
        } else {
          Text("Delete Post")
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = { navController.navigate("posts") },
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("Cancel")
      }
    }
  }
}
