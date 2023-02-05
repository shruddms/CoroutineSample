package com.kyungeun.coroutinesample.repeatOnLifecycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kyungeun.coroutinesample.R
import com.kyungeun.coroutinesample.databinding.ActivityRepeatOnLifecycleTestBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber

class RepeatOnLifecycleTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRepeatOnLifecycleTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepeatOnLifecycleTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch(Dispatchers.Main) {
            //repeatOnLifecycle: coroutine job is performed only in foreground.
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                stringFlow.collect {
                    Timber.d(it)
                    binding.textNumber.text = it
                }
            }
        }
    }

    private val stringFlow: Flow<String> = flow {
        for (i in 0..1000) {
            emit("number : $i")
            delay(1000)
        }
    }
}