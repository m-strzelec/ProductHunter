package pl.mobi.msbw.producthunter.adapters

import pl.mobi.msbw.producthunter.models.Product

interface OnProductItemClickListener {
    fun onAddToProductListClick(product: Product) {}
    fun onDeleteProductClick(product: Product) {}
    fun onQuantityIncrementClick(product: Product) {}
    fun onQuantityDecrementClick(product: Product) {}
}