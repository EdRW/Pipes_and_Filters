package utils;

import pipes.IPipe;
import pipes.PipeClosedException;

public class Splitter<T> implements Runnable {

	protected final IPipe<T> readPipe;
	protected final IPipe<T> writePipe;
	
	protected int numOutBranches;
	
	protected final Debugger debugger;
	
	public Splitter(IPipe<T> readPipe, IPipe<T> writePipe, int numOutBranches) {
		this.readPipe = readPipe;
		this.writePipe = writePipe;
		this.numOutBranches = numOutBranches;
		debugger = new Debugger(this.getClass().getName());
	}

	@Override
	public void run() {
		debugger.start();
		try {
			T item;
			while ((item = readPipe.blockingRead()) != null) {
				writePipe.blockingWrite(item);
			}

			for (int i = 0;  i < numOutBranches; i++) {
				writePipe.blockingClose();
			}
		}catch (InterruptedException | PipeClosedException e) {
			e.printStackTrace();
			writePipe.unsafeClose();
		}
		
		debugger.stop();
		debugger.report();	
	}

}
