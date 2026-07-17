package com.tospery.base.error

interface AppError {
    val debugMessage: String?
    val cause: Throwable?
}

data class UnknownAppError(
    override val debugMessage: String? = null,
    override val cause: Throwable? = null,
) : AppError