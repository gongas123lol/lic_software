object ScoreDisplay { // Controla o mostrador de pontuação.
    // Inicia a classe, estabelecendo os valores iniciais.

    var output = emptyList<Int>()
    fun init(){
        val send = 0b1110000
        SerialEmitter.send(SerialEmitter.Destination.SCORE,send,7)
    }
    // Envia comando para atualizar o valor do mostrador de pontuação
    fun setScore(value: Int) {
        if(value.toString().length >= 5){
            throw error("too big")
        }
        val tosend = value.toString()

        for(i in 0..tosend.length - 1){
            var towrite = i
            towrite = towrite shl 4 //shifts it so it goes to the command part of the thing
            towrite = towrite or (tosend[i].toString().toInt())
            output+=towrite
            println("___")
            SerialEmitter.send(SerialEmitter.Destination.SCORE,towrite,7)
            println("___")
        }
    }
    // Envia comando para desativar/ativar a visualização do mostrador de pontuação
    fun off(value: Boolean){
        val turnoff = 0b1110001
        SerialEmitter.send(SerialEmitter.Destination.SCORE,turnoff,7)
    }
}
fun main(){
    ScoreDisplay.setScore(123)
    println(ScoreDisplay.output)
    println(0b0010)
}

