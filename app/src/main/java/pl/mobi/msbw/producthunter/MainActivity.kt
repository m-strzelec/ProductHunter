package pl.mobi.msbw.producthunter

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import pl.mobi.msbw.producthunter.ui.AddProductActivity
import pl.mobi.msbw.producthunter.ui.ProductListActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicjalizacja Firebase
        FirebaseApp.initializeApp(this)

        val showProductsButton: Button = findViewById(R.id.showProductsButton)
        val addProductButton: Button = findViewById(R.id.addProductButton)

        // Dodaj obsługę kliknięcia przycisku
        showProductsButton.setOnClickListener {
            // Uruchom aktywność ProductListActivity po kliknięciu przycisku
            val productListIntent = Intent(this, ProductListActivity::class.java)
            startActivity(productListIntent)
        }

        addProductButton.setOnClickListener {
            val productListIntent = Intent(this, AddProductActivity::class.java)
            startActivity(productListIntent)
        }
    }
}