package com.six.lgnitedamo;

import org.apache.ignite.lifecycle.LifecycleBean;
import org.apache.ignite.lifecycle.LifecycleEventType;

/**
 * @author sixliu E-mail:359852326@qq.com
 * @version 创建时间：2017年8月27日 下午2:01:39 类说明
 */
public class MyLifecycleBean implements LifecycleBean {
	
	
	@Override
	public void onLifecycleEvent(LifecycleEventType evt) {
		if (evt == LifecycleEventType.BEFORE_NODE_START) {
			System.out.println("do something before node start");
		}else if (evt == LifecycleEventType.AFTER_NODE_START) {
			System.out.println("do something after node start");
		}else if (evt == LifecycleEventType.BEFORE_NODE_STOP) {
			System.out.println("do something before node stop");
		}else if (evt == LifecycleEventType.AFTER_NODE_STOP) {
			System.out.println("do something after node stop");
		}
	}
}