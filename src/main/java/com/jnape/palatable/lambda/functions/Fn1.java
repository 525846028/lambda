package com.jnape.palatable.lambda.functions;

import com.jnape.palatable.lambda.functions.builtin.fn1.Constantly;
import com.jnape.palatable.lambda.functor.Profunctor;
import com.jnape.palatable.lambda.functor.applicative.Applicative;

import java.util.function.Function;

import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;

/**
 * A function taking a single argument. This is the core function type that all other function types extend and
 * auto-curry with.
 *
 * @param <A> The argument type
 * @param <B> The result type
 */
@FunctionalInterface
public interface Fn1<A, B> extends Applicative<B, Fn1<A, ?>>, Profunctor<A, B, Fn1<?, ?>>, Function<A, B> {

    /**
     * Invoke this function with the given argument.
     *
     * @param a the argument
     * @return the result of the function application
     */
    B apply(A a);

    /**
     * Produce an <code>Fn1</code> from any <code>A</code> to <code>c</code>.
     *
     * @param c   the return value
     * @param <C> the return parameter type
     * @return Fn1 from A to C
     * @see Constantly
     */
    @Override
    default <C> Fn1<A, C> pure(C c) {
        return constantly(c);
    }

    /**
     * Given an <code>Fn1&lt;A, B&gt;</code> <code>g</code> and an <code>Fn1&lt;A, Function&lt;? super B, ? extends
     * C&gt;&gt;</code> <code>f</code>, return an <code>Fn1&lt;A, C&gt;</code>
     * <code>a -&gt; f.apply(a, g.apply(a))</code>.
     *
     * @param <C>   the return parameter type
     * @param appFn the function to apply
     * @return Fn1 from A to C
     */
    @Override
    @SuppressWarnings("unchecked")
    default <C> Fn1<A, C> sequence(Applicative<? extends Function<? super B, ? extends C>, Fn1<A, ?>> appFn) {
        return a -> ((Fn1<A, ? extends Function<B, C>>) appFn).apply(a).apply(this.apply(a));
    }

    default <C> Fn1<A, C> sequence(Fn1<A, Fn1<B, C>> appFn) {
        return sequence((Applicative<? extends Function<B, C>, Fn1<A, ?>>) appFn);
    }

    default <C> Fn1<A, C> sequence(Fn2<A, B, C> appFn) {
        return sequence((Applicative<? extends Function<B, C>, Fn1<A, ?>>) appFn);
    }

    @Override
    default <C> Fn1<A, C> discardL(Applicative<C, Fn1<A, ?>> appB) {
        return (Fn1<A, C>) Applicative.super.discardL(appB);
    }

    @Override
    default <C> Fn1<A, B> discardR(Applicative<C, Fn1<A, ?>> appB) {
        return (Fn1<A, B>) Applicative.super.discardL(appB);
    }

    /**
     * Left-to-right composition, such that <code>g.then(f).apply(x)</code> is equivalent to
     * <code>f.apply(g.apply(x))</code>.
     *
     * @param f   the function to invoke with this function's return value
     * @param <C> the return type of the next function to invoke
     * @return a function representing the composition of this function and f
     */
    default <C> Fn1<A, C> then(Function<? super B, ? extends C> f) {
        return fmap(f);
    }

    /**
     * Also left-to-right composition (<a href="http://jnape.com/the-perils-of-implementing-functor-in-java/">sadly</a>).
     *
     * @param <C> the return type of the next function to invoke
     * @param f   the function to invoke with this function's return value
     * @return a function representing the composition of this function and f
     * @see Fn1#then(Function)
     */
    @Override
    default <C> Fn1<A, C> fmap(Function<? super B, ? extends C> f) {
        return a -> f.apply(apply(a));
    }

    /**
     * Contravariantly map over the argument to this function, producing a function that takes the new argument type,
     * and produces the same result.
     *
     * @param <Z> the new argument type
     * @param fn  the contravariant argument mapping function
     * @return a new function from Z (the new argument type) to B (the same result)
     */
    @Override
    default <Z> Fn1<Z, B> diMapL(Function<Z, A> fn) {
        return (Fn1<Z, B>) Profunctor.super.diMapL(fn);
    }

    /**
     * Covariantly map over the return value of this function, producing a function that takes the same argument, and
     * produces the new result type.
     *
     * @param <C> the new result type
     * @param fn  the covariant result mapping function
     * @return a new function from A (the same argument type) to C (the new result type)
     */
    @Override
    default <C> Fn1<A, C> diMapR(Function<B, C> fn) {
        return (Fn1<A, C>) Profunctor.super.diMapR(fn);
    }

    /**
     * Exercise both <code>diMapL</code> and <code>diMapR</code> over this function in the same invocation.
     *
     * @param <Z> the new argument type
     * @param <C> the new result type
     * @param lFn the contravariant argument mapping function
     * @param rFn the covariant result mapping function
     * @return a new function from Z (the new argument type) to C (the new result type)
     */
    @Override
    default <Z, C> Fn1<Z, C> diMap(Function<Z, A> lFn, Function<B, C> rFn) {
        return lFn.andThen(this).andThen(rFn)::apply;
    }

    /**
     * Override of {@link Function#compose(Function)}, returning an instance of <code>Fn1</code> for compatibility.
     * Right-to-left composition.
     *
     * @param before the function who's return value is this function's argument
     * @param <Z>    the new argument type
     * @return a new function from Z (the new argument type) to B (the same result type)
     */
    @Override
    default <Z> Fn1<Z, B> compose(Function<? super Z, ? extends A> before) {
        return z -> apply(before.apply(z));
    }

    /**
     * Override of {@link Function#andThen(Function)}, returning an instance of <code>Fn1</code> for compatibility.
     * Left-to-right composition.
     *
     * @param after the function to invoke on this function's return value
     * @param <C>   the new result type
     * @return a new function from A (the same argument type) to C (the new result type)
     */
    @Override
    default <C> Fn1<A, C> andThen(Function<? super B, ? extends C> after) {
        return a -> after.apply(apply(a));
    }

    /**
     * Static factory method for wrapping a {@link Function} in an {@link Fn1}. Useful for avoid explicit casting when
     * using method references as {@link Fn1}s.
     *
     * @param function the function to adapt
     * @param <A>      the input argument type
     * @param <B>      the output type
     * @return the Fn1
     */
    static <A, B> Fn1<A, B> adapt(Function<A, B> function) {
        return function::apply;
    }
}
