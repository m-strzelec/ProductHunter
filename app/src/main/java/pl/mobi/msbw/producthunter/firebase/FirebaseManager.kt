package pl.mobi.msbw.producthunter.firebase

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import pl.mobi.msbw.producthunter.models.Product

class FirebaseManager {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun addProduct(product: Product) {
        val productId = database.child("products").push().key
        productId?.let {
            database.child("products").child(it).setValue(product)
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
                // Obsługa błędów
            }
        })
    }
}
