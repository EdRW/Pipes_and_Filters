package filters;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import pipes.IPipe;
import pipes.PipeClosedException;
import utils.Debugger;

public class StopWordFilter extends Filter<String, String> {
	
	private Scanner fileScanner;
	private Set<String> stopWords;

	public StopWordFilter(IPipe<String> readPipe, IPipe<String> writePipe, String fileName) throws FileNotFoundException {
		super(readPipe, writePipe);
		this.fileScanner = new Scanner(new File(fileName));
		
		this.stopWords = new HashSet<>();
		
		while (fileScanner.hasNextLine()) {
			String stopWord = fileScanner.nextLine();
			this.stopWords.add(stopWord);
		}
	}

	@Override
	protected void filter(IPipe<String> read, IPipe<String> write) throws InterruptedException, PipeClosedException {
		String word;
		while ((word = readPipe.blockingRead()) != null) {
			debugger.tick();
			
			if (!stopWords.contains(word)) {
				writePipe.blockingWrite(word);
				
				if (Debugger.loggingStatus()) System.out.println("StopWordFilter: " + word);
			}
			
			debugger.tock();
		}
		
	}

}
