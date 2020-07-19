package ch.unstable.ost.views.lists.connection

data class TravelDurations(val duration: UInt, val type: Type) {
    enum class Type {
        WALK,
        WAIT,
        TRAVEL
    }
}
