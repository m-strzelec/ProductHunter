package pl.mobi.msbw.producthunter

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import pl.mobi.msbw.producthunter.adapters.ProductAdapter
import pl.mobi.msbw.producthunter.firebase.FirebaseManager
import pl.mobi.msbw.producthunter.models.Product

class MainActivity : AppCompatActivity() {

    lateinit var productAdapter: ProductAdapter
    lateinit var productsList: List<Product>
    //val stores = resources.getStringArray(R.array.lista_sklepow)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        val productRV = findViewById<RecyclerView>(R.id.productRecyclerView)
        val firebaseManager = FirebaseManager()

        productRV.layoutManager = LinearLayoutManager(this)

        firebaseManager.searchProductByName("", object : (List<Product>) -> Unit {
            override fun invoke(products: List<Product>) {
                productAdapter = ProductAdapter(products)
                productRV.adapter = productAdapter
                productsList = products
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

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