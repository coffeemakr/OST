package ch.unstable.ost.error.model

class ErrorReport(
        val error: ErrorInfo,
        val app: AppInfo,
        val build: BuildInfo,
        val android: AndroidInfo
)