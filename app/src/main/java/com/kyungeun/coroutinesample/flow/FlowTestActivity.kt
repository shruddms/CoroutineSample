package com.kyungeun.coroutinesample.flow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kyungeun.coroutinesample.databinding.ActivityFlowTestBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import kotlin.system.measureTimeMillis

class FlowTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlowTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlowTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnFlowBasic.setOnClickListener { testFlowBasic() }
        binding.btnFlowCancellation.setOnClickListener { testFlowCancellation() }
        binding.btnFlowOperators.setOnClickListener { testFlowOperators() }
        binding.btnFlowOn.setOnClickListener { testFlowOn() }
        binding.btnFlowBuffering.setOnClickListener { testFlowBuffering() }
        binding.btnFlowExceptions.setOnClickListener { testFlowExceptions() }
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

    private fun testFlowCancellation() = runBlocking<Unit> {
        withTimeoutOrNull(250) { // Timeout after 250ms
            simple().collect { value -> Timber.d("value : $value") }
        }
        Timber.d("Done!")
    }

    private fun testFlowOperators() = runBlocking<Unit> {
        (1..5).asFlow() // a flow of requests
            .filter { it % 2 == 0 }
            .map { request -> performRequest(request) }
            .collect { response -> Timber.d(response) }
    }

    private fun testFlowOn() = runBlocking<Unit> {
        simpleFlowOn().collect { value ->
            Timber.d("value : $value")
        }
    }

    private fun testFlowBuffering() = runBlocking<Unit> {
        val time = measureTimeMillis {
            simple().collect { value ->
                delay(300) // pretend we are processing it for 300 ms
                Timber.d("value : $value")
            }
        }
        println("Collected in $time ms")
    }

    private fun testFlowExceptions() = runBlocking<Unit> {
        try {
            simpleFlowExceptions().collect { value -> println(value) }
        } catch (e: Throwable) {
            Timber.d("Caught $e")
        }
    }

    private fun simple(): Flow<Int> = flow { // flow builder
        for (i in 1..3) {
            delay(100) // pretend we are doing something useful here
            emit(i) // emit next value
        }
    }

    private fun simpleFlowOn(): Flow<Int> = flow { // flow builder
        for (i in 1..3) {
            delay(100) // pretend we are doing something useful here
            emit(i) // emit next value
        }
    }.flowOn(Dispatchers.Default)

    private fun simpleFlowExceptions(): Flow<String> = flow {
        for (i in 1..3) {
            delay(100) // pretend we are doing something useful here
            emit(i) // emit next value
        }
    }.map { value ->
        check(value <= 1) { "Crashed on $value" }
        "string $value"
    }

    private suspend fun performRequest(request: Int): String {
        delay(1000) // imitate long-running asynchronous work
        return "response $request"
    }
}

