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

    fun addProduct(product: Product) {
        val currentList = _products.value?.toMutableList() ?: mutableListOf()
        currentList.add(product)
        _products.value = currentList
    }

    fun removeProduct(product: Product) {
        val currentList = _products.value?.toMutableList() ?: return
        currentList.remove(product)
        _products.value = currentList
    }

}