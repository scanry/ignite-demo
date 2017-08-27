package com.six.lgnitedamo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.ignite.IgniteException;
import org.apache.ignite.compute.ComputeJob;
import org.apache.ignite.compute.ComputeJobResult;
import org.apache.ignite.compute.ComputeTaskSplitAdapter;
import org.apache.ignite.internal.GridTaskSessionImpl;
import org.apache.ignite.resources.TaskSessionResource;

/**
 * @author sixliu E-mail:359852326@qq.com
 * @version 创建时间：2017年8月27日 下午2:33:06 类说明
 */
public class MyUrgentTask extends ComputeTaskSplitAdapter<Object, Object> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 974729987674932543L;
	
	// Auto-injected task session.
	@TaskSessionResource
	private GridTaskSessionImpl taskSes = null;

	@Override
	protected Collection<ComputeJob> split(int gridSize, Object arg) {
		// Set high task priority.
		taskSes.setAttribute("grid.task.priority", 10);

		List<ComputeJob> jobs = new ArrayList<>(gridSize);
		// These jobs will be executed with higher priority.
		return jobs;
	}

	@Override
	public Object reduce(List<ComputeJobResult> results) throws IgniteException {
		return null;
	}
}
