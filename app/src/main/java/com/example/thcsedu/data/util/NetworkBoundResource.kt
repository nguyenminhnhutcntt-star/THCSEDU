package com.example.thcsedu.data.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
): Flow<Resource<ResultType>> = flow {
    emit(Resource.Loading(query().first()))

    val data = query().first()

    if (shouldFetch(data)) {
        emit(Resource.Loading(data))

        try {
            val remoteData = fetch()
            saveFetchResult(remoteData)
            emitAll(query().map { Resource.Success(it) })
        } catch (throwable: Throwable) {
            emitAll(query().map { Resource.Error(throwable.localizedMessage ?: "Unknown Network Error", it) })
        }
    } else {
        emitAll(query().map { Resource.Success(it) })
    }
}
