package ch.unstable.ost.error.model

import android.os.Build

class AndroidInfo {
    val sdk: Int = Build.VERSION.SDK_INT
    val release: String = Build.VERSION.RELEASE
}