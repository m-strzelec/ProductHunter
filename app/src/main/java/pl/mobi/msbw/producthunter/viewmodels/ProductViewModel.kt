package pl.mobi.msbw.producthunter.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.mobi.msbw.producthunter.models.Product

class ProductViewModel : ViewModel() {
    private val _products = MutableLiveData<List<Product>>(mutableListOf())
    private val _loadedProducts = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products
    val loadedProducts: LiveData<List<Product>> get() = _loadedProducts

    fun setProducts(newProducts: List<Product>) {
        _products.value = newProducts
    }

    fun setLoadedProducts(newLoadedProducts: List<Product>) {
        _loadedProducts.value = newLoadedProducts
    }

    fun addProduct(product: Product): Boolean {
        val currentList = _products.value ?: mutableListOf()
        if (currentList.any { it.id == product.id }) return false

        val updatedList = currentList.toMutableList().apply { add(product) }
        _products.value = updatedList
        return true
    }

    fun removeProduct(product: Product) {
        _products.value = _products.value?.filter { it.id != product.id }
    }

    fun incrementQuantity(product: Product) {
        updateProductQuantity(product, 1)
    }

    fun decrementQuantity(product: Product) {
        updateProductQuantity(product, -1)
    }

    private fun updateProductQuantity(product: Product, increment: Int) {
        _products.value = _products.value?.map {
            if (it.id == product.id) it.copy(quantity = it.quantity + increment) else it
        }
    }
}