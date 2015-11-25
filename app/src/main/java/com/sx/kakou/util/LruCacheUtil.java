package com.sx.kakou.util;

import android.util.LruCache;

/**
 * Created by mglory on 2015/11/12.
 */
public class LruCacheUtil {
    private LruCache<String,String> mjsonCache;

    public LruCacheUtil() {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        mjsonCache = new LruCache<>(maxMemory/8);
    }
    /**
     * 添加进入缓存列表
     *
     * @param key
     * @param value
     */
    public void addJsonLruCache(String key, String value) {
        mjsonCache.put(key, value);
    }

    /**
     * 从缓存列表中拿出来
     *
     * @param key
     * @return
     */
    public String getJsonLruCache(String key) {
        return mjsonCache.get(key);
    }

}
