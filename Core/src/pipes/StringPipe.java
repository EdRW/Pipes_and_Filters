package pipes;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

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
		if (writable.get() == false && queue.isEmpty()) {
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
