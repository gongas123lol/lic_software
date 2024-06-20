object ScoreDisplay {
    var output = emptyList<Int>()

    fun init() {
        ScoreDisplay.on(false)
        ScoreDisplay.on(true)
    }

    fun setScore(value: Int) {
        //iteramos o valor 123 passa para "123" e vamos digito a digito escrever e no final damos confirmar
        val scoreString = value.toString().reversed()

        if (scoreString.length > 7) {
            throw IllegalArgumentException("Score too big")
        }

        for (i in scoreString.indices) {
            var towrite = i
            towrite = towrite or (scoreString[i].toString().toInt() shl 3)
            output += towrite
            SerialEmitter.send(SerialEmitter.Destination.SCORE, towrite, 7)
        }
        //command to update
        SerialEmitter.send(SerialEmitter.Destination.SCORE, 0b0000_110, 7)
    }
    fun clear(){
        val scoreString = "000000"

        if (scoreString.length > 7) {
            throw IllegalArgumentException("Score too big")
        }

        for (i in scoreString.indices) {
            var towrite = i
            towrite = towrite or (scoreString[i].toString().toInt() shl 3)
            output += towrite
            SerialEmitter.send(SerialEmitter.Destination.SCORE, towrite, 7)
        }
        //command to update
        SerialEmitter.send(SerialEmitter.Destination.SCORE, 0b0000_110, 7)
    }
    fun idle(state: Int){ //state varia de 1 a 4
        //LD = 0b1101_000 -> 13
        //RD = 0b1110_000 -> 14
        //RU = 0b1010_000 -> 10
        //LU = 0b1011_000 -> 11
        val send = 9 + state
        clear()
        for(i in 0..5){
            SerialEmitter.send(SerialEmitter.Destination.SCORE, (send shl 3) or i, 7)
        }
        //command to update
        SerialEmitter.send(SerialEmitter.Destination.SCORE, 0b0000_110, 7)
    }

    fun on(value: Boolean) {
        if (value) {
            val send = 0b0000_111
            SerialEmitter.send(SerialEmitter.Destination.SCORE, send, 7)
        } else {
            val turnoff = 0b0001_111
            SerialEmitter.send(SerialEmitter.Destination.SCORE, turnoff, 7)
        }
    }
}

fun main() {
    HAL.init()
    Thread.sleep(500)
    ScoreDisplay.init()
    var cnt = 1
    while (true){
        ScoreDisplay.idle(cnt)
        cnt++
        if(cnt > 4) cnt = 1
        Thread.sleep(500)
    }

    Thread.sleep(500)
    ScoreDisplay.setScore(340)
    ScoreDisplay.setScore(0)
    Thread.sleep(500)
    ScoreDisplay.setScore(123456)
    ScoreDisplay.idle(1)
    Thread.sleep(1000)
}
