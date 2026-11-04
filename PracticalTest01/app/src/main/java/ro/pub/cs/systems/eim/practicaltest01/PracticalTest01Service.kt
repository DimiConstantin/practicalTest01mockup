package ro.pub.cs.systems.eim.practicaltest01

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.lang.Thread.sleep
import java.util.Date
import kotlin.math.sqrt

class PracticalTest01Service : Service() {

    private var thread: Thread? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        Log.d("PracticalTest01Service", "onStartCommand() method was invoked")
        val number1 = intent.getIntExtra("left", 0)
        val number2 = intent.getIntExtra("right", 0)
        val mean = (number1 + number2) / 2.0
        val geometricMean = sqrt(number1.toDouble() * number2)

        val actions = listOf(
            "ro.pub.cs.systems.eim.practicaltest01.ACTION1",
            "ro.pub.cs.systems.eim.practicaltest01.ACTION2",
            "ro.pub.cs.systems.eim.practicaltest01.ACTION3"
        )

        thread = Thread {
            while (true) {
                val action = actions.random()
                Log.d("PracticalTest01Service", "Service thread is running")
                val broadcast = Intent(action)

                broadcast.setPackage(packageName)

                broadcast.putExtra("mean", mean)
                broadcast.putExtra("geometricMean", geometricMean)
                broadcast.putExtra("date", Date().toString())

                sendBroadcast(broadcast)

                Log.d("PracticalTest01Service", "Broadcast trimis: action=$action, mean=$mean, geometricMean=$geometricMean, date=${Date().toString()}")
                sleep(10000)
            }
        }

        thread?.start()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        thread?.interrupt()
        super.onDestroy()
    }
}