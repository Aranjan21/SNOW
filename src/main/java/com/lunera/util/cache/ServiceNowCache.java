package com.lunera.util.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class ServiceNowCache {
	private Map<CacheKey, CacheValue> map = new ConcurrentHashMap<CacheKey, CacheValue>();

	public void put(CacheKey key, CacheValue value) {
		map.put(key, value);
	}

	public CacheValue get(CacheKey key) {
		return map.get(key);
	}
}
