package xzf.spiderman.common.cache;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class MemDelayCache<K, SV extends Supplier<V>,V > implements DelayCache<K, SV, V>
{
    private Map<K, SV> map = new ConcurrentHashMap<>();

    @Override
    public SV compute(K key,BiFunction<? super K, ? super SV, ? extends SV> remappingFunction)
    {
        return map.compute(key,remappingFunction);
    }

    @Override
    public Optional<V> computeAndGet(K key, BiFunction<? super K, ? super SV, ? extends SV> remappingFunction)
    {
        Supplier<V> sv  =  map.compute(key,remappingFunction);
        return sv == null ? Optional.empty() : Optional.ofNullable(sv.get());
    }

}
