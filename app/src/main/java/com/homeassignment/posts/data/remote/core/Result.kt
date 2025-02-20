package com.homeassignment.posts.data.remote.core

/**
 * Represents a value of one of two possible types (a disjoint union).
 * Instances of [Result] are either an instance of [Error] or [Success].
 * FP Convention dictates that [Error] is used for "failure"
 * and [Success] is used for "success".
 *
 * @see Error
 * @see Success
 */
sealed class Result<out E, out S> {

    /**
     * Represents the left side of [Result] class which by convention is a "Failure"
     */
    data class Error<out E>(val error: E) : Result<E, Nothing>()

    /**
     * Represents the right side of [Result] class which by convention is a "Success"
     */
    data class Success<out S>(val data: S) : Result<Nothing, S>()
}
