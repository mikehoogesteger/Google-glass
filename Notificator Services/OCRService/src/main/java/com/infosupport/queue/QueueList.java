package com.infosupport.queue;

import java.util.LinkedList;
import java.util.Queue;

public class QueueList {
	
	private static Queue<QueueItem> queue = new LinkedList<QueueItem>();
	
	public static void addToQueue(QueueItem queueItem) {
		queue.add(queueItem);
	}

	public static QueueItem retrieveFromQueue() {
		return queue.poll();
	}
	
	public static QueueItem retrieveFromQueueButDontDelete() {
		return queue.peek();
	}
	
	public static int sizeOfQueue() {
		return queue.size();
	}
}
