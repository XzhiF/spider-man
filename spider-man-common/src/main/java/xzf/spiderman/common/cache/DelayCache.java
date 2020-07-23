package xzf.spiderman.common.cache;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface DelayCache <K,SV extends Supplier<V>, V>
{
     SV compute(K key, BiFunction<? super K, ? super SV, ? extends SV> remappingFunction);

     Optional<V> computeAndGet(K key, BiFunction<? super K, ? super SV, ? extends SV> remappingFunction);
}
