package pl.mobi.msbw.producthunter.models

data class ShoppingList(
    val listName: String,
    val products: List<ShoppingListItem>
)
