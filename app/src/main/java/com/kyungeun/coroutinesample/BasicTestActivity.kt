package com.kyungeun.coroutinesample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.kyungeun.coroutinesample.databinding.ActivityBasicTestBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class BasicTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBasicTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBasicTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLifecycleScope.setOnClickListener { testLifecycleScope() }
        binding.btnGlobalScope.setOnClickListener { testGlobalScope() }
        binding.btnHandlerException.setOnClickListener { testHandlerException() }
        binding.btnTwoTasks.setOnClickListener { testTwoTasks() }
        binding.btnTwoAsync.setOnClickListener { testTwoAsync() }
    }

    private fun testLifecycleScope() {
        showSnackBar(getString(R.string.lifecycle_scope))
        lifecycleScope.launch {
            Timber.d("Before Task")
            doLongRunningTask()
            Timber.d("After Task")
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun testGlobalScope() {
        showSnackBar(getString(R.string.global_scope))
        GlobalScope.launch {
            Timber.d("Before Task")
            doLongRunningTask()
            Timber.d("After Task")
        }
    }

    private fun testHandlerException() {
        showSnackBar(getString(R.string.handler_exception))
        lifecycleScope.launch(exceptionHandler) {
            Timber.d("Before Task")
            doLongRunningTask()
            throw Exception("Some Error")
            Timber.d("After Task") // This line will not be executed
        }
    }

    private fun testTwoTasks() {
        showSnackBar(getString(R.string.two_tasks))
        val job = lifecycleScope.launch(Dispatchers.Main) {
            Timber.d("Before Task 1")
            doLongRunningTask()
            Timber.d("After Task 1")
        }
        lifecycleScope.launch(Dispatchers.Main) {
            Timber.d("Before Task 2")
            job.cancel() // Cancel the first task
            doLongRunningTask()
            Timber.d("After Task 2")
        }
    }

    private fun testTwoAsync() {
        showSnackBar(getString(R.string.two_async))
        lifecycleScope.launch {
            val deferredOne = async {
                Timber.d("Before Task 1")
                doLongRunningTaskOne()
                Timber.d("After Task 1")
            }
            val deferredTwo = async {
                Timber.d("Before Task 2")
                doLongRunningTaskTwo()
                Timber.d("After Task 2")
            }
            Timber.d("deferredOne = ${deferredOne.await()}")
            Timber.d("deferredTwo = ${deferredTwo.await()}")
        }
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        Timber.e("exception handler: $e")
    }

    private suspend fun doLongRunningTask() {
        withContext(Dispatchers.Default) {
            Timber.d("Before Delay")
            delay(2000)
            Timber.d("After Delay")
        }
    }

    private suspend fun doLongRunningTaskOne(): Int {
        return withContext(Dispatchers.Default) {
            delay(2000)
            return@withContext 10
        }
    }

    private suspend fun doLongRunningTaskTwo(): Int {
        return withContext(Dispatchers.Default) {
            delay(2000)
            return@withContext 10
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }
}