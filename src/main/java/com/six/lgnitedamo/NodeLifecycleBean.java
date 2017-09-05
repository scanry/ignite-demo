package com.six.lgnitedamo;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCluster;
import org.apache.ignite.IgniteLock;
import org.apache.ignite.events.EventType;
import org.apache.ignite.lifecycle.LifecycleBean;
import org.apache.ignite.lifecycle.LifecycleEventType;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sixliu E-mail:359852326@qq.com
 * @version 创建时间：2017年8月27日 下午2:01:39 类说明
 */
public class NodeLifecycleBean implements LifecycleBean {

	final static Logger log = LoggerFactory.getLogger(NodeLifecycleBean.class);

	@IgniteInstanceResource
	private Ignite ignite;

	@Override
	public void onLifecycleEvent(LifecycleEventType evt) {
		if (evt == LifecycleEventType.BEFORE_NODE_START) {
			log.info("do something before node start");
		} else if (evt == LifecycleEventType.AFTER_NODE_START) {
			IgniteCluster cluster = ignite.cluster();
			/**基于节点事件**/
			ignite.events().remoteListen((UUID,event)->{
				return false;
			}, event->{
				return false;
			}, EventType.EVT_NODE_FAILED);
			/**基于reentrantLock**/
			ignite.executorService(cluster.forLocal()).execute(() -> {
				IgniteLock lock = ignite.reentrantLock("schedule", true, true, true);
				lock.lock();
				try {
					// scheduler 实现定时任务的加载和调度。
					Scheduler scheduler = new Scheduler();
					scheduler.start();
					// 注意这里一直阻塞，除非节点挂掉
				} finally {
					lock.unlock();
				}
			});
		} else if (evt == LifecycleEventType.BEFORE_NODE_STOP) {
			log.info("do something before node stop");
		} else if (evt == LifecycleEventType.AFTER_NODE_STOP) {
			log.info("do something after node stop");
		}
	}

}