package pl.mobi.msbw.producthunter.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.mobi.msbw.producthunter.firebase.FirebaseManager
import pl.mobi.msbw.producthunter.models.Product
import pl.mobi.msbw.producthunter.R
import pl.mobi.msbw.producthunter.adapters.ProductAdapter

class ProductListActivity : AppCompatActivity() {

    private lateinit var productRecyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        productRecyclerView = findViewById(R.id.productRecyclerView)
        productRecyclerView.layoutManager = LinearLayoutManager(this)

        val firebaseManager = FirebaseManager()

        // Pobierz produkty z bazy danych Firebase
        firebaseManager.searchProductByName("", object : (List<Product>) -> Unit {
            override fun invoke(products: List<Product>) {
                // Ustaw adapter dla RecyclerView
                productAdapter = ProductAdapter(products)
                productRecyclerView.adapter = productAdapter
            }
        })
    }
}
