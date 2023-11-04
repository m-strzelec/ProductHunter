package pl.mobi.msbw.producthunter.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.mobi.msbw.producthunter.R
import pl.mobi.msbw.producthunter.models.Product

class ProductAdapter(private var products: List<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productCategoryTextView: TextView = itemView.findViewById(R.id.productCategoryTV)
        val productNameTextView: TextView = itemView.findViewById(R.id.productNameTV)
        val productPriceTextView: TextView = itemView.findViewById(R.id.productPriceTV)
        val storeNameTextView: TextView = itemView.findViewById(R.id.storeNameTV)
        val storeAddressTextView: TextView = itemView.findViewById(R.id.storeAddressTV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_product_list, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentProduct = products[position]
        holder.productCategoryTextView.text = currentProduct.category
        holder.productNameTextView.text = currentProduct.name
        holder.productPriceTextView.text =  String.format("%.2f", currentProduct.price)
        holder.storeNameTextView.text = currentProduct.storeName
        holder.storeAddressTextView.text = currentProduct.storeAddress
    }

    fun updateItems(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun getItems(): List<Product> {
        return products
    }
}
