import isel.leic.UsbPort
import isel.leic.utils.Time

fun main(args: Array<String>) {

    /*HAL.init()
    HAL.writeBits(0x0F,0x0F)
    HAL.setBits(0x10)

    HAL.clrBits(0x10)
    HAL.setBits(0x10)*/
   LCD.init()
   LCD.write("L")
   Time.sleep(1000)
   LCD.cursor(0,7)
   LCD.write('d')
   Time.sleep(1000)
   LCD.cursor(1,4)
   LCD.write("a")
   Time.sleep(1000)
   LCD.cursor(1,5)
   LCD.write('e')

   Time.sleep(3000)
   LCD.clear()
}