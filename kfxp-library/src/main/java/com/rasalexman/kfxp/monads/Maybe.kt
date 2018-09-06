package com.rasalexman.kfxp.monads

data class Maybe<T>(val value:T?) {

    val isNothing:Boolean
        get() = (value is Nothing || value == null)

    inline fun map(crossinline fn: (T) -> T):Maybe<T> = if(isNothing) this else Maybe.of(fn(value!!))

    inline fun chain(crossinline fn: (T) -> Maybe<T>):Maybe<T> = if(isNothing) Maybe.of(null) else fn(value!!)

    companion object {
        fun<T> of(v:T?) = Maybe(v)
    }
}