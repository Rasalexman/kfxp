package com.rasalexman.kfxp.monads

typealias Running<C, A> = (C) -> A

class Reader<C, out A>(val run: Running<C, A>) {

    inline fun <B> run(crossinline fn: () -> B): B = fn()

    inline fun <B> chain(crossinline fn: (A) -> B): Reader<C, B> {
        val that = this
        return Reader {
            fn(that.run(it))
        }
    }

    /**
     * Monad
     *
     * Receives a fa: (A) -> B as an argument to transform an A value to a B value. According to standard map convention,
     * map returns a new monad of the same type (Reader in this case), containing the mapped element of type B.
     */
    inline fun <B> map(crossinline fa: (A) -> B): Reader<C, B> = Reader { c ->
        fa(run(c))
    }

    /**
     * Monad
     *
     * The mapping function passed in has the type fa: (A) -> Reader<C, B> .
     * If we passed that function to the map combinator, we would end up having a duplicated reader as a result,
     * like Reader<C, Reader<C, B>> . Since we want to get a flattened result,
     * flatMap implementation is prepared to return a simple Reader<B>. flatMap can help us on Reader concatenation.
     */
    inline fun <B> flatMap(crossinline fa: (A) -> Reader<C, B>): Reader<C, B> = Reader { c ->
        fa(run(c)).run(c)
    }

    /**
     * Can be used to combine Readers with different context scopes.
     */
    inline fun <D> local(crossinline fd: (D) -> C): Reader<D, A> = Reader { d ->
        run(fd(d))
    }

    /**
     * Combinator is handy to combine two Readers into a single one
     * that will contain a function capable of receiving the context c, and returning a Pair<a, b>.
     */
    fun <B> zip(other: Reader<C, B>): Reader<C, Pair<A, B>> = this.flatMap { a ->
        other.map { b -> a to b }
    }

    fun <B> ap(readerWithFn:Reader<C, B>): Reader<C, Reader<C, A>> {
        val that:Reader<C, A> = this
        return readerWithFn.chain { _ -> Reader<C, A> { that.run(it) } }
    }

    companion object Factory {
        fun <C> ask(): Reader<C, C> = Reader { it }
        fun <C, A> of(a: A): Reader<C, A> = Reader { _ -> a }
    }
}