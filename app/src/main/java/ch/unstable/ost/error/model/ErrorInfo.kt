package ch.unstable.ost.error.model

import java.io.PrintWriter
import java.io.StringWriter
import java.util.*

class ErrorInfo(private val stackTrace: String?) {
    val timestamp = Date()
    val exception: Throwable? = null

    fun getStackTrace(): String {
        return if (exception == null && stackTrace == null) {
            ""
        } else if (stackTrace != null) {
            stackTrace
        } else {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            exception!!.printStackTrace(pw)
            sw.toString()
        }
    }

    companion object {
        @JvmField
        val EMPTY = ErrorInfo(null as String?)
    }
}