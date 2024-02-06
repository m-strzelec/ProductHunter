package pl.mobi.msbw.producthunter.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.mobi.msbw.producthunter.R
import pl.mobi.msbw.producthunter.models.Product

class ProductAdapter(
    private var chosenCardType: Int,
    private val onProductItemClickListener: OnProductItemClickListener? = null
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean = oldItem == newItem
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productCategoryTextView: TextView = itemView.findViewById(R.id.productCategoryTV)
        private val productNameTextView: TextView = itemView.findViewById(R.id.productNameTV)
        private val productPriceTextView: TextView = itemView.findViewById(R.id.productPriceTV)
        private val storeNameTextView: TextView = itemView.findViewById(R.id.storeNameTV)
        private val storeAddressTextView: TextView = itemView.findViewById(R.id.storeAddressTV)
        private val productQuantityTextView: TextView? = itemView.findViewById(R.id.productQuantityTV)
        private val buttonAddToCart: Button? = itemView.findViewById(R.id.buttonAddToCart)
        private val deleteButton: ImageButton? = itemView.findViewById(R.id.deleteButton)
        private val quantityPlus: ImageButton? = itemView.findViewById(R.id.quantityPlus)
        private val quantityMinus: ImageButton? = itemView.findViewById(R.id.quantityMinus)

        fun bind(product: Product) {
            productCategoryTextView.text = product.category
            productNameTextView.text = product.name
            productPriceTextView.text = String.format("%.2f", product.price)
            storeNameTextView.text = product.storeName
            storeAddressTextView.text = product.storeAddress
            productQuantityTextView?.text = product.quantity.toString()
            setListeners(product)
        }

        private fun setListeners(product: Product) {
            buttonAddToCart?.setOnClickListener { onProductItemClickListener?.onAddToProductListClick(product) }
            deleteButton?.setOnClickListener { onProductItemClickListener?.onDeleteProductClick(product) }
            quantityPlus?.setOnClickListener { onProductItemClickListener?.onQuantityIncrementClick(product) }
            quantityMinus?.setOnClickListener { onProductItemClickListener?.onQuantityDecrementClick(product) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val layoutId = if (chosenCardType == 0) R.layout.item_list_home else R.layout.item_list_shopping
        val itemView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentProduct = getItem(position)
        holder.bind(currentProduct)
    }
}
