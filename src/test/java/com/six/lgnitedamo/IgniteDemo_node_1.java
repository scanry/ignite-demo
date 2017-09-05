package com.six.lgnitedamo;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCluster;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.collision.fifoqueue.FifoQueueCollisionSpi;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;


/**
 * @author sixliu E-mail:359852326@qq.com
 * @version 创建时间：2017年8月27日 下午1:03:08 类说明
 */
public class IgniteDemo_node_1 {

	static String localHost="127.0.0.1";
	
	public static void main(String[] args) {
		IgniteConfiguration cfg = new IgniteConfiguration();
		/** 设置节点启动关闭事件 **/
		cfg.setLifecycleBeans(new NodeLifecycleBean());
		/**
		 * Set to true to enable distributed class loading for examples, default
		 * is false.
		 **/
		cfg.setPeerClassLoadingEnabled(true);
		/** Enable active on Start**/
		cfg.setActiveOnStart(true);
		/** Enable cache events for examples. **/
		cfg.setIncludeEventTypes(org.apache.ignite.events.EventType.EVTS_CACHE);
		/** 缓存配置 **/
		CacheConfiguration<Integer, String> cacheCfg = new CacheConfiguration<Integer, String>();
		cacheCfg.setName("myCache");
		cacheCfg.setCacheMode(CacheMode.PARTITIONED);
		cacheCfg.setAtomicityMode(CacheAtomicityMode.ATOMIC);
		cacheCfg.setBackups(1);
		cfg.setCacheConfiguration(cacheCfg);
		/** TcpCommunicationSpi **/
		TcpCommunicationSpi tcpCommunicationSpi = new TcpCommunicationSpi();
		tcpCommunicationSpi.setSlowClientQueueLimit(1000);
		cfg.setCommunicationSpi(tcpCommunicationSpi);
		/** FifoQueueCollisionSpi **/
		FifoQueueCollisionSpi fifoQueueCollisionSpi = new FifoQueueCollisionSpi();
		fifoQueueCollisionSpi.setParallelJobsNumber(1);
		cfg.setCollisionSpi(fifoQueueCollisionSpi);
		/** 设置本地ip **/
		cfg.setLocalHost(localHost);
		/**setDaemon**/
		//cfg.setDaemon(true);
		/**设置tpc节点发现**/
		TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
		discoverySpi.setLocalAddress(localHost);
		discoverySpi.setLocalPort(8081);
		TcpDiscoveryVmIpFinder tcpDiscoveryVmIpFinder=new TcpDiscoveryVmIpFinder();
		tcpDiscoveryVmIpFinder.setShared(true);
		tcpDiscoveryVmIpFinder.setAddresses(Arrays.asList(localHost+":8081",localHost+":8082"));
//		TcpDiscoveryZookeeperIpFinder tcpDiscoveryVmIpFinder=new TcpDiscoveryZookeeperIpFinder();
//		tcpDiscoveryVmIpFinder.setZkConnectionString("127.0.0.1:2181");
		discoverySpi.setIpFinder(tcpDiscoveryVmIpFinder);
		cfg.setDiscoverySpi(discoverySpi);
		/** 设置工作目录 **/
		String igniteWorkDir = "/Users/liusong/git/ignite-demo/workdir";
		cfg.setWorkDirectory(igniteWorkDir);
		
		Ignite ignite = Ignition.getOrStart(cfg);
		
		IgniteCluster cluster = ignite.cluster();
		ClusterNode localNode = cluster.localNode();
		System.out.println("localNode:" + localNode);
		ClusterGroup serverGroup = cluster.forServers();
		System.out.println("serverGroup:" + serverGroup.nodes());
		ClusterGroup oldestNode = cluster.forOldest();
		System.out.println("oldestNode:" + oldestNode.nodes());
		ClusterGroup youngestNode = cluster.forYoungest();
		System.out.println("youngestNode:" + youngestNode.nodes());
		ExecutorService executorService =ignite.executorService();
		executorService.execute(new ConsumeRunner());
		/** 增加关闭钩子 **/
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			ignite.close();
		}));
	}

}
