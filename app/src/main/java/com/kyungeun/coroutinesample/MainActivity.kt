package com.kyungeun.coroutinesample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kyungeun.coroutinesample.basic.BasicTestActivity
import com.kyungeun.coroutinesample.channel.ChannelActivity
import com.kyungeun.coroutinesample.databinding.ActivityMainBinding
import com.kyungeun.coroutinesample.repeatOnLifecycle.RepeatOnLifecycleTestActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTest.setOnClickListener {
            startActivity(Intent(this@MainActivity, BasicTestActivity::class.java))
        }
        binding.btnRepeatOnLifecycleTest.setOnClickListener {
            startActivity(Intent(this@MainActivity, RepeatOnLifecycleTestActivity::class.java))
        }
        binding.btnChannelTest.setOnClickListener {
            startActivity(Intent(this@MainActivity, ChannelActivity::class.java))
        }
    }
}