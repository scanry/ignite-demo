package com.six.lgnitedamo;

import java.util.ArrayList;
import java.util.List;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.collision.fifoqueue.FifoQueueCollisionSpi;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liusong
 * @date 2017年8月28日
 * @email 359852326@qq.com
 */
public class Launcher {

	final static Logger log = LoggerFactory.getLogger(Launcher.class);
	private static String DEFAULT_LOCAL_HOST = "127.0.0.1";
	private static int DEFAULT_LOCAL_PORT = 8881;
	private static Ignite ignite;

	public static void main(String[] args) {
		String localHost = Config.INSTANCE.getProperty("local.host", DEFAULT_LOCAL_HOST);
		int localPort = Config.INSTANCE.getPropertyInt("local.port",DEFAULT_LOCAL_PORT);
		IgniteConfiguration cfg = new IgniteConfiguration();
		/** 设置节点启动关闭事件 **/
		cfg.setLifecycleBeans(new NodeLifecycleBean());
		/**
		 * Set to true to enable distributed class loading for examples, default
		 * is false.
		 **/
		cfg.setPeerClassLoadingEnabled(true);
		/** Enable active on Start **/
		cfg.setActiveOnStart(true);
		/** Enable cache events for examples. **/
		cfg.setIncludeEventTypes(org.apache.ignite.events.EventType.EVTS_CACHE);
//		/** 缓存配置 **/
//		CacheConfiguration<Integer, String> cacheCfg = new CacheConfiguration<Integer, String>();
//		cacheCfg.setName("myCache");
//		cacheCfg.setCacheMode(CacheMode.PARTITIONED);
//		cacheCfg.setAtomicityMode(CacheAtomicityMode.ATOMIC);
//		cacheCfg.setBackups(1);
//		cfg.setCacheConfiguration(cacheCfg);
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
		/** 设置tpc节点发现 **/
		TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
		discoverySpi.setLocalAddress(localHost);
		discoverySpi.setLocalPort(localPort);
		TcpDiscoveryVmIpFinder tcpDiscoveryVmIpFinder = new TcpDiscoveryVmIpFinder();
		tcpDiscoveryVmIpFinder.setShared(true);
		String nodeStrs = Config.INSTANCE.getProperty("cluster.node.addresses");
		if (null != nodeStrs && nodeStrs.trim().length() > 0) {
			String[] nodes = nodeStrs.split(";");
			List<String> nodeList = new ArrayList<>();
			for (String node : nodes) {
				nodeList.add(node);
			}
			tcpDiscoveryVmIpFinder.setAddresses(nodeList);
		}
		discoverySpi.setIpFinder(tcpDiscoveryVmIpFinder);
		cfg.setDiscoverySpi(discoverySpi);
		ignite = Ignition.getOrStart(cfg);
		/** 增加关闭钩子 **/
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			ignite.close();
		}));

	}

}
