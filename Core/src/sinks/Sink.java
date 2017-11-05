package sinks;

import pipes.IPipe;
import pipes.PipeClosedException;

public abstract class Sink<T> implements Runnable {

	protected IPipe<T> readPipe;

	protected Sink(IPipe<T> readPipe) {
		this.readPipe = readPipe;
	}
	
	@Override
	public void run() {
		try {
			handleItem(readPipe);
		} catch (InterruptedException | PipeClosedException e) {
			e.printStackTrace();
		}
	}

	protected abstract void handleItem(IPipe<T> read) throws InterruptedException, PipeClosedException;

}
