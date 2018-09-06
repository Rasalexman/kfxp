package com.rasalexman.kfxp.monads

typealias IOPerform<T> = ()->T?

data class IO<T>(val performIO:IOPerform<T>) {

    fun <T> perform(io:IO<T>):IOPerform<T> = io.performIO

    fun join():IO<T> = IO(performIO)

    inline fun map(crossinline fn: (IOPerform<T>) -> IOPerform<T>):IO<T> = IO(fn(performIO))


    inline fun chain(crossinline fn: (IOPerform<T>) -> IO<T>):IO<T> = IO(fn(performIO).performIO)


    companion object {
        fun<T> of(v:T?) = IO {v}
    }
}