/**
 * 
 */
package com.seda.data.procedure.result.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.seda.commons.cache.Cache;
import com.seda.commons.cache.CacheException;
import com.seda.commons.cache.CacheKey;

/**
 * @author f.ricci
 *
 */
public class NestedResultCache implements Cache {

	  private String id;
	  private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();;	 
	  private Map cache = new HashMap();

	  public NestedResultCache(String id) {
	    this.id = id;
	  }

	  public String getId() {
	    return id;
	  }

	  public int getSize() {
	    return cache.size();
	  }

	  public void putObject(Object key, Object value) {
	    cache.put(key, value);
	  }

	  public Object getObject(Object key) {
	    return cache.get(key);
	  }

	  public Object removeObject(Object key) {
	    return cache.remove(key);
	  }

	  public void clear() {
	    cache.clear();
	  }

	  public boolean equals(Object o) {
	    if (getId() == null) throw new CacheException("Cache instances require an ID.");
	    if (this == o) return true;
	    if (!(o instanceof Cache)) return false;

	    Cache otherCache = (Cache) o;
	    return getId().equals(otherCache.getId());
	  }

	  public int hashCode() {
	    if (getId() == null) throw new CacheException("Cache instances require an ID.");
	    return getId().hashCode();
	  }

	  public ReadWriteLock getReadWriteLock() {
		  return readWriteLock;
	  }
	
}
