package com.kyungeun.coroutinesample.basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.kyungeun.coroutinesample.R
import com.kyungeun.coroutinesample.databinding.ActivityBasicTestBinding
import kotlinx.coroutines.*
import timber.log.Timber

class BasicTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBasicTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBasicTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLifecycleScope.setOnClickListener { testLifecycleScope() }
        binding.btnGlobalScope.setOnClickListener { testGlobalScope() }
        binding.btnCoroutineScope.setOnClickListener { testCoroutineScope() }
        binding.btnSupervisorScope.setOnClickListener { testSupervisorScope() }
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

    private fun testCoroutineScope() {
        showSnackBar(getString(R.string.coroutine_scope))
        // If one of the task fails, the other tasks will not run
        CoroutineScope(Dispatchers.Default).launch(exceptionHandler) {
            val testOne = withContext(Dispatchers.Default) { doLongRunningTaskOne() }
            Timber.d("testOne = $testOne")

            val testError = withContext(Dispatchers.Default) {
                doLongRunningTaskError(true)
            }
            Timber.d("testError = $testError")

            val testTwo = withContext(Dispatchers.Default) { doLongRunningTaskTwo() }
            Timber.d("testTwo = $testTwo")
        }
    }

    private fun testSupervisorScope() {
        showSnackBar(getString(R.string.supervisor_scope))
        CoroutineScope(Dispatchers.Default).launch(exceptionHandler) {
            supervisorScope { // If one of the task fails, the other tasks will continue to run
                val testOne = withContext(Dispatchers.Default) { doLongRunningTaskOne() }
                Timber.d("testOne = $testOne")

                val testError = try {
                    withContext(Dispatchers.Default) {
                        doLongRunningTaskError(
                            true
                        )
                    }
                } catch (e: Exception) {
                    Timber.e("exception: $e")
                    null
                }
                Timber.d("testError = $testError")

                val testTwo = withContext(Dispatchers.Default) { doLongRunningTaskTwo() }
                Timber.d("testTwo = $testTwo")
            }
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

    // CoroutineExceptionHandler: An optional element in the coroutine context to handle uncaught exceptions.
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

    private suspend fun doLongRunningTaskError(check: Boolean): Int {
        return withContext(Dispatchers.Default) {
            delay(2000)
            if (check) {
                throw Exception("Some Error")
            }
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