object coinAcceptor {

    const val COIN_MASK = 0b01000000
    const val COINACK_MASK = 0b001000000
    private var avaiable = false

    fun check(){
       avaiable =HAL.isBit(COIN_MASK)
    }
    fun read(): Boolean{
        check()
        return avaiable
    }
    fun accept(){
        HAL.setBits(COINACK_MASK)
        HAL.clrBits(COINACK_MASK)
    }


}