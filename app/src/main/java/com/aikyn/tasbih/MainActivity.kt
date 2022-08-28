package com.aikyn.tasbih

import android.animation.ValueAnimator
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import com.aikyn.tasbih.databinding.ActivityMainBinding
import com.aikyn.tasbih.databinding.AlertDialogBinding
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var menuPref by Delegates.notNull<SharedPreferences>()
    private var pref by Delegates.notNull<SharedPreferences>()
    private var count = 0
    private var labelText = ""
    private var position = 0
    private var stateSwitchVibrator = false
    private var bannerIncreased = false
    private var ZERO_DP by Delegates.notNull<Int>()
    private val INCREASE_VALUE = 120
    private val MAX_NUMBER = 999999

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        menuPref = getSharedPreferences("MENU", MODE_PRIVATE)
        pref = getSharedPreferences("TABLE", MODE_PRIVATE)

        stateSwitchVibrator = pref.getBoolean("stateSwitchVibrator", false)

        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        count = intent.getStringExtra("count")!!.toInt()
        labelText = intent.getStringExtra("description")!!
        position = intent.getIntExtra("position", 0)

        binding.label.isEnabled = false

        ZERO_DP = resources.displayMetrics.run { heightPixels / density }.toInt()

        initializeCount()
        initializeLabelText()

        binding.buttonClear.setOnClickListener {
            count = 0
            initializeCount()
            saveState()
        }

        binding.buttonEdit.setOnClickListener {
            createAlertDialog()
        }

        binding.buttonMinus.setOnClickListener {
            if (count!=0) {
                count--
                initializeCount()
                saveState()
            }
        }

        binding.labelClickListener.setOnClickListener {
            if (!bannerIncreased) {
                labelStateChange()
                binding.banner.changeHeightSize(ZERO_DP)
                updateViews()
                bannerIncreased = true
            }
        }

        binding.labelClickListener.setOnLongClickListener {
            true
        }

        binding.buttonExit.setOnClickListener {
            if (bannerIncreased) {
                labelStateChange()
                binding.banner.changeHeightSize(INCREASE_VALUE)
                updateViews()
                bannerIncreased = false
            }
        }

        binding.label.doOnTextChanged { text, start, before, coun ->
            labelText = binding.label.text.toString()
            saveState()
        }

        binding.buttonCount.setOnLongClickListener {
            true
        }

        binding.buttonCount.setOnClickListener {
            if (this.currentFocus == null) {
                if (count != MAX_NUMBER) {
                    count++
                    initializeCount()
                    saveState()
                }
            } else {
                hideKeyBoard()
            }

            if (stateSwitchVibrator) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE)) // New vibrate method for API Level 26 or higher
                } else {
                    vibrator.vibrate(40)
                }
            }

        }

        binding.count.doOnTextChanged { text, start, before, coun ->

            if (!binding.buttonCount.isPressed){
                if (binding.count.text.toString().isNotEmpty()) {
                    count = binding.count.text.toString().toInt()
                    saveState()
                } else {
                    initializeCount()
                    binding.count.setSelection(1)
                    saveState()
                }
            }

        }

        binding.buttonToList.setOnClickListener {
            finish()
        }

    }

    fun hideKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }

    private fun labelStateChange() {
        binding.label.isEnabled = !binding.label.isEnabled
    }

    private fun updateViews() {
        binding.buttonClear.changeVisibility()
        binding.buttonMinus.changeVisibility()
        binding.buttonExit.changeVisibility()
        binding.buttonToList.changeVisibility()
        binding.labelClickListener.changeVisibility()
    }

    private fun View.changeVisibility() {
        if (this.visibility == View.VISIBLE) this.visibility = View.GONE
            else if (this.visibility == View.GONE) this.visibility = View.VISIBLE
    }

    private fun View.changeHeightSize(newHeight: Int) {
        val scale: Float = resources.displayMetrics.density
        val valueAnimator = ValueAnimator.ofInt(this.height, (newHeight * scale + 0.5f).toInt())
        valueAnimator.duration = 250L
        valueAnimator.addUpdateListener {
            val animatedValue = valueAnimator.animatedValue as Int
            val layoutParams = this.layoutParams
            layoutParams.height = animatedValue
            this.layoutParams = layoutParams
        }
        valueAnimator.start()
    }

    private fun createAlertDialog() {
        val builder = android.app.AlertDialog.Builder(this)

        val dialogBinding = AlertDialogBinding.inflate(layoutInflater)

        val input = dialogBinding.editText

        builder.setView(dialogBinding.root)
        dialogBinding.editText.requestFocus()
        dialogBinding.editText.text = binding.label.text

        builder.setPositiveButton("OK") { dialog, which ->
            if (input.text.isNotEmpty()) {
                binding.label.setText(input.text.toString())
                labelText = input.text.toString()
                saveState()
            }
        }

        builder.setNegativeButton("Қайту") { dialog, which ->
            dialog.cancel()
        }
        builder.show()
    }

    fun initializeCount() {
        binding.count.setText(count.toString())
    }

    fun initializeLabelText() {
        binding.label.setText(labelText)
    }

    fun saveState() {
        menuPref.edit().apply {
            putString("tasbih$position", "$count/$labelText")
            apply()
        }
    }

    override fun onBackPressed() {
        if (bannerIncreased) {
            labelStateChange()
            binding.banner.changeHeightSize(INCREASE_VALUE)
            updateViews()
            bannerIncreased = false
        } else {
            super.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        saveState()
    }

}