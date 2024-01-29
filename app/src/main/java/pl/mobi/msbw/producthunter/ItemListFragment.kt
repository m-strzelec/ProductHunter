package pl.mobi.msbw.producthunter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import pl.mobi.msbw.producthunter.adapters.OnProductItemClickListener
import pl.mobi.msbw.producthunter.adapters.ProductAdapter
import pl.mobi.msbw.producthunter.firebase.FirebaseManager
import pl.mobi.msbw.producthunter.models.Product
import pl.mobi.msbw.producthunter.viewmodel.ProductViewModel

class ItemListFragment : Fragment(R.layout.fragment_item_list), OnProductItemClickListener {

    private lateinit var productViewModel: ProductViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productsList: List<Product>
    private lateinit var storesNamesList: Array<String>
    private lateinit var selectedStoresList: BooleanArray

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        storesNamesList = resources.getStringArray(R.array.lista_sklepow)
        selectedStoresList = BooleanArray(storesNamesList.size)

        val productRV = view.findViewById<RecyclerView>(R.id.productRecyclerView)
        productRV.layoutManager = LinearLayoutManager(requireContext())
        productAdapter = ProductAdapter(emptyList(),1, this)
        productRV.adapter = productAdapter

        productViewModel = ViewModelProvider(requireActivity())[ProductViewModel::class.java]
        productViewModel.products.observe(viewLifecycleOwner) { products ->
            onShoppingListLoaded(products)
        }

        return view
    }

    override fun onDeleteProductClick(product: Product) {
        productViewModel.removeProduct(product)
        Toast.makeText(requireContext(), "Usunięto z listy zakupów: ${product.name}", Toast.LENGTH_SHORT).show()
    }

    private fun onShoppingListLoaded(shoppingList: List<Product>) {
        productAdapter.updateItems(shoppingList)
    }

}