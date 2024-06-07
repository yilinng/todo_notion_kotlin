package com.example.todonotion.data

import com.example.todonotion.model.Flower
import com.example.todonotion.network.TodoApiService
import retrofit2.http.Query
import javax.inject.Inject

interface TodosRepository {
    suspend fun getPhotos(): Flower

    suspend fun getPhotosById(@Query(value = "id", encoded = true) id: String): Flower

    suspend fun getPhotosWithKey(@Query(value = "q", encoded = true) q: String): Flower
}

class NetworkTodosRepository @Inject constructor(
    private val todoApiService: TodoApiService
) : TodosRepository {
    override suspend fun getPhotos(): Flower = todoApiService.getPhotos()
    override suspend fun getPhotosById(id: String): Flower = todoApiService.getPhotosById(id)
    override suspend fun getPhotosWithKey(q: String): Flower = todoApiService.getPhotosWithKey(q)

}