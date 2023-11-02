package pl.mobi.msbw.producthunter

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import pl.mobi.msbw.producthunter.adapters.ProductAdapter
import pl.mobi.msbw.producthunter.firebase.FirebaseManager
import pl.mobi.msbw.producthunter.models.Product
import pl.mobi.msbw.producthunter.ui.AddProductActivity

class MainActivity : AppCompatActivity() {

    lateinit var productAdapter: ProductAdapter
    lateinit var productsList: List<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicjalizacja Firebase
        FirebaseApp.initializeApp(this)

        val productRV = findViewById<RecyclerView>(R.id.productRecyclerView)
        val addProductBut: Button = findViewById(R.id.addProductButton)
        val firebaseManager = FirebaseManager()

        productRV.layoutManager = LinearLayoutManager(this)

        firebaseManager.searchProductByName("", object : (List<Product>) -> Unit {
            override fun invoke(products: List<Product>) {
                productAdapter = ProductAdapter(products)
                productRV.adapter = productAdapter
                productsList = products
            }
        })

        addProductBut.setOnClickListener {
            val productListIntent = Intent(this, AddProductActivity::class.java)
            startActivity(productListIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)

        val searchViewItem = menu.findItem(R.id.action_search)
        val searchView = searchViewItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val filteredProducts = productsList.filter { it.name.contains(query, true) }
                if (filteredProducts.isNotEmpty()) {
                    productAdapter.updateItems(filteredProducts)
                } else {
                    Toast.makeText(this@MainActivity, "Nie znaleziono produktów",
                        Toast.LENGTH_LONG).show()
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val filteredProducts = productsList.filter { it.name.contains(newText, true) }
                productAdapter.updateItems(filteredProducts)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

}