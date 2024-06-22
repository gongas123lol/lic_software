fun main(){
    val enemies = arrayOf(
        charArrayOf('#', '#'),
        charArrayOf('#', '#')
    )
   enemies[0]= enemies[0].plus('#')
    println(enemies[0])
    HAL.init()
    LCD.init()
    KBD.init()
    while(true){
        println(HAL.isBit(TUI.M_MASK))
        val a = KBD.waitKey(10)
        if(a != KBD.NONE.toChar()){
            LCD.clear()
            LCD.write(a)
        }

        Thread.sleep(10)
    }

}