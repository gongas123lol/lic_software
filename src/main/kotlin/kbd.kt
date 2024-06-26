import isel.leic.utils.Time

object KBD { // Ler teclas. Métodos retornam ‘0’..’9’,’#’,’*’ ou NONE.
    const val NONE = 0;
    val KACK_MSK = 0b10000000
    val KVAL_MSK = 0b00010000
    val KMSK = 0x0F
    val digits = arrayOf('1', '4', '7', '*', '2', '5', '8', '0', '3', '6', '9', '#' , NONE.toChar(), NONE.toChar(), NONE.toChar(), NONE.toChar())
    var isEnabled = true

    // Inicia a classe
    fun init() {
        HAL.clrBits(KACK_MSK)
    }

    // Retorna de imediato a tecla premida ou NONE se não há tecla premida.
    fun getKey(): Char { // Executa a rotina de ler a key, de acordo com o diagrama temporal

        if(isEnabled) {
            if (HAL.readBits(KVAL_MSK) > 0) {
                val key = HAL.readBits(KMSK)
                HAL.setBits(KACK_MSK)
                while (HAL.isBit(KVAL_MSK)) {
                }
                HAL.clrBits(KACK_MSK)
                return digits[key]  // Vai buscar o digito em questão ao array de digitos
            }
            return NONE.toChar()
        }
        else return NONE.toChar()
    }


    // Retorna a tecla premida, caso ocorra antes do ‘timeout’ (representado em milissegundos), ou NONE caso contrário.
    fun waitKey(timeout: Long): Char {
        if(isEnabled) {
            val t = Time.getTimeInMillis()  // Valor atual de tempo
            while (Time.getTimeInMillis() - t < timeout) {  // Enquanto o tempo passado for inferior ao timeout, vai se buscar a key.
                val key = getKey()
                if (key != NONE.toChar()) return key
            }
            return NONE.toChar()
        }
        else return NONE.toChar()
    }
}

fun main() {
    HAL.init()
    KBD.init()

    while(true){
        println(KBD.waitKey(1000))
    }

}