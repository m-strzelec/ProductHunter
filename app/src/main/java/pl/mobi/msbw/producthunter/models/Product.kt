package pl.mobi.msbw.producthunter.models

data class Product(
    val id: String = "",
    val category: String = "",
    val name: String = "",
    val price: Double = 0.00,
    val storeName: String = "",
    val storeAddress: String = ""
)