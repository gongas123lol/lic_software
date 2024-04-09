import isel.leic.UsbPort
import isel.leic.utils.*
import HAL
import kotlin.concurrent.timer


object LCD {

    private const val LINES = 2 //Dimensão do display
    private const val COLS = 16
    private const val SERIAL_INTERFACE = false //define se a interface é série ou paralela
    const val RS_MASK = 0x40
    const val E_MASK = 0x20
    const val CLK_REG_MASK = 0x10
    const val DATA_LOW = 0x0F
    const val DATA_HIGH = 0xF0
    const val SHIFT_4BITS_RIGHT = 4
    const val WAITTIME1 = 15 //tempos de espera na iniciação do LCD
    const val WAITTIME2 = 4.1
    const val WAITTIME3 = 0.0001

    //escreve um byte de comando/dados no LCD em paralelo

        private fun writeByteParallel(rs: Boolean, data: Int) {

        val dataInLow = data and DATA_LOW //dividir os 8 bits em dois blocos de 4
        val data_InHigh = data and DATA_HIGH
        val dataInHIgh = data_InHigh.shr(SHIFT_4BITS_RIGHT) //shift para a direita de 4 bits
             HAL.init()

        if (rs == false) {  //pões o bit do enable a 1 e o bit de RS a 0
            HAL.clrBits(RS_MASK)
        }else HAL.setBits(RS_MASK)

            HAL.setBits(E_MASK)

            HAL.writeBits(DATA_LOW, dataInHIgh) //escreve os bits mais significativos
            HAL.setBits(CLK_REG_MASK) //clock, passa o valor para o registo
             HAL.clrBits(CLK_REG_MASK)//um ciclo de clock

            HAL.writeBits(DATA_LOW,dataInLow)//escreve os valores menos significativos
            HAL.setBits(CLK_REG_MASK) //clock, move denovo os valores
             HAL.clrBits(CLK_REG_MASK)//outro ciclo de clock


            HAL.clrBits(E_MASK) //clear no enable
    }

    //escreve um byte de comandos/dados no LCD em série
    private fun writeByteSerial(rs:Boolean, data: Int){
        if(rs==true)
            HAL.setBits(RS_MASK) //põe o bit de RS a 1
        print("$data")//só para verificar se
    }

    //escreve um byte de comando/dados no LCD
     private fun writeByte(rs:Boolean, data: Int){
        if (rs==false) writeByteParallel(rs,data)
         else writeByteSerial(true,data)
    }

    //escrever um comando no LCD
    private fun writeCMD(data: Int){
        writeByteParallel(false,data)
    }

    //escrever um dado no LCD
    private fun writeDATA(data: Int){
        writeByteParallel(true,data)
    }

    //enviar a sequência de iniciação para a comunicação de 8 bits
    fun init(){
        Time.sleep(WAITTIME1.toLong())
        writeCMD(0b0011_0000)
        Time.sleep(WAITTIME2.toLong())
        writeCMD(0b0011_0000)
        Time.sleep(WAITTIME3.toLong())
        writeCMD(0b0011_0000)

        writeCMD(0b0011_1000)//F=0, pois 5x10 dots ; N=1, pois 2lines
        writeCMD(0b0000_1000)
        writeCMD(0b0000_0001)
        writeCMD(0b0000_0111)//S=1 ; I/D=1, pois
        writeCMD(0b0000_1111)
    }

    //escreve um caracter na posição corrente
    fun write(c:Char){
        writeDATA(c.code)
    }

    //escreve uma string na posição corrente
    fun write(text:String){
        for (i in text.indices) {
            write(text[i])
        }
    }

    //Envia comando para posicionar cursor
    fun cursor(line:Int, column:Int){
        var add= 0
        if (line == 0) add =column
        if (line ==1) add= 0x40 + column

        writeCMD(0b1000_0000 or add)
    }

    //Envia comando para limpar o ecrã e posicionar o cursor em (0,0)
    fun clear(){
        writeCMD(0b0000_0001)
    }
}







