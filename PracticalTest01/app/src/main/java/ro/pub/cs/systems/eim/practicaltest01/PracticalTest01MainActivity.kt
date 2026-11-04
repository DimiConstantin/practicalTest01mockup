package ro.pub.cs.systems.eim.practicaltest01

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class PracticalTest01MainActivity : AppCompatActivity() {

    private lateinit var textLeft: EditText
    private lateinit var textRight: EditText
    private lateinit var buttonLeft: Button
    private lateinit var buttonRight: Button

    private lateinit var navigateButton: Button

    companion object {
        const val THRESHOLD = 10
    }

    private var serviceStarted = false

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            val date = intent.getStringExtra("date")
            val mean = intent.getDoubleExtra("mean", 0.0)
            val geometricMean = intent.getDoubleExtra("geometricMean", 0.0)

            Log.d("PracticalTest01MainActivity", "$date | Media: $mean | Geometrica: $geometricMean")
        }
    }

    private val startActivityForResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Toast.makeText(this, "The activity returned with result ${result.resultCode}", Toast.LENGTH_LONG).show()
    }

    inner class ButtonListener(private val editText: EditText) : View.OnClickListener {
        override fun onClick(view: View) {
            Log.d("PracticalTest01MainActivity", "Button clicked")
            val currentValue = editText.text.toString().toIntOrNull() ?: 0
            editText.setText((currentValue + 1).toString())
            startServiceIfNeeded()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_practical_test01_main)

        val filter = IntentFilter()

        filter.addAction("ro.pub.cs.systems.eim.practicaltest01.ACTION1")
        filter.addAction("ro.pub.cs.systems.eim.practicaltest01.ACTION2")
        filter.addAction("ro.pub.cs.systems.eim.practicaltest01.ACTION3")

        ContextCompat.registerReceiver(
            this,
            receiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        textLeft = findViewById(R.id.textLeft)
        textRight = findViewById(R.id.textRight)
        buttonLeft = findViewById(R.id.buttonLeft)
        buttonRight = findViewById(R.id.buttonRight)

        buttonLeft.setOnClickListener(ButtonListener(textLeft))
        buttonRight.setOnClickListener(ButtonListener(textRight))
        navigateButton = findViewById(R.id.navigateButton)
        navigateButton.setOnClickListener {
            val intent = Intent(this, PracticalTest01SecondaryActivity::class.java)
            val total = (textLeft.text.toString().toIntOrNull() ?: 0) + (textRight.text.toString().toIntOrNull() ?: 0)
            intent.putExtra("total", total)
            startActivityForResultLauncher.launch(intent)
        }


        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("textLeft")) {
                textLeft.setText(savedInstanceState.getString("textLeft"))
            }
            if (savedInstanceState.containsKey("textRight")) {
                textRight.setText(savedInstanceState.getString("textRight"))
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("textLeft", textLeft.text.toString())
        outState.putString("textRight", textRight.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState.containsKey("textLeft")) {
            textLeft.setText(savedInstanceState.getString("textLeft"))
        }
        if (savedInstanceState.containsKey("textRight")) {
            textRight.setText(savedInstanceState.getString("textRight"))
        }
    }

    override fun onDestroy() {
        if (serviceStarted) {
            val intent = Intent(this, PracticalTest01Service::class.java)
            stopService(intent)
        }
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private fun startServiceIfNeeded() {
        val left = textLeft.text.toString().toInt()
        val right = textRight.text.toString().toInt()

        if (left + right > THRESHOLD && !serviceStarted) {

            val intent = Intent(this, PracticalTest01Service::class.java)

            intent.putExtra("left", left)
            intent.putExtra("right", right)

            startService(intent)

            Log.d("PracticalTest01MainActivity", "Starting service")

            serviceStarted = true
        }
    }
}