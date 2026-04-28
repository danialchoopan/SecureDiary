package ir.danialchoopan.securediarydanial.domain.model

data class DiaryEntry(
    val id: Long = 0,
    val title: String,
    val content: String,
    val photoPath: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val timestamp: Long = System.currentTimeMillis()
)
