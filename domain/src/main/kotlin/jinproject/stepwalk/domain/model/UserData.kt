package jinproject.stepwalk.domain.model

data class UserData(
    val id : String,
    val password : String,
    val nickname : String,
    val email : String,
    val age : Int,
    val height : Int,
    val weight : Int
)
