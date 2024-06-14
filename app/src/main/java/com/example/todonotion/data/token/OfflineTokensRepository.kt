package com.example.todonotion.data.token

import kotlinx.coroutines.flow.Flow

class OfflineTokensRepository (private val tokenDao: TokenDao) : TokensRepository {
    override fun getAllTokensStream(): Flow<List<Token>> = tokenDao.getTokens()

    override fun getTokenStream(id: Int): Flow<Token?> = tokenDao.getToken(id)

    override suspend fun insertToken(token: Token) = tokenDao.insert(token)

    override suspend fun deleteToken(token: Token) = tokenDao.delete(token)

    override suspend fun updateToken(token: Token) = tokenDao.update(token)
}