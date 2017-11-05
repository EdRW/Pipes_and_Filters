package pipes;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class StrIntHashMapPipe implements IPipe<HashMap<String, Integer>> {
	private static final HashMap<String, Integer> POISON_PILL = new HashMap<String, Integer>();
	
	LinkedBlockingQueue<HashMap<String, Integer>> queue = null;
	AtomicBoolean writable;

	public StrIntHashMapPipe(int size) {
		this.queue =  new LinkedBlockingQueue<>(size);
		this.writable = new AtomicBoolean(true);
	}

	@Override
	public HashMap<String, Integer> blockingRead() throws InterruptedException, PipeClosedException {
		if (writable.get() == false && queue.isEmpty()) {
			throw new PipeClosedException("There are no items to read and Pipe is closed");
		}
		HashMap<String, Integer> returnVal = queue.take();
		if (returnVal == POISON_PILL) {
			returnVal = null;
		}
		return returnVal;
	}

	@Override
	public void blockingWrite(HashMap<String, Integer> data) throws InterruptedException, PipeClosedException {
		if (writable.get() == false) {
			throw new PipeClosedException("Cannot write item: " + data + ". Pipe is closed");
		}
		queue.put(data);
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
