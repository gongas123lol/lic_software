import java.io.File

object TUI{


    const val filename = "C:\\Users\\ferra\\Desktop\\HelloWorld_LIC\\src\\main\\kotlin\\scores"
    const val M_MASK = 0x0000 // idk ainda
    var credits = 0
    var scores = mutableMapOf<String, Int>()
    var Maintenance = false


    fun init(){

        credits = 0
        val read = File(filename).readLines()
        for(line in read){
            val split = line.trim().split(' ') //careful of white spaces
            scores[split[0]] = split[1].toIntOrNull() ?: 0
        }
        println(scores)

        Maintenance = HAL.isBit(M_MASK)

    }

    fun mainMenu(){

        while(KBD.getKey() != '#' || Maintenance){
            Maintenance = HAL.isBit(M_MASK)
            LCD.cursor(0,0)
            LCD.write("SPACE INVADERS")
            LCD.cursor(1,0)
            LCD.write("insert coin, 1eur = 2 credits")
        }
        //play stage
        if(credits <= 0) mainMenu() // no money no play

        playgame()


    }

    private fun playgame(){

    }

    fun turnoff(){

    }
}

fun main(){
    TUI.init()
}