package com.example.todonotion.data

import com.example.todonotion.model.Flower
import com.example.todonotion.network.TodoApiService
import retrofit2.http.Query

interface TodosRepository {
    suspend fun getPhotos() : Flower

    suspend fun getPhotosWithKey(@Query(value="q", encoded=true) q: String): Flower
}

class NetworkTodosRepository(
    private val todoApiService: TodoApiService
):TodosRepository{
    override suspend fun getPhotos(): Flower = todoApiService.getPhotos()

    override suspend fun getPhotosWithKey(q: String): Flower = todoApiService.getPhotosWithKey(q)

}