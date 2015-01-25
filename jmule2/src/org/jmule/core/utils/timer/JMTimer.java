/*
 *  JMule - Java file sharing client
 *  Copyright (C) 2007-2009 JMule team ( jmule@jmule.org / http://jmule.org )
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
package org.jmule.core.utils.timer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created on Aug 28, 2009
 * @author binary256
 * @version $Revision: 1.6 $
 * Last changed by $Author: binary255 $ on $Date: 2010/09/04 16:17:38 $
 */
public class JMTimer {
	private Queue<TaskExecutor> taskList = new ConcurrentLinkedQueue<TaskExecutor>();
	
	public JMTimer() {
	}
	
	public void addTask(JMTimerTask task, long waitTime, boolean repeat) {
		TaskExecutor executor = new TaskExecutor(waitTime, task, repeat);
		taskList.add(executor);
		executor.start();	
	}
	
	public void addTask(JMTimerTask task, long waitTime) {
		task.resetStopTask();
		addTask(task, waitTime ,false);
	}
	
	private void removeTask(TaskExecutor executor) {
		taskList.remove(executor);
	}
	
	public void removeTask(JMTimerTask removeTask) {
		for(TaskExecutor task_executor : taskList) 
			if (task_executor.getTask().equals(removeTask)) {
				task_executor.stopTask();
				taskList.remove(task_executor);
				return ;
			}
	}
	
	public void cancelAllTasks() {
		for(TaskExecutor task_executor : taskList) {
			task_executor.stopTask();
		}
		taskList.clear();
	}
	
	
	private class TaskExecutor extends Thread {
		private boolean stop = false;
		private long waitTime;
		private boolean repeatTask = false;
		
		private JMTimerTask task;
		
		public TaskExecutor(long waitTime, JMTimerTask task, boolean repeatTask) {
			super("Task executor " +task);
			this.waitTime = waitTime;
			this.task = task;
			this.repeatTask = repeatTask;
		}
		
		public void run() {
			task.setRunning(true);
			do {
				if (task.mustStopTask() || stop)
					break;
				try {
					synchronized (this) {
						this.wait(waitTime);
					}
				} catch (InterruptedException e) {
					if (stop) {
						break;
					}
				}

				if (task.mustStopTask() || stop) 
					break;

				try {
					task.run();
				} catch (Throwable t) {
					t.printStackTrace();
				}

			} while (repeatTask);
			task.setRunning(false);
			removeTask(this);
		}

		public JMTimerTask getTask() {
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
