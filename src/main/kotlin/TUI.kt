import isel.leic.utils.Time
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import kotlin.random.Random

object TUI {

    data class Score(var name: String, var score: Int)
    const val filename = "src/main/kotlin/scores"
    const val M_MASK = 0b10000000

    const val gamedata_filename = "src/main/kotlin/gamedata"
    var credits = 0
    var scores = mutableListOf<Score>()
    var Maintenance = false
    var gamecnt = 0
    var creditcnt = 0
    var isOn = true

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
        LCD.clear()
        LCD.cursor(0, 0)
        LCD.write("SPACE INVADERS")
        LCD.cursor(1, 0)
        LCD.write("cr: ${credits}")
        var lastScoreDisplayTime = LocalTime.now()
        Maintenance = HAL.isBit(M_MASK)
        var cl = KBD.waitKey(10)

        while (cl != '#' && !Maintenance) {
            cl = KBD.waitKey(10)
            if(cl == '*'){
                turnoff()
            }

            Maintenance = HAL.isBit(M_MASK)
            if(coinAcceptor.read()){
                credits += 2
                creditcnt += 2
                println("coin in")
                coinAcceptor.accept()
                mainMenu()
            }
            val currentTime = LocalTime.now()
            if(lastScoreDisplayTime.plusSeconds(20).isBefore(currentTime) && isOn){
                while(displayscores() != 1) {
                }
                lastScoreDisplayTime = currentTime
            }
        }
        if (credits <= 0 && !Maintenance) {
            mainMenu() // no money no play
        } else if(credits > 0 && !Maintenance) {
            credits--
            playgame()
        }else if(Maintenance){
            doMaintenance();

        }
    }


    private fun playgame() {
        LCD.clear()
        LCD.cursor(0, 0)
        LCD.write("  Starting")
        LCD.cursor(1, 0)
        for (i in 0..15) {
            LCD.write(".")
            Time.sleep(200)
        }
        LCD.clear()

        var playerpos = 0 // alternates between 0 and 1
        val enemies = arrayOf(
            CharArray(16) { ' ' }, // Enemies on line 0
            CharArray(16) { ' ' }  // Enemies on line 1
        )
        enemies[0][15] = '#'
        enemies[1][15] = '#'
        gamecnt++
        var currScore = Score("wip",0)
        var tickCounter = 0
        var enemySpeed = 5 // Number of ticks before enemies move
        var spawnFrequency = 30 // Number of ticks before a new enemy spawns

        while (true) {
            // Check for key press to move player or kill enemy
            val key = KBD.waitKey(100)
            if (key == '2') playerpos = 0
            if (key == '5') playerpos = 1
            if (key == '#') {
                if (playerpos == 0 && enemies[0].contains('#')) {
                    // Kill enemy on line 0
                    enemies[0][enemies[0].indexOf('#')] = ' '
                    currScore.score++
                    //enemySpeed++
                    if(spawnFrequency > 10){
                        spawnFrequency--
                    }
                    ScoreDisplay.setScore(currScore.score)

                } else if (playerpos == 1 && enemies[1].contains('#')) {
                    // Kill enemy on line 1
                    enemies[1][enemies[1].indexOf('#')] = ' '
                    currScore.score++
                    //enemySpeed++
                    if(spawnFrequency > 10){
                        spawnFrequency--
                    }
                    ScoreDisplay.setScore(currScore.score)
                }
            }

            // Update player position
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

            // Move enemies left every enemySpeed ticks
            if (tickCounter % enemySpeed == 0) {
                for (i in 0 until 15) {
                    if (enemies[0][i + 1] == '#') {
                        enemies[0][i] = '#'
                        enemies[0][i + 1] = ' '
                    }
                    if (enemies[1][i + 1] == '#') {
                        enemies[1][i] = '#'
                        enemies[1][i + 1] = ' '
                    }
                }

                // Draw enemies
                for (i in 0 until 16) {
                    LCD.cursor(0, i + 1)
                    LCD.write(enemies[0][i])
                    LCD.cursor(1, i + 1)
                    LCD.write(enemies[1][i])
                }

                // Check if player dies
                if ((enemies[0][0] == '#' && playerpos == 0) || (enemies[1][0] == '#' && playerpos == 1)) {

                    break
                }
            }

            // Spawn a new enemy every spawnFrequency ticks
            if (tickCounter % spawnFrequency == 0) {
                val newEnemyLine = Random.nextInt(2) // Randomly choose line 0 or 1
                enemies[newEnemyLine][15] = '#'
            }

            tickCounter++
        }

        LCD.clear()
        LCD.cursor(0, 0)
        LCD.write("game over")
        LCD.cursor(1,0)
        LCD.write("# -> exit")
        while (KBD.waitKey(100) != '#') {
            Time.sleep(100)
        }

        LCD.clear()
        LCD.cursor(0,0)
        val alphabet = ('A'..'Z').toList()
        var cnt = 0
        var written = ""
        LCD.write("name?")
        LCD.cursor(1, 0)
        var currentchar = 'A'
        while (true) {
            LCD.cursor(1,0)
            LCD.write("                ")
            LCD.cursor(1,0)
            LCD.write(written + currentchar)
            val pressed = KBD.waitKey(100)

            when (pressed) {
                '#' -> {
                    written = written + currentchar
                    currentchar = 'A'
                }
                '*' -> {
                    // Move to the next letter in the alphabet
                    cnt = (cnt + 1) % alphabet.size
                    currentchar = alphabet[cnt]

                }
                '9' -> {
                    // End input
                    written += currentchar
                    break
                }
                '8' -> {
                    // Delete the last letter if there's any
                    if (written.isNotEmpty()) {
                        written = written.dropLast(1)
                    }
                }
            }
        }

        currScore.name = written
        scores+= currScore
        scores = scores.sortedByDescending { it.score }.toMutableList()

        LCD.clear()
        LCD.cursor(0, 0)
        LCD.write(currScore.name + ": " + currScore.score.toString())
        LCD.cursor(1, 0)
        LCD.write("top " + scores.indexOf(currScore).toString())
        Time.sleep(5000)
        //now we go back to the menu
        mainMenu()

    }









    // adds a new score
    fun addscore(name: String, score: Int) {
        scores += Score(name, score)
    }

    private fun writeScoresToFile(scores: List<Score>, fileName: String) {
        File(fileName).bufferedWriter().use { out ->
            for (score in scores) {
                out.write("${score.name} ${score.score}")
                out.newLine()
            }
        }
    }

    private fun writeDataToFile(gamecnt: Int, creditcnt: Int) {
        File(gamedata_filename).bufferedWriter().use { out ->
            out.write("gamecounter: ${gamecnt}")
            out.newLine()
            out.write("creditcounter: ${creditcnt}")
            out.newLine()
        }
    }

    fun turnoff() {
        isOn = false
        LCD.clear()
        LCD.cursor(0,0)
        LCD.write("goodbye!")
        LCD.cursor(1,0)
        for(i in 0..15){
            LCD.write('.')
            Time.sleep(200)
        }
        // save the scores and data
        writeScoresToFile(scores, filename)
        writeDataToFile(gamecnt, creditcnt)

        LCD.clear()
        while (true){

        }

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
    fun doMaintenance(){
        LCD.clear()
        LCD.cursor(0,0)
        LCD.write("play:1,Conslt:#")
        LCD.cursor(1,0)
        LCD.write("off: 2,MM:*")

        var choosen = KBD.waitKey(100)
        while(choosen == KBD.NONE.toChar()){
            choosen = KBD.waitKey(100)

            if(choosen == '1'){
                playgame()
            }else if(choosen == '#'){
                LCD.clear()
                LCD.cursor(0,0)
                LCD.write("total games: ${gamecnt}")
                LCD.cursor(1,0)
                LCD.write("total credits: ${creditcnt}")
                while (KBD.waitKey(100) != '#'){
                }
                mainMenu()
            }else if(choosen == '2'){
                LCD.clear()
                LCD.write("Good Bye!")
                Time.sleep(2000)
                turnoff()
            }
            else if(choosen == '*'){
                mainMenu()
            }
        }
    }
}

fun main() {
    KBD.init()
    LCD.init()
    TUI.init()
    Time.sleep(100)
   TUI.mainMenu()
    //while(TUI.displayscores() != 1){} // loop enquanto n faz nada
    LCD.clear()
}
