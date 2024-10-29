package com.intecular.invis.data

import android.content.Context
import android.net.ConnectivityManager
import com.intecular.invis.data.entities.response.ErrorResponse
import com.slack.eithernet.ApiResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber
import javax.inject.Inject


interface ErrorHandlerDelegate {

    val errorMessage: SharedFlow<String>

    val errorResponse: SharedFlow<ErrorResponse>

    fun extractErrorMessage(result: ApiResult.Failure<*>)

    fun extractErrorMessage(throwable: Throwable)

    fun reductionErrorResponse()
}

internal class ErrorHandlerDelegateImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : ErrorHandlerDelegate {

    private val _errorMessage = MutableSharedFlow<String>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    private val _errorResponse = MutableSharedFlow<ErrorResponse>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val errorResponse = _errorResponse.asSharedFlow()
    override fun extractErrorMessage(result: ApiResult.Failure<*>) {
        val message = when (result) {
            is ApiResult.Failure.NetworkFailure -> {
                result.error.message
            }

            is ApiResult.Failure.UnknownFailure -> result.error.message
            is ApiResult.Failure.HttpFailure -> {
                val error = result.error
                if (error is ErrorResponse) {
                    _errorResponse.tryEmit(error)
                    Timber.d("Error Response:${error.message}, Code:${result.code}")
                    return
                }
                "HTTP ${result.code}"
            }

            is ApiResult.Failure.ApiFailure -> result.error?.toString()
        } ?: ""

        showErrorMessage(message)
    }

    override fun extractErrorMessage(throwable: Throwable) {
        val message = throwable.message ?: ""
        showErrorMessage(message)
    }

    private fun showErrorMessage(message: String) {
        _errorMessage.tryEmit(message)
    }

    override fun reductionErrorResponse() {
        _errorResponse.tryEmit(ErrorResponse("", ""))
        _errorMessage.tryEmit("")
    }
}