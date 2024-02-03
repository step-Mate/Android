package jinproject.stepwalk.domain.model

/**
 * 분수를 다루는 클래스
 * @param son 분자
 * @param mother 분모
 */
class Fraction(val son: Int, val mother: Int) {

    /**
     * 분수의 덧셈을 제공하는 메소드
     * @param other 피연산자
     */
    operator fun plus(other: Fraction): Fraction = Fraction(
        son = son * other.mother + other.son * mother,
        mother = mother * other.mother,
    )

    /**
     * 분수를 기약분수로 형태로 반환해주는 메소드
     *
     * 기약분수로 만들기 위해 분자, 분모에 대한 최대공약수로 각각 나눔
     * 
     * @return 기약분수
     */
    fun getIrreducibleFraction(): Fraction {
        val gcd = gcd(son, mother)

        return Fraction(
            son = son / gcd,
            mother = mother / gcd
        )
    }

    /**
     * 유클리드 호제법을 이용한 최대공약수(gcd)를 반환
     */
    private fun gcd(a: Int, b:Int): Int {
        return if(a > b)
            if(a % b == 0)
                b
            else
                gcd(b, a % b)
        else
            if(b % a == 0)
                a
            else
                gcd(a, b % a)

    }
}

/**
 * @suppress 분수의 덧셈을 반환하는 메소드
 * @return List.isEmpty() 이면 null, 아니면 Fraction
 */
fun List<Fraction>.sumOrNull(): Fraction? {
    if(this.isEmpty())
        return null

    var sum: Fraction? = null

    for (element in this) {
        if(sum == null)
            sum = element
        else
            sum += element
    }

    return sum!!.getIrreducibleFraction()
}