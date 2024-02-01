package jinproject.stepwalk.domain.model

data class Fraction(val son: Int, val mother: Int) {

    operator fun plus(other: Fraction): Fraction = Fraction(
        son = son * other.mother + other.son * mother,
        mother = mother * other.mother,
    ).getIrreducibleFraction()

    private fun getIrreducibleFraction(): Fraction {
        val gcd = gcd(son, mother)

        return Fraction(
            son = son / gcd,
            mother = mother / gcd
        )
    }

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

fun List<Fraction>.sum(): Fraction {
    var sum = Fraction(1, 1)
    for (element in this) {
        sum += element
    }
    return sum
}