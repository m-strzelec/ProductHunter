package pl.mobi.msbw.producthunter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import pl.mobi.msbw.producthunter.adapters.ProductAdapter
import pl.mobi.msbw.producthunter.firebase.FirebaseManager
import pl.mobi.msbw.producthunter.models.Product

class ItemListFragment : Fragment(R.layout.fragment_item_list) {

    private lateinit var productAdapter: ProductAdapter
    private lateinit var productsList: List<Product>
    private lateinit var storesNamesList: Array<String>
    private lateinit var selectedStoresList: BooleanArray
    private lateinit var categoriesNamesList: Array<String>
    private lateinit var selectedCategoriesList: BooleanArray

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FirebaseApp.initializeApp(requireContext())
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        storesNamesList = resources.getStringArray(R.array.lista_sklepow)
        selectedStoresList = BooleanArray(storesNamesList.size)

        categoriesNamesList = resources.getStringArray(R.array.lista_kategorii)
        selectedCategoriesList = BooleanArray(categoriesNamesList.size)

        val productRV = view.findViewById<RecyclerView>(R.id.productRecyclerView)
        productRV.layoutManager = LinearLayoutManager(requireContext())
        productAdapter = ProductAdapter(emptyList(),1)
        productRV.adapter = productAdapter

        val firebaseManager = FirebaseManager()

        firebaseManager.searchProductByName("", ::onProductsLoaded)

        return view
    }

    private fun onProductsLoaded(products: List<Product>) {
        productAdapter.updateItems(products)
        productsList = products
    }
}