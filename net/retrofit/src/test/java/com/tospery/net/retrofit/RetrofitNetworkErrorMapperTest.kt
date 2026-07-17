package com.tospery.net.retrofit

import com.tospery.net.ReachableError
import com.tospery.net.ServerError
import java.net.UnknownHostException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class RetrofitNetworkErrorMapperTest {
    @Test
    fun httpExceptionKeepsStatusCode() {
        val throwable = HttpException(
            Response.error<Any>(
                403,
                "forbidden".toResponseBody(),
            ),
        )

        val error = RetrofitNetworkErrorMapper().map(throwable)

        assertTrue(error is ServerError.HttpFailure)
        error as ServerError.HttpFailure
        assertEquals(403, error.statusCode)
        assertSame(throwable, error.cause)
    }

    @Test
    fun nonRetrofitExceptionUsesDefaultMapping() {
        val throwable = UnknownHostException("offline")

        val error = RetrofitNetworkErrorMapper().map(throwable)

        assertTrue(error is ReachableError.NoConnectivity)
        assertSame(throwable, error.cause)
    }
}
