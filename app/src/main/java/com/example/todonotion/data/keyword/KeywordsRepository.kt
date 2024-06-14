package com.example.todonotion.data.keyword

import kotlinx.coroutines.flow.Flow

interface KeywordsRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllKeywordsStream(): Flow<List<Keyword>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getKeywordStream(id: Int): Flow<Keyword?>

    /**
     * Insert item in the data source
     */
    suspend fun insertKeyword(keyword: Keyword)

    /**
     * Delete item from the data source
     */
    suspend fun deleteKeyword(keyword: Keyword)

    /**
     * Update item in the data source
     */
    suspend fun updateKeyword(keyword: Keyword)
}