/**
 * 
 */
package com.seda.data.dao;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Questa classe fornisce il supporto per l'aggiunta di attributi facoltativi al model
 * @author f.ricci
 * @deprecated usare com.seda.commons.data.AttributesModel. Nelle prossime release sarà eliminato
 *
 */
public class ModelAttributes implements Serializable {

	private static final long serialVersionUID = 1L;
	private Map<String, Serializable> attributes;

	/**
	 * Torna la mappa degli attributi 
	 * @return la mappa degli attributi
	 */
	public Map<String, Serializable> getAttributes(){
		//Lazy Instance
		if (attributes == null) {
			attributes = new HashMap<String, Serializable>();
		}	
		return attributes;
	}
	/**
	 * Assegna un nuovo attributo e torna, se presente, il valore di quello esistente
	 * con la stessa chiave 
	 * @param key la chiave attributo
	 * @param value il valore attributo
	 * @return il valore attributo precedente (null se nuovo)
	 */
	public Serializable setAttribute(String key, Serializable value){
		return getAttributes().put(key, value);
	}
	/**
	 * Assegna un nuovo map di attributi
	 */
	public void setAttributes(Map<String, Serializable> attributes){
		getAttributes().clear();
		getAttributes().putAll(attributes);
	}		
	/**
	 * Torna il valore dell'attributo identificato dalla chiave <code>key</code>
	 * Usa getAttribute(String key) invece di questo metodo.
	 * @param key la chiave attributo
	 * @return il valore dell'attributo
	 */
	@Deprecated
	public Serializable getAttributes(String key){
		return getAttributes().get(key);
	}
	/**
	 * Torna il valore dell'attributo identificato dalla chiave <code>key</code>
	 * @param key la chiave attributo
	 * @return il valore dell'attributo
	 */
	public Serializable getAttribute(String key){
		return getAttributes().get(key);
	}
	
	public String getString(String name) {
		return (String)getAttribute(name);
	}
	public void setString(String name, String value) {
		setAttribute(name,value);
	}
	
	public int getInt(String name) {
		return (Integer)getAttribute(name);
	}
	public void setInt(String name, Integer value) {
		setAttribute(name,value);
	}	
	
	/**
	 * Torna l'enumeratore con la lista delle chiave degli oggetti degli attributi aggiuntivi
	 * @return l'enumeratore con le chiave
	 */
	public Enumeration<String> getEnumKeys(){
		return Collections.enumeration(getAttributes().keySet());
	}

	protected String getAttributesToString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(" [ModelAttributes ");
		Enumeration<String> enumKeys = getEnumKeys();
		boolean first=true;
		while (enumKeys.hasMoreElements()) {
			String key = (String) enumKeys.nextElement();
			buffer.append(first?"[":" ,");
			first=false;
			buffer.append(key);
			buffer.append("={");
			buffer.append(getAttribute(key));
			buffer.append("}");				
		}
		buffer.append("]]");
		return buffer.toString();
	}
}
