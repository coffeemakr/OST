package android.util

object Log {
    @JvmStatic
    fun d(tag: String, message: String): Int {
        println("DEBUG $tag: $message")
        return 0
    }

    @JvmStatic
    fun w(tag: String, message: String): Int {
        println("WARN  $tag: $message")
        return 0
    }
}