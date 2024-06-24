import isel.leic.utils.Time
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import kotlin.random.Random

object TUI {

    data class Score(var name: String, var score: Int)
    const val filename = "src/main/kotlin/scores"
    const val M_MASK = 0b10000000
    const val COIN_MASK = 0b01000000
    const val COINACK_MASK = 0b001000000
    const val gamedata_filename = "src/main/kotlin/gamedata"
    var credits = 0
    var scores = mutableListOf<Score>()
    var Maintenance = false
    var gamecnt = 0
    var creditcnt = 0

    fun init() {
        // init part
        credits = 0
        val read = File(filename).readLines()
        for (line in read) {
            val split = line.trim().split(' ') // careful of white spaces
            scores += Score(split[0], split[1].toIntOrNull() ?: 0)
        }
        println(scores)

        Maintenance = HAL.isBit(M_MASK)
        val info = File(gamedata_filename).readLines()
        gamecnt = info[0].split(' ')[1].toIntOrNull() ?: 0
        creditcnt = info[1].split(' ')[1].toIntOrNull() ?: 0
    }

    fun mainMenu() {
        LCD.cursor(0, 0)
        LCD.write("SPACE INVADERS")
        LCD.cursor(1, 0)
        LCD.write("1eur = 2 credits")
        var lastScoreDisplayTime = LocalTime.now()
        Maintenance = HAL.isBit(M_MASK)

        while (KBD.waitKey(10) != '#' && !Maintenance) {
            Maintenance = HAL.isBit(M_MASK)
            if(HAL.isBit(COIN_MASK)){
                credits += 2
                HAL.setBits(COINACK_MASK)
                HAL.clrBits(COINACK_MASK)
            }
            val currentTime = LocalTime.now()
            if(lastScoreDisplayTime.plusSeconds(20).isBefore(currentTime)){
                while(displayscores() != 1) {
                    // Keep displaying scores until a key is pressed
                }
                lastScoreDisplayTime = currentTime
            }
        }
        println("aaa")
        // play stage
        if (credits <= 0 && !Maintenance) {
            mainMenu() // no money no play
        } else {
            playgame()
        }
    }

    private fun playgame() {
        LCD.clear()
        LCD.cursor(0, 0)
        LCD.write("  Starting")
        LCD.cursor(1, 0)
        for (i in 0..15) {
            LCD.write(".")
            Thread.sleep(200)
        }
        LCD.clear()

        var playerpos = 0 // alternates between 0 and 1
        val enemies = arrayOf(
            CharArray(16) { ' ' }, // Enemies on line 0
            CharArray(16) { ' ' }  // Enemies on line 1
        )
        enemies[0][15] = '#'
        enemies[1][15] = '#'

        while (true) {
            val key = KBD.waitKey(100)
            if (key == '2') playerpos = 0
            if (key == '5') playerpos = 1

            // Check for '#' to kill the enemy
            if (key == '#') {
                if (playerpos == 0 && enemies[0].contains('#')) {
                    // Kill enemy on line 0
                    enemies[0][enemies[0].indexOf('#')] = ' '
                } else if (playerpos == 1 && enemies[1].contains('#')) {
                    // Kill enemy on line 1
                    enemies[1][enemies[1].indexOf('#')] = ' '
                }
                // Add a new random enemy in one of the lines
                val newEnemyLine = Random.nextInt(2) // Randomly choose line 0 or 1
                val newEnemyPos = 15 // Start at the rightmost position
                enemies[newEnemyLine][newEnemyPos] = '#'
            }

            // Update player position without clearing the screen
            if (playerpos == 0) {
                LCD.cursor(0, 0)
                LCD.write('P')
                LCD.cursor(1, 0)
                LCD.write(' ')
            } else {
                LCD.cursor(0, 0)
                LCD.write(' ')
                LCD.cursor(1, 0)
                LCD.write('P')
            }

            // Clear the screen except for player position
            LCD.cursor(0, 1)
            LCD.write(" ".repeat(15))
            LCD.cursor(1, 1)
            LCD.write(" ".repeat(15))

            // Update enemies position
            var playerDied = false
            for (i in 15 downTo 1) {
                if (enemies[0][i] == '#') {
                    enemies[0][i] = ' '
                    enemies[0][i - 1] = '#'
                }
                if (enemies[1][i] == '#') {
                    enemies[1][i] = ' '
                    enemies[1][i - 1] = '#'
                }
            }

            // Check if player dies
            if (enemies[0][0] == '#' && playerpos == 0) {
                playerDied = true
            }
            if (enemies[1][0] == '#' && playerpos == 1) {
                playerDied = true
            }

            // Draw enemies
            for (i in 0 until 16) {
                LCD.cursor(0, i + 1)
                LCD.write(enemies[0][i])
                LCD.cursor(1, i + 1)
                LCD.write(enemies[1][i])
            }

            if (playerDied) {
                LCD.clear()
                LCD.cursor(0, 0)
                LCD.write("Game Over")
                Thread.sleep(2000)
                break
            }

            Thread.sleep(500)
        }
        LCD.clear()
        LCD.cursor(0,0)
        LCD.write("game over")
        while (true){

        }
    }



    // adds a new score
    fun addscore(name: String, score: Int) {
        scores += Score(name, score)
    }

    fun writeScoresToFile(scores: List<Score>, fileName: String) {
        File(fileName).bufferedWriter().use { out ->
            for (score in scores) {
                out.write("${score.name} ${score.score}")
                out.newLine()
            }
        }
    }

    fun writeDataToFile(gamecnt: Int, creditcnt: Int) {
        File(gamedata_filename).bufferedWriter().use { out ->
            out.write("gamecounter: ${gamecnt}")
            out.newLine()
            out.write("creditcounter: ${creditcnt}")
            out.newLine()
        }
    }

    fun turnoff() {
        // save the scores and data
        writeScoresToFile(scores, filename)
        writeDataToFile(gamecnt, creditcnt)
    }

    var counter = 0

    fun displayscores(): Int {
        LCD.clear()
        LCD.cursor(0, 0)
        val towrite = scores[counter]
        LCD.write(towrite.name + " : " + towrite.score)

        var cnt = 20 // show each score for 2s and check for any key pressed
        while (cnt != 0) {
            cnt--
            val keyread = KBD.waitKey(100)
            if (keyread != KBD.NONE.toChar()) return 1
        }
        counter = (counter + 1) % scores.size
        LCD.cursor(1, 0)
        if (scores.size > counter) {
            val towrite2 = scores[counter]
            LCD.write(towrite2.name + " : " + towrite2.score)
            cnt = 20
            while (cnt != 0) {
                cnt--
                if (KBD.waitKey(100) != KBD.NONE.toChar()) return 1
            }
        }
        return 0
    }
}

fun main() {
    KBD.init()
    LCD.init()
    TUI.init()
    Thread.sleep(100)
   TUI.mainMenu()
    while(TUI.displayscores() != 1){} // loop enquanto n faz nada
    LCD.clear()
}
