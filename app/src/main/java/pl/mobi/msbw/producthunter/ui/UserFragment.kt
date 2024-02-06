package pl.mobi.msbw.producthunter.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import pl.mobi.msbw.producthunter.R
import pl.mobi.msbw.producthunter.firebase.FirebaseManager
import pl.mobi.msbw.producthunter.models.ShoppingListItem
import pl.mobi.msbw.producthunter.viewmodels.ProductViewModel

class UserFragment : Fragment(R.layout.fragment_user) {

    private lateinit var productViewModel: ProductViewModel
    private val firebaseManager = FirebaseManager()

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
        val dialogView = layoutInflater.inflate(R.layout.dialog_save_list, null)
        val input: EditText = dialogView.findViewById(R.id.listNameInput)

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.shoplist_name))
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val listName = input.text.toString()
                saveCurrentProductList(listName)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .setIcon(android.R.drawable.ic_dialog_info)
            .show()
    }

    private fun saveCurrentProductList(listName: String) {
        if (listName.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.shoplist_filter_name_err), Toast.LENGTH_SHORT).show()
            return
        }
        val productList = productViewModel.products.value ?: emptyList()
        if (productList.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.shoplist_filter_name_err), Toast.LENGTH_SHORT).show()
            return
        }
        val shoppingListItems = productList.map { ShoppingListItem(it.id, it.quantity) }
        firebaseManager.saveProductList(listName, shoppingListItems)
        Toast.makeText(requireContext(), getString(R.string.shoplist_save), Toast.LENGTH_SHORT).show()
    }

    private fun showLoadListDialog() {
        firebaseManager.getShoppingListsNames { lists ->
            if (lists.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.shoplist_none), Toast.LENGTH_SHORT).show()
            }
            else {
                val dialogView = layoutInflater.inflate(R.layout.dialog_load_list, null)
                val spinner: Spinner = dialogView.findViewById(R.id.spinner)
                setupSpinner(spinner, lists)

                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.shoplist_choose))
                    .setView(dialogView)
                    .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                        loadSelectedProductList(spinner.selectedItem.toString())
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show()
            }
        }
    }

    private fun setupSpinner(spinner: Spinner, shoppingLists: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, shoppingLists).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.adapter = adapter
    }

    private fun loadSelectedProductList(listName: String) {
        firebaseManager.getShoppingListProducts(listName) { productsWithQuantity ->
            val updatedProducts = productViewModel.loadedProducts.value.orEmpty().mapNotNull { product ->
                productsWithQuantity[product.id]?.let { quantity ->
                    product.copy(quantity = quantity)
                }
            }
            productViewModel.setProducts(updatedProducts)
            Toast.makeText(requireContext(), getString(R.string.shoplist_loaded), Toast.LENGTH_SHORT).show()
        }
    }

}