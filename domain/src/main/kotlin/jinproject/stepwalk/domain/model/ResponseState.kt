package jinproject.stepwalk.domain.model

sealed interface ResponseState<out T> {
    data class Result<out T>(val data : T) : ResponseState<T>
    data class Exception(val code : Int, val message : String) : ResponseState<Nothing>
}

inline fun <reified T : Any> ResponseState<T>.onSuccess(action : (data : T) -> Unit) {
    if (this is ResponseState.Result) action(data)
}

inline fun <reified T : Any> ResponseState<T>.onException(action : (code : Int, message : String) -> Unit) {
    if (this is ResponseState.Exception) action(code,message)
}

fun <T> transResponseState(code: Int, message: String, result : T) : ResponseState<T> =
    if (code == 200) ResponseState.Result(result) else ResponseState.Exception(code,message)