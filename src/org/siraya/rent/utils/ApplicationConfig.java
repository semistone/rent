<<<<<<< HEAD
package org.siraya.rent.utils;
/**
 * load config file from config.yaml
 * @author angus_chen
 *
 */
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import java.util.Map;
import org.springframework.stereotype.Service;
@Service("applicationConfig")
public class ApplicationConfig implements IApplicationConfig{
    private static Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
    private static Map<String, Object>  data;
    
    
    public ApplicationConfig()throws Exception{
    	if (data == null) {
    		this.init();
    	}
    }
    
	private void init()throws Exception{
		logger.info("load config from config.yaml");
		InputStream input = ApplicationConfig.class.getResourceAsStream("/config.yaml");
        Yaml yaml = new Yaml();
        data = (Map<String, Object>)yaml.load(input);
        input.close();

	}
		
	public Map<String,Object> get(String key){
		return (Map<String,Object>)data.get(key);
	}
	
}
=======
package org.siraya.rent.utils;
/**
 * load config file from config.yaml
 * @author angus_chen
 *
 */
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import java.util.Map;
import org.springframework.stereotype.Service;
@Service("applicationConfig")
public class ApplicationConfig implements IApplicationConfig{
    private static Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
    private static Map<String, Object>  data;
    
    
    public ApplicationConfig()throws Exception{
    	if (data == null) {
    		this.init();
    	}
    }
    
	private void init()throws Exception{
		logger.info("load config from config.yaml");
		InputStream input = ApplicationConfig.class.getResourceAsStream("/config.yaml");
        Yaml yaml = new Yaml();
        data = (Map<String, Object>)yaml.load(input);
        input.close();

	}
		
	public Map<String,Object> get(String key){
		return (Map<String,Object>)data.get(key);
	}
	
}
>>>>>>> master
