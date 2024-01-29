package pl.mobi.msbw.producthunter.firebase

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import pl.mobi.msbw.producthunter.models.FirebaseShoppingList
import pl.mobi.msbw.producthunter.models.Product

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
        val newProduct = Product(productId.toString(), category, productName, price, storeName, storeAddress)
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
                Log.w(TAG, "Failed to read data.", error.toException())
            }
        })
    }

    fun saveProductList(listName: String, productIds: List<String>) {
        val firebaseShoppingList = FirebaseShoppingList(listName, productIds)
        database.child("shoppingLists").child(listName).setValue(firebaseShoppingList)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseManager", "Lista produktów została zapisana pomyślnie")
                } else {
                    Log.e("FirebaseManager", "Błąd podczas zapisywania listy produktów", task.exception)
                }
            }
    }
}
