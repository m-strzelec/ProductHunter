package pl.mobi.msbw.producthunter.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.mobi.msbw.producthunter.R
import pl.mobi.msbw.producthunter.adapters.OnProductItemClickListener
import pl.mobi.msbw.producthunter.adapters.ProductAdapter
import pl.mobi.msbw.producthunter.models.Product
import pl.mobi.msbw.producthunter.viewmodels.ProductViewModel

class ItemListFragment : Fragment(R.layout.fragment_item_list), OnProductItemClickListener {

    private lateinit var productViewModel: ProductViewModel
    private lateinit var productAdapter: ProductAdapter

    private val chosenStoresIndexList = ArrayList<Int>()

    private lateinit var viewItemCost:TextView
    private lateinit var storesNamesList: Array<String>
    private lateinit var selectedStoresList: BooleanArray
    private lateinit var storeAutoCompleteTV: AutoCompleteTextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        storesNamesList = resources.getStringArray(R.array.shop_name_list)
        selectedStoresList = BooleanArray(storesNamesList.size)
        storeAutoCompleteTV = view.findViewById(R.id.storeAutoCompleteTV)

        val productRV = view.findViewById<RecyclerView>(R.id.productRecyclerView)
        productRV.layoutManager = LinearLayoutManager(requireContext())
        productAdapter = ProductAdapter(emptyList(),1, this)
        productRV.adapter = productAdapter

        productViewModel = ViewModelProvider(requireActivity())[ProductViewModel::class.java]
        productViewModel.products.observe(viewLifecycleOwner) { products ->
            productAdapter.updateItems(products)
            filterProducts()
        }

        viewItemCost = view.findViewById(R.id.viewItemCost)

        setAutoCompleteTextView(storeAutoCompleteTV, storesNamesList, selectedStoresList, chosenStoresIndexList)

        return view
    }

    override fun onDeleteProductClick(product: Product) {
        productViewModel.removeProduct(product)
        val a = getString(R.string.shoplist_delete_product)
        Toast.makeText(requireContext(), "$a ${product.name}", Toast.LENGTH_SHORT).show()
    }

    override fun onQuantityIncrementClick(product: Product) {
        productViewModel.incrementQuantity(product)
    }

    override fun onQuantityDecrementClick(product: Product) {
        if (product.quantity > 1) {
            productViewModel.decrementQuantity(product)
        }
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
                filterProducts()
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
                filterProducts()
            }
            builder.show()
        }
    }

    private fun filterProducts() {
        val filteredProducts = productViewModel.products.value.orEmpty().filter { product ->
            val matchesStore = isMatchingSelection(chosenStoresIndexList, storesNamesList, product.storeName)
            matchesStore
        }
        productAdapter.updateItems(filteredProducts)
        calculatePrice(filteredProducts)
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

    private fun calculatePrice(products: List<Product>) {
        var totalPrice = 0.0
        for (product in products) {
            totalPrice += product.price * product.quantity
        }
        viewItemCost.text = String.format("%.2f",totalPrice)
    }

}