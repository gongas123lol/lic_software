object SerialEmitter { // Envia tramas para os diferentes módulos Serial Receiver.

    const val DATAX_MASK = 0b00001000 //idk
    const val LCD_SS_MASK = 0b00000001
    const val SC_SS_MASK = 0b00000010
    const val CLOCK_MASK = 0b00010000 //idk


    enum class Destination {LCD, SCORE}
    // Inicia a classe
    fun init(){
        HAL.setBits(LCD_SS_MASK) //reset aos SS
        HAL.setBits(SC_SS_MASK)
    }
// Envia uma trama para o SerialReceiver identificado o destino em addr,os bits de dados em
// ‘data’ e em size o número de bits a enviar.

    fun send(addr: Destination, data: Int, size : Int) {
        var tobesent = 0
        var total = 0

        if(addr != Destination.LCD){
            HAL.setBits(LCD_SS_MASK)
            HAL.clrBits(SC_SS_MASK)
        }else{
            HAL.clrBits(LCD_SS_MASK)
            HAL.setBits(SC_SS_MASK)
        }

        for(i in 0 until    size){
            tobesent = (data shr i) and 1
            total += tobesent
            if(tobesent == 1){
                HAL.setBits(DATAX_MASK)
                //pulsar o clock
                HAL.setBits(CLOCK_MASK)
                HAL.clrBits(CLOCK_MASK)
            }else{
                HAL.clrBits(DATAX_MASK)
                HAL.setBits(CLOCK_MASK)
                HAL.clrBits(CLOCK_MASK)
            }



        }

        if(total % 2 == 0){ // enviar o parity bit correto
            HAL.clrBits(DATAX_MASK)

        }else{
            HAL.setBits(DATAX_MASK)
        }
        HAL.setBits(CLOCK_MASK)
        HAL.clrBits(CLOCK_MASK)

        //desligar os selects
        HAL.setBits(LCD_SS_MASK)
        HAL.setBits(SC_SS_MASK)

    }
}