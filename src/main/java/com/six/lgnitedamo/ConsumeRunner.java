package com.six.lgnitedamo;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteQueue;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CollectionConfiguration;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**   
* @author liusong  
* @date   2017年8月28日 
* @email  359852326@qq.com 
*/
public class ConsumeRunner implements IgniteRunnable{

	final static Logger log = LoggerFactory.getLogger(ConsumeRunner.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -5464034968229118760L;
	@IgniteInstanceResource
    private Ignite ignite;
	IgniteQueue<String> igniteQueue;
	

	@Override
	public void run() {
		CollectionConfiguration queueCfg=new CollectionConfiguration();
		queueCfg.setAtomicityMode(CacheAtomicityMode.ATOMIC);
		queueCfg.setBackups(1);
		queueCfg.setCacheMode(CacheMode.REPLICATED);
		queueCfg.setCollocated(false);
		IgniteQueue<String> igniteQueue=ignite.queue("test-queue", 1000, queueCfg);
		log.info("Ignite start consume data");
		String value=null;
		while(!"end".equals(value=igniteQueue.poll())){
			if(null!=value){
				log.info("ConsumeRunner consume data:"+value);
			}else{
				try {
					log.info("no data to consume and will sleep");
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
			}
		}
		log.info("Ignite end consume data");
	}
}
