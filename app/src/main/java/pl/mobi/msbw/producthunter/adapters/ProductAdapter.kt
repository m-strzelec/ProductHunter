package pl.mobi.msbw.producthunter.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.mobi.msbw.producthunter.R
import pl.mobi.msbw.producthunter.models.Product

class ProductAdapter(private val products: List<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName)
        val storeName: TextView = itemView.findViewById(R.id.storeName)
        val storeAddress: TextView = itemView.findViewById(R.id.storeAddress)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentProduct = products[position]
        holder.productName.text = currentProduct.name
        holder.storeName.text = currentProduct.storeName
        holder.storeAddress.text = currentProduct.storeAddress
        holder.productPrice.text = currentProduct.price.toString()
    }

    override fun getItemCount(): Int {
        return products.size
    }
}
