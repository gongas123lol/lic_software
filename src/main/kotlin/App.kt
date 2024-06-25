object App {
    fun init(){
        coinAcceptor.read()
        HAL.init()
        TUI.init()
        LCD.init()
        KBD.init()
        ScoreDisplay.init()
    }
    fun play(){
        TUI.mainMenu()
    }
    fun close(){
        TUI.turnoff()
    }
}


fun main(){

    App.init()
    App.play()

}