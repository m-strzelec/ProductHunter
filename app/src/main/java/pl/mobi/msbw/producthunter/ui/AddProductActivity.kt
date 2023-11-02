package pl.mobi.msbw.producthunter.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.mobi.msbw.producthunter.R
import pl.mobi.msbw.producthunter.firebase.FirebaseManager
import pl.mobi.msbw.producthunter.models.Product

class AddProductActivity : AppCompatActivity() {

    private lateinit var productCategoryEditText: EditText
    private lateinit var productNameEditText: EditText
    private lateinit var storeNameEditText: EditText
    private lateinit var storeAddressEditText: EditText
    private lateinit var productPriceEditText: EditText
    private lateinit var addProductButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        productCategoryEditText = findViewById(R.id.productCategoryET)
        productNameEditText = findViewById(R.id.productNameET)
        storeNameEditText = findViewById(R.id.storeNameET)
        storeAddressEditText = findViewById(R.id.storeAddressET)
        productPriceEditText = findViewById(R.id.productPriceET)
        addProductButton = findViewById(R.id.addProductBut)

        addProductButton.setOnClickListener {
            val category = productCategoryEditText.text.toString()
            val productName = productNameEditText.text.toString()
            val storeName = storeNameEditText.text.toString()
            val storeAddress = storeAddressEditText.text.toString()
            val price = productPriceEditText.text.toString().toDoubleOrNull()

            if (category.isNotEmpty() && productName.isNotEmpty() && storeName.isNotEmpty() &&
                storeAddress.isNotEmpty() && price != null) {
                val newProduct = Product(category = category, name = productName,
                    storeName = storeName, storeAddress = storeAddress, price = price)
                val firebaseManager = FirebaseManager()
                firebaseManager.addProduct(newProduct)
                finish() // Zamyka aktywność po dodaniu produktu
            } else {
                // Obsługa błędnych danych
                Toast.makeText(this, "Proszę wypełnić wszystkie pola prawidłowo.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}
