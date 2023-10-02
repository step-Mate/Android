package jinproject.stepwalk.home.state

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Stable
import jinproject.stepwalk.home.utils.onKorea
import java.time.Instant

/**
 * 헬스케어 데이터가 가져야할 필수 정보들
 * @property details 상세 정보 ex) 키 : { 170, 키 이미지, 사용자의 키 정보 입니다. }
 * @property graphItems 그래프에 표현되어져야 할 값 리스트
 */
internal interface HealthMenu {
    @Stable
    val details: Map<String, MenuDetail>

    @Stable
    val graphItems: List<Long>
}

@Stable
internal data class MenuDetail(
    val value: Float,
    @DrawableRes val img: Int,
    val intro: String
)

/**
 * 그래프에 표현되는 헬스케어 정보
 * @property startTime 시작시간
 * @property endTime 끝시간
 * @property graphValue 특정 헬스케어 정보의 값
 */
@Stable
internal interface GraphItem {
    val startTime: Long
    val endTime: Long
    val graphValue: Long
}

internal fun <T: GraphItem> List<T>.addGraphItems(time: Time, dataList: ArrayList<Long>) {
    this.forEach { item ->
        val startTime = item.startTime
        val value = item.graphValue

        val instant = Instant.ofEpochSecond(startTime).onKorea()
        val key = time.toZonedOffset(instant)

        when (time) {
            Time.Day -> dataList[key] = value
            else -> dataList[key - 1] = value
        }
    }
}