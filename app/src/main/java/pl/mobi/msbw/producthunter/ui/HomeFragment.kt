package pl.mobi.msbw.producthunter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.FirebaseApp
import pl.mobi.msbw.producthunter.R
import pl.mobi.msbw.producthunter.adapters.OnProductItemClickListener
import pl.mobi.msbw.producthunter.adapters.ProductAdapter
import pl.mobi.msbw.producthunter.firebase.FirebaseManager
import pl.mobi.msbw.producthunter.models.Product
import pl.mobi.msbw.producthunter.viewmodels.ProductViewModel

class HomeFragment : Fragment(R.layout.fragment_home), OnProductItemClickListener {

    private lateinit var productAdapter: ProductAdapter
    private lateinit var storesNamesList: Array<String>
    private lateinit var selectedStoresList: BooleanArray
    private lateinit var categoriesNamesList: Array<String>
    private lateinit var selectedCategoriesList: BooleanArray

    private val chosenStoresIndexList = ArrayList<Int>()
    private val chosenCategoriesIndexList = ArrayList<Int>()

    private var productsLoaded = false
    private var productsList: List<Product> = emptyList()

    private lateinit var storeAutoCompleteTV: AutoCompleteTextView
    private lateinit var categoryAutoCompleteTV: AutoCompleteTextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var productViewModel: ProductViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FirebaseApp.initializeApp(requireContext())
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        storesNamesList = resources.getStringArray(R.array.shop_name_list)
        selectedStoresList = BooleanArray(storesNamesList.size)

        categoriesNamesList = resources.getStringArray(R.array.category_name_list)
        selectedCategoriesList = BooleanArray(categoriesNamesList.size)

        storeAutoCompleteTV = view.findViewById(R.id.storeAutoCompleteTV)
        categoryAutoCompleteTV = view.findViewById(R.id.categoryAutoCompleteTV)

        val productRV = view.findViewById<RecyclerView>(R.id.productRecyclerView)
        productRV.layoutManager = LinearLayoutManager(requireContext())
        productAdapter = ProductAdapter(0, this)
        productRV.adapter = productAdapter

        setAutoCompleteTextView(storeAutoCompleteTV, storesNamesList, selectedStoresList, chosenStoresIndexList)
        setAutoCompleteTextView(categoryAutoCompleteTV, categoriesNamesList, selectedCategoriesList, chosenCategoriesIndexList)

        productViewModel = ViewModelProvider(requireActivity())[ProductViewModel::class.java]

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val firebaseManager = FirebaseManager()
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        if (!productsLoaded) {
            firebaseManager.searchProductByName("", ::onFirebaseLoaded)
            productsLoaded = true
        }
        else {
            onProductsLoaded(productsList)
        }

        swipeRefreshLayout.setOnRefreshListener {
            firebaseManager.searchProductByName("", ::onFirebaseLoaded)
            swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()
        chosenStoresIndexList.clear()
        chosenCategoriesIndexList.clear()
        storeAutoCompleteTV.text = null
        categoryAutoCompleteTV.text = null
    }


    override fun onAddToProductListClick(product: Product) {
        if (productViewModel.addProduct(product)) {
            val a = getString(R.string.shoplist_product_add)
            Toast.makeText(
                requireContext(),
                "$a ${product.name}",
                Toast.LENGTH_SHORT
            ).show()
        }
        else {
            val a = getString(R.string.shoplist_product_add_err)
            Toast.makeText(
                requireContext(),
                "${product.name} $a",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun onFirebaseLoaded(products: List<Product>) {
        onProductsLoaded(products)
        productViewModel.setLoadedProducts(products)
        productsList = products
    }
    private fun onProductsLoaded(products: List<Product>) {
        productAdapter.submitList(products)
    }

    private fun setAutoCompleteTextView(
        autoCompleteTextView: AutoCompleteTextView,
        items: Array<String>,
        selectedItems: BooleanArray,
        selectedItemsList: ArrayList<Int>
    ) {
        autoCompleteTextView.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                selectedItemsList
            )
        )

        autoCompleteTextView.setOnClickListener {
            val initialSelectedItems = selectedItems.copyOf(selectedItems.size)
            val initialSelectedItemsList = ArrayList(selectedItemsList)
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("${autoCompleteTextView.contentDescription}")
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
            builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
                val stringBuilder = StringBuilder()
                for (j in 0 until selectedItemsList.size) {
                    stringBuilder.append(items[selectedItemsList[j]])
                    if (j != selectedItemsList.size - 1) {
                        stringBuilder.append(", ")
                    }
                }
                autoCompleteTextView.setText(stringBuilder.toString())
                filterProducts("")
            }
            builder.setNegativeButton(getString(R.string.cancel)) { _, _ ->
                System.arraycopy(initialSelectedItems, 0, selectedItems, 0, initialSelectedItems.size)
                selectedItemsList.clear()
                selectedItemsList.addAll(initialSelectedItemsList)
            }
            builder.setNeutralButton(getString(R.string.clear)) { _, _ ->
                for (j in selectedItems.indices) {
                    selectedItems[j] = false
                }
                selectedItemsList.clear()
                autoCompleteTextView.text = null
                filterProducts("")
            }
            builder.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_view, menu)

        val searchViewItem = menu.findItem(R.id.action_search)
        val searchView = searchViewItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterProducts(query.orEmpty())
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterProducts(newText.orEmpty())
                return false
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun filterProducts(query: String) {
        val filteredProducts = productsList.filter { product ->
            val matchesCategory = isMatchingSelection(chosenCategoriesIndexList, categoriesNamesList, product.category)
            val matchesStore = isMatchingSelection(chosenStoresIndexList, storesNamesList, product.storeName)
            val matchesName = product.name.contains(query, true)

            matchesCategory && matchesStore && matchesName
        }

        productAdapter.submitList(filteredProducts)
        if (filteredProducts.isEmpty()) {
            val a = getString(R.string.products_not_found_err)
            Toast.makeText(requireContext(), a, Toast.LENGTH_LONG).show()
        }
    }

    private fun isMatchingSelection(
        selectedIndices: ArrayList<Int>,
        availableNames: Array<String>,
        selectedName: String
    ): Boolean {
        if (selectedIndices.isNotEmpty()) {
            val selectedItems = selectedIndices.map { index -> availableNames[index] }
            return selectedItems.contains(selectedName)
        }
        return true
    }
}
