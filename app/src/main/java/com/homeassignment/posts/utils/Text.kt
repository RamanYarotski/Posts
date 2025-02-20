package com.homeassignment.posts.utils

import android.content.Context
import androidx.annotation.StringRes
import com.homeassignment.posts.R
import com.homeassignment.posts.data.remote.core.ErrorType
import com.homeassignment.posts.utils.Text.Resource
import com.homeassignment.posts.utils.Text.Simple

/**
 * An abstraction over text resources / messages to user to be used on viewModel's layer
 */
sealed class Text {
    /** Represents a simple text string */
    data class Simple(val text: String) : Text()

    /** Represents a text from resource */
    data class Resource(@StringRes val id: Int) : Text()
}

fun Text.mapToString(context: Context): String {
    return when (this) {
        is Resource -> context.getString(id)
        is Simple -> text
    }
}

/**
 * Use this function to map [ErrorType] to error message wrapped in [Text]
 */
fun ErrorType.toText(): Text {
    return Text.Resource(
        id = when (this) {
            ErrorType.ServerError -> R.string.generic_error_text
            ErrorType.NetworkError.ConnectionError -> R.string.internet_connection_error
            ErrorType.NetworkError.TimeoutException -> R.string.timeout_error_text
            ErrorType.ClientError.DecodingError -> R.string.decoding_error_text
            ErrorType.ClientError.AuthenticationError -> R.string.authentication_error_text
            ErrorType.ClientError.BadRequestError -> R.string.bad_request_error_text
            ErrorType.ClientError.ForbiddenError -> R.string.forbidden_error_text
            ErrorType.ClientError.NotAllowedError -> R.string.not_allowed_error_text
            ErrorType.ClientError.NotFoundError -> R.string.not_found_error_text
            else -> R.string.msg_unknown_error
        }
    )
}
