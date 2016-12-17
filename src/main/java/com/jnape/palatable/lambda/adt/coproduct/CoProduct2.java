package com.jnape.palatable.lambda.adt.coproduct;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;
import com.jnape.palatable.lambda.functor.Bifunctor;
import com.jnape.palatable.lambda.functor.Functor;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static com.jnape.palatable.lambda.adt.hlist.HList.tuple;

/**
 * A generalization of the coproduct of two types <code>A</code> and <code>B</code>. Coproducts represent the disjoint
 * union of two or more distinct types, and provides an interface for specifying morphisms from those types to a common
 * result type.
 * <p>
 * Learn more about <a href="https://en.wikipedia.org/wiki/Coproduct">Coproducts</a>.
 *
 * @param <A> a type parameter representing the first possible type of this coproduct
 * @param <B> a type parameter representing the second possible type of this coproduct
 */
@FunctionalInterface
public interface CoProduct2<A, B> extends Functor<B, CoProduct2<A, ?>>, Bifunctor<A, B, CoProduct2<?, ?>> {

    /**
     * Type-safe convergence requiring a match against all potential types.
     *
     * @param aFn morphism <code>A -&gt; R</code>
     * @param bFn morphism <code>B -&gt; R</code>
     * @param <R> result type
     * @return the result of applying the appropriate morphism from whichever type is represented by this coproduct to R
     */
    <R> R match(Function<? super A, ? extends R> aFn, Function<? super B, ? extends R> bFn);

    /**
     * Diverge this coproduct by introducing another possible type that it could represent. As no morphisms can be
     * provided mapping current types to the new type, this operation merely acts as a convenience method to allow the
     * use of a more convergent coproduct with a more divergent one; that is, if a <code>CoProduct3&lt;String, Integer,
     * Boolean&gt;</code> is expected, a <code>CoProduct2&lt;String, Integer&gt;</code> should suffice.
     * <p>
     * Generally, we use inheritance to make this a non-issue; however, with coproducts of differing magnitudes, we
     * cannot guarantee variance compatibility in one direction conveniently at construction time, and in the other
     * direction, at all. A {@link CoProduct2} could not be a {@link CoProduct3} without specifying all type parameters
     * that are possible for a {@link CoProduct3} - more specifically, the third possible type - which is not
     * necessarily known at construction time, or even useful if never used in the context of a {@link CoProduct3}. The
     * inverse inheritance relationship - {@link CoProduct3} &lt; {@link CoProduct2} - is inherently unsound, as a
     * {@link CoProduct3} cannot correctly implement {@link CoProduct2#match}, given that the third type <code>C</code>
     * is always possible.
     * <p>
     * For this reason, there is a <code>diverge</code> method supported between all <code>CoProduct</code> types of
     * single magnitude difference.
     *
     * @param <C> the additional possible type of this coproduct
     * @return a coproduct of the initial types plus the new type
     */
    default <C> CoProduct3<A, B, C> diverge() {
        return match(CoProduct3::a, CoProduct3::b);
    }

    /**
     * Project this coproduct onto a tuple, such that the slot in the tuple that corresponds to this coproduct's value
     * is present, while the other slots are absent.
     *
     * @return a tuple of the coproduct projection
     */
    default Tuple2<Optional<A>, Optional<B>> project() {
        return match(a -> tuple(Optional.of(a), Optional.empty()),
                     b -> tuple(Optional.empty(), Optional.of(b)));
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

    @Override
    default <C> CoProduct2<A, C> fmap(Function<? super B, ? extends C> fn) {
        return biMapR(fn);
    }

    @Override
    @SuppressWarnings("unchecked")
    default <C> CoProduct2<C, B> biMapL(Function<? super A, ? extends C> fn) {
        return (CoProduct2<C, B>) Bifunctor.super.biMapL(fn);
    }

    @Override
    @SuppressWarnings("unchecked")
    default <C> CoProduct2<A, C> biMapR(Function<? super B, ? extends C> fn) {
        return (CoProduct2<A, C>) Bifunctor.super.biMapR(fn);
    }

    @Override
    default <C, D> CoProduct2<C, D> biMap(Function<? super A, ? extends C> lFn,
                                          Function<? super B, ? extends D> rFn) {
        return match(a -> a(lFn.apply(a)), b -> b(rFn.apply(b)));
    }

    /**
     * Static factory method for wrapping a value of type <code>A</code> in a {@link CoProduct2}.
     *
     * @param a   the value
     * @param <A> a type parameter representing the first possible type of this coproduct
     * @param <B> a type parameter representing the second possible type of this coproduct
     * @return the wrapped value as a CoProduct2&lt;A, B&gt;
     */
    static <A, B> CoProduct2<A, B> a(A a) {
        class _A implements CoProduct2<A, B> {

            private final A a;

            private _A(A a) {
                this.a = a;
            }

            @Override
            public <R> R match(Function<? super A, ? extends R> aFn, Function<? super B, ? extends R> bFn) {
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
                return "CoProduct2{" +
                        "a=" + a +
                        '}';
            }
        }

        return new _A(a);
    }

    /**
     * Static factory method for wrapping a value of type <code>B</code> in a {@link CoProduct2}.
     *
     * @param b   the value
     * @param <A> a type parameter representing the first possible type of this coproduct
     * @param <B> a type parameter representing the second possible type of this coproduct
     * @return the wrapped value as a CoProduct2&lt;A, B&gt;
     */
    static <A, B> CoProduct2<A, B> b(B b) {
        class _B implements CoProduct2<A, B> {

            private final B b;

            private _B(B b) {
                this.b = b;
            }

            @Override
            public <R> R match(Function<? super A, ? extends R> aFn, Function<? super B, ? extends R> bFn) {
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
                return "CoProduct2{" +
                        "b=" + b +
                        '}';
            }
        }
        return new _B(b);
    }
}
