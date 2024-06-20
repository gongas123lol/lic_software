import isel.leic.utils.Time
import java.io.File

object TUI {

    data class Score(var name: String, var score: Int)
    const val filename = "C:\\Users\\ferra\\Desktop\\HelloWorld_LIC\\HelloWorld_LIC\\src\\main\\kotlin\\scores"
    const val M_MASK = 0x0000 // idk ainda
    const val COIN_MASK = 0x05
    const val gamedata_filename = "C:\\Users\\ferra\\Desktop\\HelloWorld_LIC\\HelloWorld_LIC\\src\\main\\kotlin\\gamedata"
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

        while (KBD.waitKey(10) != '#' || !Maintenance) {
            Maintenance = HAL.isBit(M_MASK)
            Thread.sleep(100)
            if(HAL.isBit(COIN_MASK)) credits += 2
        }
        // play stage
        if (credits <= 0) {
            mainMenu() // no money no play
        } else {
            playgame()
        }
    }

    private fun playgame() {
        // Implementation of the game logic
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
            println("read:" + keyread)
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
