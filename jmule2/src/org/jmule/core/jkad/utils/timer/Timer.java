/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule Team ( jmule@jmule.org / http://jmule.org )
 *
 *  Any parts of this program derived from other projects, or contributed
 *  by third-party developers are copyrighted by their respective authors.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package org.jmule.core.jkad.utils.timer;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created on Jan 8, 2009
 * @author binary256
 * @version $Revision: 1.5 $
 * Last changed by $Author: binary255 $ on $Date: 2010/09/04 16:15:07 $
 */
public class Timer {
	
	private Collection<TaskExecutor> taskList = new ConcurrentLinkedQueue<TaskExecutor>();

	private static class TimerSingletonHolder {
		private static final Timer INSTANCE = new Timer();
	}
	
	public static Timer getSingleton() {
		return TimerSingletonHolder.INSTANCE;
	}
	
	private Timer() {
	}
	
	public void stop() {
		for(TaskExecutor task : taskList)
			task.stopTask();
		taskList.clear();
	}
	
	public void addTask(long waitTime, Task task, boolean repeat) {
		TaskExecutor executor = new TaskExecutor(waitTime, task, repeat);
		taskList.add(executor);
		executor.start();	
	}
	
	public void addTask(long waitTime, Task task) {
		addTask(waitTime, task ,false);
	}
	
	private void removeTask(TaskExecutor executor) {
		taskList.remove(executor);
	}
	
	public void removeTask(Task removeTask) {
		for(TaskExecutor task_executor : taskList) 
			if (task_executor.getTask().equals(removeTask)) {
				task_executor.stopTask();
				return ;
			}
	}
	
	private class TaskExecutor extends Thread {
		private boolean stop = false;
		private long waitTime;
		private boolean repeatTask = false;
		
		private Task task;
		
		public TaskExecutor(long waitTime, Task task, boolean repeatTask) {
			super("Task executor " +task);
			this.waitTime = waitTime;
			this.task = task;
			this.repeatTask = repeatTask;
		}
		
		public void run() {
			while(repeatTask) {
				if (task.mustStopTask() || stop) break;
				try {
					synchronized (this) {
						this.wait(waitTime);
					}
				} catch (InterruptedException e) {
					if (stop) { break;  }
				}
				
				if (task.mustStopTask() || stop) break;
				
				try {
					task.run();
				}catch(Throwable t) { t.printStackTrace(); }
				
			}
			removeTask(this);
		}
		
		public Task getTask() {
			return task;
		}
		
		public void stopTask() {
			stop = true;
			synchronized (this) {
				this.notify();
			}
		}
	}
	
}
