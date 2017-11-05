package sinks;

import pipes.IPipe;
import pipes.PipeClosedException;
import utils.Debugger;

public abstract class Sink<T> implements Runnable {

	protected final IPipe<T> readPipe;
	
	protected final Debugger debugger;

	protected Sink(IPipe<T> readPipe) {
		this.readPipe = readPipe;
		debugger = new Debugger(this.getClass().getName());
	}
	
	@Override
	public void run() {
		debugger.start();
		
		try {
			handleItem(readPipe);
		} catch (InterruptedException | PipeClosedException e) {
			e.printStackTrace();
		}
		
		debugger.stop();
		debugger.report();
	}

	protected abstract void handleItem(IPipe<T> read) throws InterruptedException, PipeClosedException;

}
