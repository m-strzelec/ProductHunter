package pl.mobi.msbw.producthunter

import android.os.Bundle
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import pl.mobi.msbw.producthunter.adapters.ProductAdapter
import pl.mobi.msbw.producthunter.firebase.FirebaseManager
import pl.mobi.msbw.producthunter.models.Product

class MainActivity : AppCompatActivity() {

    private lateinit var productAdapter: ProductAdapter
    private lateinit var productsList: List<Product>
    private lateinit var stores: Array<String>
    private lateinit var selectedStores: BooleanArray
    private lateinit var categories: Array<String>
    private lateinit var selectedCategories: BooleanArray

    private val storesList = ArrayList<Int>()
    private val categoriesList = ArrayList<Int>()

    private lateinit var storeAutoCompleteTV: AutoCompleteTextView
    private lateinit var categoryAutoCompleteTV: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)

        stores = resources.getStringArray(R.array.lista_sklepow)
        selectedStores = BooleanArray(stores.size)

        categories = resources.getStringArray(R.array.lista_kategorii)
        selectedCategories = BooleanArray(categories.size)

        storeAutoCompleteTV = findViewById(R.id.storeAutoCompleteTV)
        categoryAutoCompleteTV = findViewById(R.id.categoryAutoCompleteTV)

        setupRecyclerView()

        val firebaseManager = FirebaseManager()

        firebaseManager.searchProductByName("", ::onProductsLoaded)

        setAutoCompleteTextView(storeAutoCompleteTV, stores, selectedStores, storesList)
        setAutoCompleteTextView(categoryAutoCompleteTV, categories, selectedCategories, categoriesList)
    }

    private fun setupRecyclerView() {
        val productRV = findViewById<RecyclerView>(R.id.productRecyclerView)
        productRV.layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter(emptyList())
        productRV.adapter = productAdapter
    }

    private fun setAutoCompleteTextView(
        autoCompleteTextView: AutoCompleteTextView,
        items: Array<String>,
        selectedItems: BooleanArray,
        selectedItemsList: ArrayList<Int>
    ) {
        autoCompleteTextView.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                selectedItemsList
            )
        )

        autoCompleteTextView.setOnClickListener {
            val initialSelectedItems = selectedItems.copyOf(selectedItems.size)
            val initialSelectedItemsList = ArrayList(selectedItemsList)
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Wybierz ${autoCompleteTextView.hint}")
            builder.setCancelable(false)
            builder.setMultiChoiceItems(
                items,
                selectedItems
            ) { _, i, b ->
                if (b) {
                    selectedItemsList.add(i)
                    selectedItemsList.sort()
                } else {
                    selectedItemsList.remove(i)
                }
            }
            builder.setPositiveButton("OK") { _, _ ->
                val stringBuilder = StringBuilder()
                for (j in 0 until selectedItemsList.size) {
                    stringBuilder.append(items[selectedItemsList[j]])
                    if (j != selectedItemsList.size - 1) {
                        stringBuilder.append(", ")
                    }
                }
                autoCompleteTextView.setText(stringBuilder.toString())
            }
            builder.setNegativeButton("Anuluj") { _, _ ->
                System.arraycopy(initialSelectedItems, 0, selectedItems, 0, initialSelectedItems.size)
                selectedItemsList.clear()
                selectedItemsList.addAll(initialSelectedItemsList)
            }
            builder.setNeutralButton("Wyczyść") { _, _ ->
                for (j in selectedItems.indices) {
                    selectedItems[j] = false
                }
                selectedItemsList.clear()
                autoCompleteTextView.text = null
            }
            builder.show()
        }
    }

    private fun onProductsLoaded(products: List<Product>) {
        productAdapter.updateItems(products)
        productsList = products
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchViewItem = menu.findItem(R.id.action_search)
        val searchView = searchViewItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                filterProducts(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterProducts(newText)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun filterProducts(query: String) {
        val filteredProducts = productsList.filter { it.name.contains(query, true) }
        if (filteredProducts.isNotEmpty()) {
            productAdapter.updateItems(filteredProducts)
        } else {
            Toast.makeText(this@MainActivity, "Nie znaleziono produktów", Toast.LENGTH_LONG).show()
        }
    }
}
