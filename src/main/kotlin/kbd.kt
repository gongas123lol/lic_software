import HAL

object KBD { // Ler teclas. Métodos retornam ‘0’..’9’,’#’,’*’ ou NONE.
    const val KBDMASK = 0xFF
    const val KVALMASK = 0x01
    const val NONE = 0
    // Inicia a classe
    fun init(){

    }
// Retorna de imediato a tecla premida ou NONE se não há tecla premida.
    fun getKey(): Char {
        if(HAL.isBit(KVALMASK)){
            val data = HAL.readBits(KBDMASK)
            return getchar(data)
        }else {
            return NONE.toChar()
        }

    }
// Retorna a tecla premida, caso ocorra antes do ‘timeout’ (representado em milissegundos), ou NONE caso contrário.
    fun waitKey(timeout: Long): Char {
    var curr = 0
    while (curr < timeout) {
        Thread.sleep(1)
        val key = getKey()
        if (key != NONE.toChar()) {
            return key
        }
        curr++
    }
    return NONE.toChar()
}

    fun getchar(value: Int): Char{
        return when(value){

            0b0000 -> '1'
            0b0100 -> '2'
            0b1000 -> '3'
            0b0001 -> '4'
            0b0101 -> '5'
            0b1001 -> '6'
            0b0010 -> '7'
            0b0110 -> '8'
            0b1010 -> '9'
            0b0011 -> '*'
            0b0111 -> '0'
            0b1011 -> '#'

            else-> NONE.toChar()

        }
    }
}