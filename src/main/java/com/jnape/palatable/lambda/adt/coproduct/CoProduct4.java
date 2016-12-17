package com.jnape.palatable.lambda.adt.coproduct;

import com.jnape.palatable.lambda.adt.hlist.Tuple4;
import com.jnape.palatable.lambda.functor.Bifunctor;
import com.jnape.palatable.lambda.functor.Functor;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;

/**
 * A generalization of the coproduct of four types <code>A</code>, <code>B</code>, <code>C</code>, and <code>D</code>.
 *
 * @param <A> a type parameter representing the first possible type of this coproduct
 * @param <B> a type parameter representing the second possible type of this coproduct
 * @param <C> a type parameter representing the third possible type of this coproduct
 * @param <D> a type parameter representing the fourth possible type of this coproduct
 * @see CoProduct2
 */
@FunctionalInterface
public interface CoProduct4<A, B, C, D> extends Functor<D, CoProduct4<A, B, C, ?>>, Bifunctor<C, D, CoProduct4<A, B, ?, ?>> {

    /**
     * Type-safe convergence requiring a match against all potential types.
     *
     * @param aFn morphism <code>A -&gt; R</code>
     * @param bFn morphism <code>B -&gt; R</code>
     * @param cFn morphism <code>C -&gt; R</code>
     * @param dFn morphism <code>D -&gt; R</code>
     * @param <R> result type
     * @return the result of applying the appropriate morphism from whichever type is represented by this coproduct to R
     * @see CoProduct2#match(Function, Function)
     */
    <R> R match(Function<? super A, ? extends R> aFn,
                Function<? super B, ? extends R> bFn,
                Function<? super C, ? extends R> cFn,
                Function<? super D, ? extends R> dFn);

    /**
     * Diverge this coproduct by introducing another possible type that it could represent.
     *
     * @param <E> the additional possible type of this coproduct
     * @return a Coproduct5&lt;A, B, C, D, E&gt;
     * @see CoProduct2#diverge()
     */
    default <E> CoProduct5<A, B, C, D, E> diverge() {
        return match(CoProduct5::a, CoProduct5::b, CoProduct5::c, CoProduct5::d);
    }

    /**
     * Converge this coproduct down to a lower order coproduct by mapping the last possible type into an earlier
     * possible type.
     *
     * @param convergenceFn function from last possible type to earlier type
     * @return a coproduct of the initial types without the terminal type
     * @see CoProduct3#converge
     */
    default CoProduct3<A, B, C> converge(Function<? super D, ? extends CoProduct3<A, B, C>> convergenceFn) {
        return match(CoProduct3::a, CoProduct3::b, CoProduct3::c, convergenceFn);
    }

    /**
     * Project this coproduct onto a tuple.
     *
     * @return a tuple of the coproduct projection
     * @see CoProduct2#project()
     */
    default Tuple4<Optional<A>, Optional<B>, Optional<C>, Optional<D>> project() {
        return match(a -> tuple(Optional.of(a), Optional.empty(), Optional.empty(), Optional.empty()),
                     b -> tuple(Optional.empty(), Optional.of(b), Optional.empty(), Optional.empty()),
                     c -> tuple(Optional.empty(), Optional.empty(), Optional.of(c), Optional.empty()),
                     d -> tuple(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(d)));
    }

    /**
     * Convenience method for projecting this coproduct onto a tuple and then extracting the first slot value.
     *
     * @return an optional value representing the projection of the "a" type index
     */
    @SuppressWarnings("unused")
    default Optional<A> projectA() {
        return project()._1();
    }

    /**
     * Convenience method for projecting this coproduct onto a tuple and then extracting the second slot value.
     *
     * @return an optional value representing the projection of the "b" type index
     */
    @SuppressWarnings("unused")
    default Optional<B> projectB() {
        return project()._2();
    }

    /**
     * Convenience method for projecting this coproduct onto a tuple and then extracting the third slot value.
     *
     * @return an optional value representing the projection of the "c" type index
     */
    @SuppressWarnings("unused")
    default Optional<C> projectC() {
        return project()._3();
    }

    /**
     * Convenience method for projecting this coproduct onto a tuple and then extracting the fourth slot value.
     *
     * @return an optional value representing the projection of the "d" type index
     */
    @SuppressWarnings("unused")
    default Optional<D> projectD() {
        return project()._4();
    }

    @Override
    default <E> CoProduct4<A, B, C, E> fmap(Function<? super D, ? extends E> fn) {
        return biMapR(fn);
    }

    @Override
    @SuppressWarnings("unchecked")
    default <E> CoProduct4<A, B, E, D> biMapL(Function<? super C, ? extends E> fn) {
        return (CoProduct4<A, B, E, D>) Bifunctor.super.biMapL(fn);
    }

    @Override
    @SuppressWarnings("unchecked")
    default <E> CoProduct4<A, B, C, E> biMapR(Function<? super D, ? extends E> fn) {
        return (CoProduct4<A, B, C, E>) Bifunctor.super.biMapR(fn);
    }

    @Override
    default <E, F> CoProduct4<A, B, E, F> biMap(Function<? super C, ? extends E> lFn,
                                                Function<? super D, ? extends F> rFn) {
        return match(CoProduct4::a, CoProduct4::b, c -> c(lFn.apply(c)), d -> d(rFn.apply(d)));
    }

    /**
     * Static factory method for wrapping a value of type <code>A</code> in a {@link CoProduct4}.
     *
     * @param a   the value
     * @param <A> a type parameter representing the first possible type of this coproduct
     * @param <B> a type parameter representing the second possible type of this coproduct
     * @param <C> a type parameter representing the third possible type of this coproduct
     * @param <D> a type parameter representing the fourth possible type of this coproduct
     * @return the wrapped value as a CoProduct4&lt;A, B, C, D&gt;
     */
    static <A, B, C, D> CoProduct4<A, B, C, D> a(A a) {
        class _A implements CoProduct4<A, B, C, D> {

            private final A a;

            private _A(A a) {
                this.a = a;
            }

            @Override
            public <R> R match(Function<? super A, ? extends R> aFn, Function<? super B, ? extends R> bFn,
                               Function<? super C, ? extends R> cFn, Function<? super D, ? extends R> dFn) {
                return aFn.apply(a);
            }

            @Override
            public boolean equals(Object other) {
                return other instanceof _A
                        && Objects.equals(a, ((_A) other).a);
            }

            @Override
            public int hashCode() {
                return Objects.hash(a);
            }

            @Override
            public String toString() {
                return "CoProduct4{" +
                        "a=" + a +
                        '}';
            }
        }

        return new _A(a);
    }

    /**
     * Static factory method for wrapping a value of type <code>B</code> in a {@link CoProduct4}.
     *
     * @param b   the value
     * @param <A> a type parameter representing the first possible type of this coproduct
     * @param <B> a type parameter representing the second possible type of this coproduct
     * @param <C> a type parameter representing the third possible type of this coproduct
     * @param <D> a type parameter representing the fourth possible type of this coproduct
     * @return the wrapped value as a CoProduct4&lt;A, B, C, D&gt;
     */
    static <A, B, C, D> CoProduct4<A, B, C, D> b(B b) {
        class _B implements CoProduct4<A, B, C, D> {

            private final B b;

            private _B(B b) {
                this.b = b;
            }

            @Override
            public <R> R match(Function<? super A, ? extends R> aFn, Function<? super B, ? extends R> bFn,
                               Function<? super C, ? extends R> cFn, Function<? super D, ? extends R> dFn) {
                return bFn.apply(b);
            }

            @Override
            public boolean equals(Object other) {
                return other instanceof _B
                        && Objects.equals(b, ((_B) other).b);
            }

            @Override
            public int hashCode() {
                return Objects.hash(b);
            }

            @Override
            public String toString() {
                return "CoProduct4{" +
                        "b=" + b +
                        '}';
            }
        }
        return new _B(b);
    }

    /**
     * Static factory method for wrapping a value of type <code>C</code> in a {@link CoProduct4}.
     *
     * @param c   the value
     * @param <A> a type parameter representing the first possible type of this coproduct
     * @param <B> a type parameter representing the second possible type of this coproduct
     * @param <C> a type parameter representing the third possible type of this coproduct
     * @param <D> a type parameter representing the fourth possible type of this coproduct
     * @return the wrapped value as a CoProduct4&lt;A, B, C, D&gt;
     */
    static <A, B, C, D> CoProduct4<A, B, C, D> c(C c) {
        class _C implements CoProduct4<A, B, C, D> {

            private final C c;

            private _C(C c) {
                this.c = c;
            }

            @Override
            public <R> R match(Function<? super A, ? extends R> aFn, Function<? super B, ? extends R> bFn,
                               Function<? super C, ? extends R> cFn, Function<? super D, ? extends R> dFn) {
                return cFn.apply(c);
            }

            @Override
            public boolean equals(Object other) {
                return other instanceof _C
                        && Objects.equals(c, ((_C) other).c);
            }

            @Override
            public int hashCode() {
                return Objects.hash(c);
            }

            @Override
            public String toString() {
                return "CoProduct4{" +
                        "c=" + c +
                        '}';
            }
        }
        return new _C(c);
    }

    /**
     * Static factory method for wrapping a value of type <code>D</code> in a {@link CoProduct4}.
     *
     * @param d   the value
     * @param <A> a type parameter representing the first possible type of this coproduct
     * @param <B> a type parameter representing the second possible type of this coproduct
     * @param <C> a type parameter representing the third possible type of this coproduct
     * @param <D> a type parameter representing the fourth possible type of this coproduct
     * @return the wrapped value as a CoProduct4&lt;A, B, C, D&gt;
     */
    static <A, B, C, D> CoProduct4<A, B, C, D> d(D d) {
        class _D implements CoProduct4<A, B, C, D> {

            private final D d;

            private _D(D d) {
                this.d = d;
            }

            @Override
            public <R> R match(Function<? super A, ? extends R> aFn, Function<? super B, ? extends R> bFn,
                               Function<? super C, ? extends R> cFn, Function<? super D, ? extends R> dFn) {
                return dFn.apply(d);
            }

            @Override
            public boolean equals(Object other) {
                return other instanceof _D
                        && Objects.equals(d, ((_D) other).d);
            }

            @Override
            public int hashCode() {
                return Objects.hash(d);
            }

            @Override
            public String toString() {
                return "CoProduct4{" +
                        "d=" + d +
                        '}';
            }
        }

        return new _D(d);
    }
}
