package com.kyungeun.coroutinesample.flow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kyungeun.coroutinesample.databinding.ActivityFlowTestBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class FlowTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlowTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlowTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnFlowBasic.setOnClickListener { testFlowBasic() }
    }

    private fun testFlowBasic() = runBlocking<Unit> {
        launch {
            for (k in 1..3) {
                Timber.d("I'm not blocked $k")
                delay(100)
            }
        }
        simple().collect { value -> Timber.d("value : $value") }
    }

    private fun simple(): Flow<Int> = flow { // flow builder
        for (i in 1..3) {
            delay(100) // pretend we are doing something useful here
            emit(i) // emit next value
        }
    }
}