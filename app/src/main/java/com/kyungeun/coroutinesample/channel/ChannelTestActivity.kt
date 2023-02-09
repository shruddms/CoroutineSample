package com.kyungeun.coroutinesample.channel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kyungeun.coroutinesample.databinding.ActivityChannelTestBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class ChannelTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChannelTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChannelTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnChannelBasic.setOnClickListener { testChannelBasic() }
        binding.btnChannelProducers.setOnClickListener { testChannelProducers() }
        binding.btnChannelPipeline.setOnClickListener { testChannelPipeline() }
        binding.btnChannelFair.setOnClickListener { testChannelFair() }
    }

    /**
     * Channel Basics
     */
    private fun testChannelBasic() = runBlocking {
        val channel = Channel<Int>()
        launch {
            for (x in 1..5) channel.send(x * x)
            channel.close()
        }
        for (y in channel) Timber.d(y.toString())
        Timber.d("Done!")
    }

    /**
     * Channel Producers
     */
    private fun testChannelProducers() = runBlocking {
        val squares = produceSquares()
        squares.consumeEach { Timber.d(it.toString()) }
        Timber.d("Done!")
    }

    private fun CoroutineScope.produceSquares(): ReceiveChannel<Int> = produce {
        for (x in 1..5) send(x * x)
    }

    /**
     * Channel Pipelines
     */
    private fun testChannelPipeline() = runBlocking {
        var cur = numbersFrom(2)
        repeat(10) {
            val prime = cur.receive()
            Timber.d(prime.toString())
            cur = filter(cur, prime)
        }
        coroutineContext.cancelChildren() // cancel all children to let main finish
    }

    private fun CoroutineScope.numbersFrom(start: Int) = produce<Int> {
        var x = start
        while (true) send(x++) // infinite stream of integers from start
    }

    private fun CoroutineScope.filter(numbers: ReceiveChannel<Int>, prime: Int) = produce<Int> {
        for (x in numbers) if (x % prime != 0) send(x)
    }

    /**
     * Fair Channel
     */
    // Two coroutines "ping" and "pong" are receiving the "ball" object from the shared "table" channel.
    private fun testChannelFair() = runBlocking {
        val table = Channel<Ball>() // a shared table
        launch { player("ping", table) }
        launch { player("pong", table) }
        table.send(Ball(0)) // serve the ball
        delay(1000) // delay 1 second
        coroutineContext.cancelChildren() // game over, cancel them
    }

    data class Ball(var hits: Int)

    private suspend fun player(name: String, table: Channel<Ball>) {
        for (ball in table) { // receive the ball in a loop
            ball.hits++
            Timber.d("$name $ball")
            delay(100) // wait a bit
            table.send(ball) // send the ball back
        }
    }
}