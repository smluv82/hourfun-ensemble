package com.hourfun.ensemble.config.redis;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository("redisDataAPI")
public class RedisDataRepository<K, V> {
    @Qualifier("redisTemplate")
    private RedisTemplate<K, V> defaultRedisTemplate;

    //	@Qualifier("backendRedisTemplateDefault")
    private RedisTemplate<K, V> redisTemplate;

    public void setRedisTemplate(final RedisTemplate<K, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private void checkRedisTemplate(RedisTemplate<K, V> redisTemplate) {
        log.debug("checkRedisTemplate : {}", redisTemplate);

        if(redisTemplate == null)
            redisTemplate = defaultRedisTemplate;
    }

    /**
     * ValueOperations 호출메서드
     *
     * @return
     */
    public ValueOperations<K, V> getValueOperation() {
        return redisTemplate.opsForValue();
    }

    /**
     * 데이터 저장 함수
     *
     * @param <K> Generic Key (anyType)
     * @param <T> Generic Value (anyType)
     */
    public void setData(final K key, final V value) {
        setData(key, value, redisTemplate);
    }

    public void setData(final K key, final V value, RedisTemplate<K, V> template) {
        log.info("RedisDataRepository set key[{}]", key.toString());
        log.info("RedisDataRepository set value[{}]", value.toString());
        checkRedisTemplate(template);
        template.opsForValue().set(key, value);
    }

    /**
     * 데이터 저장 함수
     *
     * @param <K>         Generic Key (anyType)
     * @param <T>         Generic Value (anyType)
     * @param exipireTime TimeUnit 만료 시간 설정
     */
    public void setData(final K key, final V value, final long expireTime, TimeUnit timeUnit) {
        setData(key, value, expireTime, timeUnit, redisTemplate);
    }

    public void setData(final K key, final V value, final long expireTime, final TimeUnit timeUnit, final RedisTemplate<K, V> template) {
        log.info("RedisDataRepository set key[{}]", key.toString());
        log.info("RedisDataRepository set value[{}]", value.toString());

        template.opsForValue().set(key, value, expireTime, timeUnit);
    }

    public void setData(final K key, final V value, final Duration duration) {
        setData(key, value, duration, redisTemplate);
    }

    public void setData(final K key, final V value, final Duration duration, final RedisTemplate<K, V> template) {
        log.info("RedisDataRepository set key[{}]", key.toString());
        log.info("RedisDataRepository set value[{}]", value.toString());

        template.opsForValue().set(key, value, duration);
    }

    /**
     * redis에 저장된 데이터 불러오기 함수
     *
     * @param <K> Generic Key (anyType)
     * @return <T> Generic Value (anyType)- null 이 리턴될 수 있음.
     */
    public V getData(final K key) {
        return getData(key, redisTemplate);
    }

    public V getData(final K key, final RedisTemplate<K, V> template) {
        log.info("RedisDataRepository get key[{}]", key);

        return template.opsForValue().get(key);
    }

    /**
     * redis에새로운 값을 저장한 후 이전에 저장 된 값을 리턴
     *
     * @param key
     * @param value
     * @return
     */
    public V getAndSet(final K key, final V value) {
        return getAndSet(key, value, redisTemplate, 0, null);
    }

    public V getAndSet(final K key, final V value, final RedisTemplate<K, V> template) {
        return getAndSet(key, value, template, 0, null);
    }

    public V getAndSet(final K key, final V value, final RedisTemplate<K, V> template, final long timeout, final TimeUnit timeUnit) {
        log.info("RedisDataRepository getAndSet key[{}]", key.toString());
        log.info("RedisDataRepository getAndSet value[{}]", value.toString());

        final V oldValue = template.opsForValue().getAndSet(key, value);

        if(timeout != 0 && timeUnit != null)
            template.expire(key, timeout, timeUnit);

        return oldValue;
    }

    /**
     * redis에 저장된 데이터 삭제 함수
     *
     * @param <K> Generic Key (anyType)
     */
    public Boolean deleteData(K key) {
        return deleteData(key, redisTemplate);
    }

    public Boolean deleteData(final K key, final RedisTemplate<K, V> template) {
        return template.delete(key);
    }

    /**
     * 파마매터로 전달되는 List 안에 Key를 가진 모든 데이터 삭제
     *
     * @param Collection<K> Generic Key list(anyType)
     */
    public Long deleteDatas(final Collection<K> keys) {
        return deleteDatas(keys, redisTemplate);
    }

    public Long deleteDatas(final Collection<K> keys, final RedisTemplate<K, V> template) {
        return template.delete(keys);
    }

    /**
     * 파라매터로 전달되는 Key 가 존재하는 지 확인
     *
     * @param <K> Generic Key (anyType)
     * @return 키가 존재하면 True, 아니면 False
     */
    public Boolean hasContainKey(K key) {
        return hasContainKey(key, redisTemplate);
    }

    public Boolean hasContainKey(final K key, final RedisTemplate<K, V> template) {
        return template.hasKey(key);
    }

    /**
     * HashMap 객체 전체를 REDIS에 저장하는 함수
     *
     * @param multiDataMap Redis에 저장할 맵 데이터
     */
    public void setMultiData(final Map<K, V> multiDataMap) {
        setMultiData(multiDataMap, redisTemplate);
    }

    public void setMultiData(final Map<K, V> multiDataMap, final RedisTemplate<K, V> template) {
        redisTemplate.opsForValue().multiSet(multiDataMap);
    }

    /**
     * 파라미터로 전달되는 List안에 Key를 가진 모든 데이터 가져오기
     *
     * @param ArrayList<K> Generic Key (anyType)
     * @return HashMap<K, T> (anyType)- 빈 MAP이 리턴될 수 있음.
     */
    public Map<K, V> getMultiData(final List<K> keys) {
        List<V> resultList = redisTemplate.opsForValue().multiGet(keys);
        Map<K, V> resultMap = Maps.newHashMap();

        int index = 0;

        for (var result : resultList) {
            resultMap.put(keys.get(index), result);
            index++;
        }
        return resultMap;
    }

    public Long increment(final K key, final Long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 파라미터 전달되는 Key의 expire 설
     *
     * @param key
     * @param timeout
     * @param timeUnit
     * @return
     */
    public Boolean expire(final K key, final long timeout, final TimeUnit timeUnit) {
        return expire(key, timeout, timeUnit, redisTemplate);
    }

    public Boolean expire(final K key, final long timeout, final TimeUnit timeUnit, final RedisTemplate<K, V> template) {
        return template.expire(key, timeout, timeUnit);
    }

    /**
     * 여기 밑에서부터 Hash Data
     * @param key
     * @param hashKey
     * @param value
     */



    public void putHashData(final K key, final Object hashKey, final Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public void putHashData(final K key, final Object hashKey, final Object value, final RedisTemplate<K, V> template) {
        template.opsForHash().put(key, hashKey, value);
    }

    public Object getHashData(final K key, final Object hashKey) {
        return getHashData(key, hashKey, redisTemplate);
    }

    public Object getHashData(final K key, final Object hashKey, final RedisTemplate<K, V> template) {
        return template.opsForHash().get(key, hashKey);
    }

    public List<Object> multiGetHashData(final K key, final Collection<Object> hashKeys) {
        return multiGetHashData(key, hashKeys, redisTemplate);
    }

    public List<Object> multiGetHashData(final K key, final Collection<Object> hashKeys, final RedisTemplate<K, V> template) {
        return template.opsForHash().multiGet(key, hashKeys);
    }

    public Boolean hasHashData(final K key, final Object hasKey) {
        return hasHashData(key, hasKey);
    }

    public Boolean hasHashData(final K key, final Object hashKey, final RedisTemplate<K, V> template) {
        return template.opsForHash().hasKey(key, hashKey);
    }

    public Long delete(final K key, Object... hashKeys) {
        return delete(key, redisTemplate, hashKeys);
    }

    public Long delete(final K key, final RedisTemplate<K, V> template, Object... hashKeys) {
        return template.opsForHash().delete(key, hashKeys);
    }

}
