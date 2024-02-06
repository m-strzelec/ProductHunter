package pl.mobi.msbw.producthunter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import pl.mobi.msbw.producthunter.ui.HomeFragment
import pl.mobi.msbw.producthunter.ui.ItemListFragment
import pl.mobi.msbw.producthunter.ui.BarcodeScannerFragment
import pl.mobi.msbw.producthunter.ui.UserFragment

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val firstFragment = HomeFragment()
        val secondFragment = ItemListFragment()
        val thirdFragment = BarcodeScannerFragment()
        val fourthFragment = UserFragment()
        bottomNavView = findViewById(R.id.bottomNavView)

        setCurrentFragment(firstFragment)

        bottomNavView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.menuHome->setCurrentFragment(firstFragment)
                R.id.menuItemList->setCurrentFragment(secondFragment)
                R.id.menuScanner->setCurrentFragment(thirdFragment)
                R.id.menuUser->setCurrentFragment(fourthFragment)
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, fragment)
            commit()
        }

}
