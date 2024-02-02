package pl.mobi.msbw.producthunter.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.mobi.msbw.producthunter.models.Product

class ProductViewModel : ViewModel() {
    private val _products = MutableLiveData<List<Product>>()
    private val _loadedProducts = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products
    val loadedProducts: LiveData<List<Product>> get() = _loadedProducts

    init {
        _products.value = mutableListOf()
    }

    fun setProducts(newProducts: List<Product>) {
        _products.value = newProducts
    }

    fun setLoadedProducts(products: List<Product>) {
        _loadedProducts.value = products
    }

    fun addProduct(product: Product): Boolean {
        val currentList = _products.value?.toMutableList() ?: mutableListOf()
        val existingProduct = currentList.find { it.id == product.id }
        return if (existingProduct == null) {
            currentList.add(product)
            _products.value = currentList
            true
        } else {
            false
        }
    }

    fun removeProduct(product: Product) {
        val currentList = _products.value?.toMutableList() ?: return
        currentList.remove(product)
        _products.value = currentList
    }

    fun incrementQuantity(product: Product, position: Int) {
        val currentList = _products.value?.toMutableList() ?: mutableListOf()
        val updatedProduct = product.copy(quantity = product.quantity + 1)
        currentList[position] = updatedProduct
        _products.value = currentList
    }
    fun decrementQuantity(product: Product, position: Int) {
        val currentList = _products.value?.toMutableList() ?: mutableListOf()
        val updatedProduct = product.copy(quantity = product.quantity - 1)
        currentList[position] = updatedProduct
        _products.value = currentList
    }
}