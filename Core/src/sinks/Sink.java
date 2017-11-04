package sinks;

import pipes.IPipe;
import pipes.PipeClosedException;

public abstract class Sink<T> implements Runnable {

	private IPipe<T> readPipe;

	Sink(IPipe<T> readPipe) {
		this.readPipe = readPipe;
	}
	
	@Override
	public void run() {
		try {
			T item;
			while ((item = readPipe.blockingRead()) != null) {
				useItem(item);
			}
		} catch (InterruptedException | PipeClosedException e) {
			e.printStackTrace();
		}
	}

	protected abstract void useItem(T item);

}
