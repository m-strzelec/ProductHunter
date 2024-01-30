package pl.mobi.msbw.producthunter

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import pl.mobi.msbw.producthunter.firebase.FirebaseManager
import pl.mobi.msbw.producthunter.ui.AddProductActivity
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
                val productIds = productList.map { it.id }
                saveProductListToFirebase(listName, productIds)
            } else {
                Toast.makeText(requireContext(), "Lista jest pusta lub nie podano jej nazwy", Toast.LENGTH_SHORT).show()
            }
        }
        dialogBuilder.setNegativeButton("Anuluj", null)

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun showLoadListDialog() {
//        val dialogBuilder = AlertDialog.Builder(requireContext())
//        dialogBuilder.setTitle("Nazwa Listy")
//        ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_spinner_dropdown_item
//            selectedItemsList
//        )
    }

    private fun saveProductListToFirebase(listName: String, productIds: List<String>) {
        val firebaseManager = FirebaseManager()
        firebaseManager.saveProductList(listName, productIds)
        Toast.makeText(requireContext(), "Lista produktów została zapisana", Toast.LENGTH_SHORT).show()
    }
}