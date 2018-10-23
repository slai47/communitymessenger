package messenger.slai.com.communitymessenger

import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import com.slai.communitymessenger.R

class MainWearActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_wear)

        // Enables Always-on
        setAmbientEnabled()
    }
}
