package ch.beerpro.data.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>A quadruple containing four elements.</p>
 *
 * <p>This class refers to its elements as 'first', 'second', 'third' and 'fourth'.</p>
 *
 * @param <F> the First Element type
 * @param <S> the Second Element type
 * @param <T> the Third Element type
 * @param <V> the Fourth Element type
 */
@Data
@AllArgsConstructor
public class Quadruple<F, S, T, V> implements Serializable {
    /**
     * Serialization version
     */
    private static final long serialVersionUID = 1L;

    private F first;
    private S second;
    private T third;
    private V fourth;

    public static <F, S, T, V> Quadruple<F, S, T, V> of(final F first, final S second, final T third, final V fourth) {
        return new Quadruple<>(first, second, third, fourth);
    }
}
