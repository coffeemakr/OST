package ch.unstable.lib.sbb.auth

import java.util.*

class DefaultDateSource: DateSource {
    override val currentDate: Date
        get() = Date()
}