package com.example.ejercicio_1.data

import retrofit2.Response
import retrofit2.http.*

interface PostApiService {
  @GET("posts")
  suspend fun getPosts(
    @Query("limit") limit: Int = 4,
    @Query("skip") skip: Int = 0
  ): Response<PostsResponse>

  @GET("posts/{id}")
  suspend fun getPost(@Path("id") id: String): Response<PostModel>

  @POST("posts/add")
  suspend fun createPost(@Body post: PostModel): Response<PostModel>

  @PUT("posts/{id}")
  suspend fun updatePost(post: PostModel): Response<PostModel>

  @DELETE("posts/{id}")
  suspend fun deletePost(@Path("id") id: Int): Response<Unit>
}

data class PostsResponse(
  val posts: List<PostModel>,
  val total: Int,
  val skip: Int,
  val limit: Int
)
