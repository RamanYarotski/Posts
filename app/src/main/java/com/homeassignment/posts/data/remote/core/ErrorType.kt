package com.homeassignment.posts.data.remote.core

sealed class ErrorType {
    data object ServerError : ErrorType()

    sealed class NetworkError : ErrorType() {
        data object ConnectionError : NetworkError()
        data object TimeoutException : NetworkError()
    }

    sealed class ClientError : ErrorType() {
        data object DecodingError : ClientError()
        data object BadRequestError : ClientError()
        data object AuthenticationError : ClientError()
        data object PaymentRequiredError : ClientError()
        data object ForbiddenError : ClientError()
        data object NotFoundError : ClientError()
        data object NotAllowedError : ClientError()
        data object OtherClientError : ClientError()
    }
}
