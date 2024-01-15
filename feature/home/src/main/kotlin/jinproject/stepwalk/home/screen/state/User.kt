package jinproject.stepwalk.home.screen.state

import androidx.compose.runtime.Stable

@Stable
internal data class User(
    val uid: Long,
    val name: String,
    val age: Int,
    val kg: Float,
    val height: Float,
) {
    companion object {
        fun getInitValues() = User(
            uid = 0L,
            name = "",
            age = 0,
            kg = 55f,
            height = 0f
        )
    }
}