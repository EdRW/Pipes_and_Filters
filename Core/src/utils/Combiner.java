package utils;

import pipes.IPipe;
import pipes.PipeClosedException;

public class Combiner<T> implements Runnable {
	
	protected final IPipe<T> readPipe;
	protected final IPipe<T> writePipe;
	
	protected int numInBranches;
	
	protected final Debugger debugger;

	public Combiner(IPipe<T> readPipe, IPipe<T> writePipe, int numInBranches) {
		this.readPipe = readPipe;
		this.writePipe = writePipe;
		this.numInBranches = numInBranches;
		debugger = new Debugger(this.getClass().getName());
	}

	@Override
	public void run() {
		debugger.start();
		int nullCount = 0;
		try {
			T item;
			
			while (nullCount < numInBranches) {
				if ((item = readPipe.blockingRead()) == null){
					nullCount++;
				}
				else{
					writePipe.blockingWrite(item);
				}
			}
			writePipe.blockingClose();
		}catch (InterruptedException | PipeClosedException e) {
			e.printStackTrace();
			writePipe.unsafeClose();
		}
		
		debugger.stop();
		debugger.report();	

	}

}
