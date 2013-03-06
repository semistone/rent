package org.siraya.rent.keystore.service;

public interface IKeystoreService {

	public void delete(String key);
	
	public void update(String key, String value);
	
	public String get(String key);
	
	public void insert(String key, String value);
}
