import isel.leic.UsbPort
import isel.leic.utils.*
import kotlin.concurrent.timer
var currOut = 0;

object HAL { // Virtualiza o acesso ao sistema UsbPort
    // Inicia a classe
    fun init(){
        UsbPort.write(0)
    }
    // Retorna true se o bit tiver o valor lógico ‘1’
    fun isBit(mask: Int): Boolean {
        currOut = UsbPort.read()
        val res = mask and currOut
        return res != 0
    }
    // Retorna os valores dos bits representados por mask presentes no UsbPort
    fun readBits(mask: Int): Int{
        currOut = UsbPort.read()
        return mask and currOut
    }
    // Escreve nos bits representados por mask os valores dos bits correspondentes em value
    fun writeBits(mask: Int, value: Int){
        currOut = (value and mask) or (currOut and mask.inv())
        UsbPort.write(currOut)
        return
    }
    // Coloca os bits representados por mask no valor lógico ‘1’
    fun setBits(mask: Int){ // funciona
        val b = currOut or mask
        UsbPort.write(b)
        currOut = b
        return
    }
    // Coloca os bits representados por mask no valor lógico ‘0’
    fun clrBits(mask: Int) { //funciona
        val b = currOut and mask.inv()
        UsbPort.write(b)
        currOut = b
    }
}
