package jinproject.stepwalk.domain.model

data class UserData(
    val id : String = "",
    val password : String = "",
    val nickname : String = "",
    val email : String = "",
    val age : Int = 0,
    val height : Int = 0,
    val weight : Int = 0
)
