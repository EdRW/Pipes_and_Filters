package pipes;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * this pipe uses a blocking queue with a fixed buffer size
 * the items in the pipe are strings.
 * the POISON_PILL is put into the pipe as the last items 
 * to notify the filter reading from it that there will be no more items to read.
 */
public class StringPipe implements IPipe<String>{
	private static final String POISON_PILL = "SAFE PIPE SHUTDOWN";
	
	LinkedBlockingQueue<String> queue = null;
	AtomicBoolean writable;
	
	public StringPipe(int size) {
		this.queue =  new LinkedBlockingQueue<>(size);
		this.writable = new AtomicBoolean(true);
	}
	
	@Override
	public String blockingRead() throws InterruptedException, PipeClosedException {
		if (false) {//writable.get() == false && queue.isEmpty()) { Couldn't get this to work for the redesign version
			throw new PipeClosedException("There are no items to read and Pipe is closed");
		}
		String returnVal = queue.take();
		if (returnVal.equals(POISON_PILL)) {
			returnVal = null;
		}
		return returnVal;
	}
	
	@Override
	public void blockingWrite(String msg) throws InterruptedException, PipeClosedException {
		if (writable.get() == false) {
			throw new PipeClosedException("Cannot write item: " + msg + ". Pipe is closed");
		}
		queue.put(msg);
	}
	
	@Override
	public void blockingClose() throws InterruptedException {
		queue.put(POISON_PILL);
		writable.set(false);
	}

	@Override
	public void unsafeClose() {
		queue.clear();
		queue.offer(POISON_PILL);
		writable.set(false);
	}

	@Override
	public int remainingCapacity() {
		return queue.remainingCapacity();
	}

	@Override
	public int size() {
		return queue.size();
	}
	
	

}
