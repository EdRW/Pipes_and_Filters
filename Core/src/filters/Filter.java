package filters;

import pipes.IPipe;
import pipes.PipeClosedException;

public abstract class Filter<T, S> implements Runnable{
	
	protected final IPipe<T> readPipe;
	protected final IPipe<S> writePipe;

	protected Filter(IPipe<T> readPipe, IPipe<S> writePipe) {
		this.readPipe = readPipe;
		this.writePipe = writePipe;
	}

	@Override
	public void run() {
		try {
			filter(readPipe, writePipe);
			writePipe.blockingClose();
		} catch (InterruptedException | PipeClosedException e) {
			e.printStackTrace();
			writePipe.unsafeClose();
		}
	}

	protected abstract void filter(IPipe<T> read, IPipe<S> write) throws InterruptedException, PipeClosedException;
	
}
