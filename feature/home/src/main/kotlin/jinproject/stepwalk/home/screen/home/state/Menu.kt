package jinproject.stepwalk.home.screen.home.state

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Stable
import jinproject.stepwalk.design.R

@Stable
internal data class MenuItem(
    val value: Float,
    @DrawableRes val img: Int,
    val intro: String,
)

internal interface MenuFactory {
    fun <T> create(v: T): MenuItem
}

interface CalculateMenu {
    fun <T> cal(v: T): Float
}

internal interface StepCalculateMenu : CalculateMenu {
    override fun <T> cal(v: T): Float {
        if (v !is Number)
            throw IllegalArgumentException("걸음수 메뉴 계산에는 숫자값 만 허용: ${v.toString()}")
        return cal(v)
    }

    fun cal(v: Number): Float
}

internal object DistanceMenuFactory : MenuFactory, StepCalculateMenu {
    const val intro = "거리(km)"

    override fun <T> create(v: T): MenuItem {
        return MenuItem(cal(v), R.drawable.ic_person_walking, intro)
    }

    override fun cal(v: Number): Float {
        return v.toFloat() * 0.0008f
    }
}

internal class CaloriesMenuFactory(
    private val weight: Int,
) : MenuFactory, StepCalculateMenu {
    val intro = "칼로리(Kcal)"
    override fun <T> create(v: T): MenuItem {
        return MenuItem(cal(v), R.drawable.ic_fire, intro)
    }

    override fun cal(v: Number): Float {
        val mets = 3.0
        return (mets * (3.5 * weight * TimeMenuFactory.cal(v).toDouble()) * 5 / 1000).toFloat()
    }
}

internal object TimeMenuFactory : MenuFactory, StepCalculateMenu {
    const val intro = "시간(분)"
    override fun <T> create(v: T): MenuItem {
        return MenuItem(cal(v), R.drawable.ic_fire, intro)
    }

    override fun cal(v: Number): Float {
        return (v.toFloat() * 0.0008).toFloat() * 15
    }
}

internal interface HeartRateCalculateMenu : CalculateMenu {
    override fun <T> cal(v: T): Float {
        if (v !is List<*> || v.firstOrNull() !is HeartRate)
            throw IllegalArgumentException("심박수 메뉴 계산에는 심박수 리스트값 만 허용하며 비어있을 수 없음: ${v.toString()}")
        return cal(v)
    }

    fun cal(v: List<*>): Float
}

internal object HeartMinMenuFactory : MenuFactory, HeartRateCalculateMenu {
    override fun <T> create(v: T): MenuItem {
        return MenuItem(cal(v), R.drawable.ic_heart_solid, "최소(분)")
    }

    override fun cal(v: List<*>): Float {
        return v.map { (it as HeartRate).min }.average().toFloat()
    }
}

internal object HeartAvgMenuFactory : MenuFactory, HeartRateCalculateMenu {
    override fun <T> create(v: T): MenuItem {
        return MenuItem(cal(v), R.drawable.ic_heart_solid, "평균(분)")
    }

    override fun cal(v: List<*>): Float {
        return v.map { (it as HeartRate).avg }.average().toFloat()
    }
}

internal object HeartMaxMenuFactory : MenuFactory, HeartRateCalculateMenu {
    override fun <T> create(v: T): MenuItem {
        return MenuItem(cal(v), R.drawable.ic_heart_solid, "최대(분)")
    }

    override fun cal(v: List<*>): Float {
        return v.map { (it as HeartRate).max }.average().toFloat()
    }
}