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

    private lateinit var productNameEditText: EditText
    private lateinit var storeNameEditText: EditText
    private lateinit var storeAddressEditText: EditText
    private lateinit var productPriceEditText: EditText
    private lateinit var addProductButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        productNameEditText = findViewById(R.id.productNameEditText)
        storeNameEditText = findViewById(R.id.storeNameEditText)
        storeAddressEditText = findViewById(R.id.storeAddressEditText)
        productPriceEditText = findViewById(R.id.productPriceEditText)
        addProductButton = findViewById(R.id.addProductButton)

        addProductButton.setOnClickListener {
            val productName = productNameEditText.text.toString()
            val storeName = storeNameEditText.text.toString()
            val storeAddress = storeAddressEditText.text.toString()
            val price = productPriceEditText.text.toString().toDoubleOrNull()

            if (productName.isNotEmpty() && storeName.isNotEmpty() && storeAddress.isNotEmpty() && price != null) {
                val newProduct = Product(name = productName, storeName = storeName, storeAddress = storeAddress, price = price)
                val firebaseManager = FirebaseManager()
                firebaseManager.addProduct(newProduct)
                finish() // Zamyka aktywność po dodaniu produktu
            } else {
                // Obsługa błędnych danych
                Toast.makeText(this, "Proszę wypełnić wszystkie pola prawidłowo.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
