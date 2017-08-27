package com.six.lgnitedamo;

import java.util.Collection;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCluster;
import org.apache.ignite.IgniteCompute;
import org.apache.ignite.IgniteScheduler;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.GridKernalContext;
import org.apache.ignite.internal.GridKernalContextImpl;
import org.apache.ignite.internal.IgniteKernal;
import org.apache.ignite.internal.cluster.IgniteClusterImpl;
import org.apache.ignite.spi.collision.fifoqueue.FifoQueueCollisionSpi;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.DiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;

/**
* @author sixliu E-mail:359852326@qq.com
* @version 创建时间：2017年8月27日 下午1:03:08
* 类说明
*/
public class IgniteDemo {

	public static void main(String[] args) {
		IgniteConfiguration cfg = new IgniteConfiguration();
		cfg.setLifecycleBeans(new MyLifecycleBean());
		TcpCommunicationSpi tcpCommunicationSpi=new TcpCommunicationSpi();
		tcpCommunicationSpi.setSlowClientQueueLimit(1000);
		cfg.setCommunicationSpi(tcpCommunicationSpi);
		FifoQueueCollisionSpi fifoQueueCollisionSpi=new FifoQueueCollisionSpi();
		fifoQueueCollisionSpi.setParallelJobsNumber(1);
		cfg.setCollisionSpi(fifoQueueCollisionSpi);
		cfg.setLocalHost("127.0.0.1");
		cfg.setDaemon(true);
		DiscoverySpi discoverySpi=new TcpDiscoverySpi();
		cfg.setDiscoverySpi(discoverySpi);
		//Ignition.setClientMode(true);
		Ignite ignite = Ignition.start(cfg);
		IgniteCluster cluster =ignite.cluster();
		
		ClusterNode localNode =cluster.localNode();
		System.out.println("localNode:"+localNode);
		ClusterGroup workerGroup = cluster.forAttribute("ROLE", "worker");
		Collection<ClusterNode> workerNodes = workerGroup.nodes();
		System.out.println("workerNodes:"+workerNodes);
		ClusterGroup oldestNode = cluster.forOldest();
		System.out.println("oldestNode:"+oldestNode.nodes());
		ClusterGroup youngestNode = cluster.forYoungest();
		System.out.println("youngestNode:"+youngestNode.nodes());
		ignite.close();
	}

}
