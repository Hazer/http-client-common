package org.jetbrains.kotlin.common.httpclient

import java.nio.charset.*

actual class Utils {
    actual companion object {
        actual fun decode(array: ByteArray, charset: String): String =
                array.toString(Charset.forName(charset))
    }
}