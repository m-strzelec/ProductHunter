package pl.mobi.msbw.producthunter.firebase

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import pl.mobi.msbw.producthunter.models.ShoppingList
import pl.mobi.msbw.producthunter.models.Product
import pl.mobi.msbw.producthunter.models.ShoppingListItem

class FirebaseManager {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun addProduct(
        category: String,
        productName: String,
        storeName: String,
        storeAddress: String,
        price: Double
    ) {
        val productId = database.child("products").push().key
        val newProduct = Product(productId.toString(), category, productName, price, 1, storeName, storeAddress)
        productId?.let {
            database.child("products").child(it).setValue(newProduct)
        }
    }

    fun searchProductByName(name: String, callback: (List<Product>) -> Unit) {
        val query = database.child("products").orderByChild("name").startAt(name).endAt(name + "\uf8ff")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products: MutableList<Product> = mutableListOf()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let {
                        products.add(it)
                    }
                }
                callback(products)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Error occurred while loading files from Firebase", error.toException())
            }
        })
    }

    fun getShoppingListsNames(callback: (List<String>) -> Unit) {
        database.child("shoppingLists").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listsNames = mutableListOf<String>()
                for (listSnapshot in snapshot.children) {
                    listsNames.add(listSnapshot.key.toString())
                }
                callback(listsNames)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Error occurred while loading shopping lists names from Firebase", error.toException())
            }
        })
    }

    fun getShoppingListProducts(name: String, callback: (Map<String, Int>) -> Unit) {
        val query = database.child("shoppingLists").child(name).child("products")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productsWithQuantity = mutableMapOf<String, Int>()
                for (productSnapshot in snapshot.children) {
                    val productId = productSnapshot.key
                    val quantity = (productSnapshot.value as? Long)?.toInt() ?: 0
                    productId?.let {
                        productsWithQuantity[it] = quantity
                    }
                }
                callback(productsWithQuantity)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Error occurred while loading products from list $name", error.toException())
            }
        })
    }

    fun getProductsByIds(productIds: List<String>, callback: (List<Product>) -> Unit) {
        val products = mutableListOf<Product>()
        for (productId in productIds) {
            val singleProductReference = database.child("products").child(productId)
            singleProductReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val product = snapshot.getValue(Product::class.java)
                    product?.let {
                        products.add(it)
                    }
                    if (products.size == productIds.size) {
                        callback(products)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Error occurred while reading product with ID $productId", error.toException())
                }
            })
        }
    }


    fun saveProductList(listName: String, products: List<ShoppingListItem>) {
        val shoppingList = ShoppingList(listName, products)
        val shoppingListMap: Map<String, Any> = mapOf(
            "listName" to shoppingList.listName,
            "products" to shoppingList.products.associateBy({ it.productId }, { it.quantity })
        )
        database.child("shoppingLists").child(listName).setValue(shoppingListMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Shopping list saved successfully")
                } else {
                    Log.e(TAG, "Error occurred while saving shopping list", task.exception)
                }
            }
    }

}