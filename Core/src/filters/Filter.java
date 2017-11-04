package filters;

import pipes.IPipe;
import pipes.PipeClosedException;

public abstract class Filter<T, S> implements Runnable{
	
	private IPipe<T> readPipe;
	private IPipe<S> writePipe;

	Filter(IPipe<T> readPipe, IPipe<S> writePipe) {
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

	protected abstract void filter(IPipe<T> readPipe, IPipe<S> writePipe) throws InterruptedException, PipeClosedException;
	
//	T item;
//	while ((item = readPipe.blockingRead()) != null) {
//		S newItem = filter(item);
//		writePipe.blockingWrite(newItem);
//	}
}
