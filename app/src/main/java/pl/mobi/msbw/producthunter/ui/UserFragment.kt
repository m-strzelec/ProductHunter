package pl.mobi.msbw.producthunter.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import pl.mobi.msbw.producthunter.R
import pl.mobi.msbw.producthunter.firebase.FirebaseManager
import pl.mobi.msbw.producthunter.models.Product
import pl.mobi.msbw.producthunter.models.ShoppingListItem
import pl.mobi.msbw.producthunter.viewmodels.ProductViewModel

class UserFragment : Fragment(R.layout.fragment_user) {

    private lateinit var productViewModel: ProductViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addProductBut: Button = view.findViewById(R.id.addProductButton)
        val saveListBut: Button = view.findViewById(R.id.saveCurrentItemList)
        val loadListBut: Button = view.findViewById(R.id.savedItemLists)

        productViewModel = ViewModelProvider(requireActivity())[ProductViewModel::class.java]
        addProductBut.setOnClickListener {
            val productListIntent = Intent(requireContext(), AddProductActivity::class.java)
            startActivity(productListIntent)
        }

        loadListBut.setOnClickListener {
            showLoadListDialog()
        }

        saveListBut.setOnClickListener {
            showSaveListDialog()
        }
    }

    private fun showSaveListDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Nazwa Listy")

        val input = EditText(requireContext())
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        input.layoutParams = lp

        dialogBuilder.setView(input)
        dialogBuilder.setCancelable(false)
        dialogBuilder.setPositiveButton("Zapisz") { _, _ ->
            val listName = input.text.toString()
            val productList = productViewModel.products.value ?: emptyList()
            if (listName.isNotEmpty() && productList.isNotEmpty()) {
                val shoppingListItems = productList.map { product ->
                    ShoppingListItem(product.id, product.quantity) // Przykładowa ilość, dostosuj do swoich potrzeb
                }
                saveProductListToFirebase(listName, shoppingListItems)
            } else {
                Toast.makeText(requireContext(), "Lista jest pusta lub nie podano jej nazwy", Toast.LENGTH_SHORT).show()
            }
        }
        dialogBuilder.setNegativeButton("Anuluj", null)

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun showLoadListDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Wybierz listę do wczytania")

        val shoppingLists = mutableListOf<String>()
        val firebaseManager = FirebaseManager()

        firebaseManager.getShoppingListsNames { lists ->
            shoppingLists.addAll(lists)

            if (shoppingLists.isNotEmpty()) {
                val inflater = requireActivity().layoutInflater
                val dialogLayout = inflater.inflate(R.layout.dialog_load_list, null)

                val spinner: Spinner = dialogLayout.findViewById(R.id.spinner)
                val adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, shoppingLists)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter

                builder.setView(dialogLayout)
                builder.setCancelable(false)
                builder.setPositiveButton("Wczytaj") { _, _ ->
                    val selectedListName = spinner.selectedItem as String
                    firebaseManager.getShoppingListProducts(selectedListName) { productsWithQuantity ->
                        val homeProducts = productViewModel.loadedProducts.value.orEmpty()
                        val foundProducts: List<Product> = homeProducts.filter { product ->
                            product.id in productsWithQuantity.keys
                        }
                        val updatedProducts = foundProducts.map { product ->
                            val quantity = productsWithQuantity[product.id] ?: 0
                            Product(
                                product.id,
                                product.category,
                                product.name,
                                product.price,
                                quantity,
                                product.storeName,
                                product.storeAddress
                            )
                        }
                        productViewModel.setProducts(updatedProducts)
                    }
                    Toast.makeText(requireContext(), "Lista produktów została wczytana", Toast.LENGTH_SHORT).show()
                }
            } else {
                builder.setMessage("Brak dostępnych list zakupów")
            }
            builder.setNegativeButton("Anuluj", null)
            builder.show()
        }
    }


    private fun saveProductListToFirebase(listName: String, itemList: List<ShoppingListItem>) {
        val firebaseManager = FirebaseManager()
        firebaseManager.saveProductList(listName, itemList)
        Toast.makeText(requireContext(), "Lista produktów została zapisana", Toast.LENGTH_SHORT).show()
    }
}