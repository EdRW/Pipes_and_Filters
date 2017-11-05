package utils;

import java.util.ArrayList;

public class Debugger {
	//private static Debuger pInstance = new Debuger(); 
	
	private static Boolean logging =  false;
	private static Boolean performance = false;
	
	private String name;
	private Long startTime;
	private Long stopTime;
	private Long lastTick;
	private ArrayList<Long> cycleDurations = null;

	public Debugger() {
	}
	
	public Debugger(String name) {
		this.name = name;
	}
	
	public void start() {
		startTime = System.currentTimeMillis();
		lastTick = System.nanoTime();
	}
	
	public void stop() {
		stopTime = System.currentTimeMillis();
	}
	
	public Long duration() {
		return stopTime - startTime;
	}
	
	public Long avgDuration() {
		if (cycleDurations == null) return null;
		return cycleDurations.stream().mapToLong(Long::longValue).sum() / cycleDurations.size();
	}
	
	public void tick() {
		lastTick = System.nanoTime();
	}
	
	public void tock() {
		if (cycleDurations == null) cycleDurations = new ArrayList<Long>();
		Long currentTock = System.nanoTime();
		cycleDurations.add(currentTock - lastTick);
	}
	
	public void report() {
		if (performance) System.out.println("Process " + name + " Runtime: " + (stopTime - startTime) + "ms. Average response time: " + avgDuration() + "ns.");
	}
	
	public static void setLoggingStatus(Boolean status) {
		 logging = status;
	}

	
	public static Boolean loggingStatus() {
		return logging;
	}

	
	public static void setProfilerStatus(Boolean status) {
		performance = status;
	}
	
	public static Boolean performanceStatus() {
		return performance;
	}
}
