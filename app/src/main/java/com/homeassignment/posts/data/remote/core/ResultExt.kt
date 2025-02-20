package com.homeassignment.posts.data.remote.core

/**
 * Map success result of type [S] to some other type [R]
 */
inline fun <E, S, R> Result<E, S>.mapSuccess(block: (S) -> R): Result<E, R> =
    when (this) {
        is Result.Success -> Result.Success(block(data))
        is Result.Error -> Result.Error(this.error)
    }

/**
 * Map error result of type [E] to some other type [R]
 */
inline fun <E, S, R> Result<E, S>.mapError(block: (E) -> R): Result<R, S> =
    when (this) {
        is Result.Success -> Result.Success(this.data)
        is Result.Error -> Result.Error(block(error))
    }


/**
 * Some action done after [Result.Success] completion of action
 */
inline fun <E, S> Result<E, S>.doOnSuccess(block: (S) -> Unit): Result<E, S> {
    if (this is Result.Success) {
        block(this.data)
    }
    return this
}

/**
 * Some action done after [Result.Error] completion of action
 */
inline fun <E, S> Result<E, S>.doOnError(block: (E) -> Unit): Result<E, S> {
    if (this is Result.Error) {
        block(this.error)
    }
    return this
}
