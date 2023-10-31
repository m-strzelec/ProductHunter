package pl.mobi.msbw.producthunter.models

data class Product(
    val id: String = "",
    val name: String = "",
    val storeName: String = "",
    val storeAddress: String = "",
    val price: Double = 0.0
)

