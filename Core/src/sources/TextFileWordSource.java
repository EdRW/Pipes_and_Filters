package sources;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import pipes.IPipe;
import pipes.PipeClosedException;
import utils.Debugger;

public class TextFileWordSource extends Source<String> {
	Scanner fileScanner;
	
	public TextFileWordSource(IPipe<String> writePipe, String fileName) throws FileNotFoundException{
		super(writePipe);
		this.fileScanner = new Scanner(new File(fileName));
	}

	@Override
	protected void generateItems(IPipe<String> write) throws InterruptedException, PipeClosedException {
		String line;
		Scanner wordScanner = null;
		try {
			debugger.tick();
			while (fileScanner.hasNextLine()) {
				line = fileScanner.nextLine();
				
				wordScanner = new Scanner(line);
				wordScanner.useDelimiter("[^A-Za-z0-9]+");
				while(wordScanner.hasNext()) {
					String word = wordScanner.next();
					//System.out.println("attempting to write " + word + "to pipe with remaining capacity: " + writePipe.remainingCapacity());
					writePipe.blockingWrite(word);
					
					debugger.tock();
					debugger.tick();
					if (Debugger.loggingStatus()) System.out.println("Source: " + word);
				}
				
				wordScanner.close();
			}
			
			fileScanner.close();
		} catch (InterruptedException | PipeClosedException e) {
			wordScanner.close();
			fileScanner.close();
		}
	}
}
