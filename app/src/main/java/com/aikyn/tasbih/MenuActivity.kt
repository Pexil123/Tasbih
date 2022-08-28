package com.aikyn.tasbih

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.aikyn.calculator.MenuAdapter
import com.aikyn.tasbih.databinding.ActivityMenuBinding
import kotlin.properties.Delegates

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private var menuPref by Delegates.notNull<SharedPreferences>()
    private var pref by Delegates.notNull<SharedPreferences>()
    private var arrayTasbihs = ArrayList<ListItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater).also { setContentView(it.root) }
        binding.recyclerView.hasFixedSize()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        menuPref = getSharedPreferences("MENU", MODE_PRIVATE)
        pref = getSharedPreferences("TABLE", MODE_PRIVATE)

        if (pref.getBoolean("stateSwitchVibrator", false)) {
            binding.switchVibrator.isChecked = pref.getBoolean("stateSwitchVibrator", false)
        }

        binding.switchVibrator.setOnCheckedChangeListener { _, b ->
            saveStateSwitchVibrator(b)
        }

    }

    private fun saveStateSwitchVibrator(b: Boolean) {
        pref.edit().apply {
            putBoolean("stateSwitchVibrator", b)
            apply()
        }
    }

    override fun onResume() {
        super.onResume()

        updateState()

    }

    fun updateState() {
        arrayTasbihs.clear()

        for (i in 1..20) {
            if (menuPref.getString("tasbih$i", "") != "") {
                var string = menuPref.getString("tasbih$i", "")

                val separator = string!!.indexOf("/")

                val count = string.substring(0, separator)

                val description = string.substring(separator+1)

                val listItem = ListItem(count, description)

                arrayTasbihs.add(listItem)
            } else {
                val count = "0"
                val description = ""
                val listItem = ListItem(count, description)
                arrayTasbihs.add(listItem)
            }
        }

        binding.recyclerView.adapter = MenuAdapter(arrayTasbihs, this)
    }

}