package com.vinio.haze.domain.exceptions

class NetworkException(cause: Throwable): Exception("Network error", cause)