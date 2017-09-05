package com.six.lgnitedamo;

import java.io.FileInputStream;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author liusong
 * @date 2017年8月28日
 * @email 359852326@qq.com
 */
public class Config {

	private Properties config = new Properties();

	public static Config INSTANCE = new Config();

	private Config() {
		init();
	}

	private void init() {
		try {
			Resource resource = new ClassPathResource("/config.properties");
			config.load(new FileInputStream(resource.getFile()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String getProperty(String key) {
		return config.getProperty(key);
	}

	public String getProperty(String key, String defaultValue) {
		return config.getProperty(key, defaultValue);
	}

	public int getPropertyInt(String key, int defaultValue) {
		String value = config.getProperty(key);
		return null != value ? Integer.valueOf(value) : defaultValue;
	}

}
