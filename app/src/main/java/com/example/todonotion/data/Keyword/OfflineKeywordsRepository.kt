package com.example.todonotion.data.Keyword

import kotlinx.coroutines.flow.Flow

class OfflineKeywordsRepository  (private val keywordDao: KeywordDao) : KeywordsRepository {
    override fun getAllKeywordsStream(): Flow<List<Keyword>> = keywordDao.getKeys()

    override fun getKeywordStream(id: Int): Flow<Keyword?> = keywordDao.getKey(id)

    override suspend fun insertKeyword(keyword: Keyword) = keywordDao.insert(keyword)

    override suspend fun deleteKeyword(keyword: Keyword) = keywordDao.delete(keyword)

    override suspend fun updateKeyword(keyword: Keyword) = keywordDao.update(keyword)
}