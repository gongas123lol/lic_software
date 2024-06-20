fun main(){
    HAL.init()
    LCD.init()
    KBD.init()
    while(true){

        val a = KBD.waitKey(10)
        if(a != KBD.NONE.toChar()){
            LCD.clear()
            LCD.write(a)
        }

        Thread.sleep(10)
    }

}