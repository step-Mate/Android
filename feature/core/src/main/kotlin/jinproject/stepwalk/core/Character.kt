package jinproject.stepwalk.core

fun getCharacter(level: Int): String  =
    when {
        level < 10 -> "ch_caterpillars.json"
        level < 20 -> "ch_broccoli.json"
        level < 30 -> "ch_avocado.json"
        level < 40 -> "ch_coffee.json"
        level < 50 -> "ch_donut.json"
        level < 60 -> "ch_fat_bird.json"
        level < 70 -> "ch_french_fries.json"
        level < 80 -> "ch_orange.json"
        level < 90 -> "ch_plant.json"
        level < 100 -> "ch_sandwich.json"
        level < 110 -> "ch_watermelon.json"
        level < 120 -> "ch_fox.json"
        else -> "ic_anim_running_1.json"
    }