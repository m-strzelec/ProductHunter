package pl.mobi.msbw.producthunter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import pl.mobi.msbw.producthunter.ui.AddProductActivity

class UserFragment : Fragment(R.layout.fragment_user) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)

        val addProductBut: Button = view.findViewById(R.id.addProductButton)

        addProductBut.setOnClickListener {
            val productListIntent = Intent(requireContext(), AddProductActivity::class.java)
            startActivity(productListIntent)
        }

        return view
    }

}