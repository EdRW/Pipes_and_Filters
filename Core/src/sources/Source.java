package sources;

import pipes.IPipe;
import pipes.PipeClosedException;

public abstract class Source<T> implements Runnable {

	protected final IPipe<T> writePipe;

	protected Source(IPipe<T> writePipe) {
		this.writePipe = writePipe;
	}
	
	@Override
	public void run() {
		try {
			generateItems(writePipe);
			writePipe.blockingClose();
		}catch (InterruptedException | PipeClosedException e) {
			e.printStackTrace();
			writePipe.unsafeClose();
		}
		
	}
	
	protected abstract void generateItems(IPipe<T> write) throws InterruptedException, PipeClosedException;
}
