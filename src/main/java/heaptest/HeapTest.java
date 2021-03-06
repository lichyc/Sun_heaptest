/*
 * 00/08/01 @(#)HeapTest.java   1.3
 *
 * Copyright (c) 2000 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to
use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all
copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 */

package heaptest;

import java.io.FileOutputStream;
import java.io.PrintStream;

public class HeapTest {

	public static void main(String[] args) {

		int k_numIterations = 5000;
		int k_numNodesToAlloc = 30000;
		int k_numDataPoints = 50000;
		int k_numThreadCreateFactor = 10;

		int maxNumThreads = 0;
		int numThreads = 0;
		int totalCycles = 0;
		int heapCycles = 0;
		int cpuCycles = 0;
		int i = 0;
		Integer[] threadNumArray = new Integer[]{1,1,5,10,25,50,100,150,250,500,1000};

		try {

			if (args.length < 2)
				usage();
			PrintStream logFile = null;
			try {
				logFile = new PrintStream(new FileOutputStream(args[2]));
			} catch (Exception e) {
				System.out
						.println("Unable to open log file. Printing to System.out..");
				logFile = System.out;
			}

			try {
				maxNumThreads = Integer.parseInt(args[0]);
				totalCycles = Integer.parseInt(args[1]);
			} catch (Exception e) {
			}

			if (maxNumThreads == 0 || totalCycles == 0)
				usage();
			else {
				if (maxNumThreads > threadNumArray.length) {
				logFile.println("\nMax # threads =      " + (maxNumThreads * k_numThreadCreateFactor)
						+ "\n" + "Total (heap + CPU) cycles = " + totalCycles
						+ "\n\n");
				} else {
					logFile.println("\nMax # threads =      " + threadNumArray[maxNumThreads]
							+ "\n" + "Total (heap + CPU) cycles = " + totalCycles
							+ "\n\n");
				}
			}

			HeapThread[] threads = new HeapThread[maxNumThreads * k_numThreadCreateFactor];
			logFile.print("# Threads");
			for (cpuCycles = 0, heapCycles = totalCycles; cpuCycles <= totalCycles; cpuCycles++, heapCycles--) {
				logFile.print("\t" + heapCycles + " Heap Cycles, " + cpuCycles
						+ " CPU Cycles");
			}
			logFile.flush();

			for (numThreads = 1; numThreads <= maxNumThreads; numThreads++) {
				int numWorkThread = 1;
				if (maxNumThreads > threadNumArray.length) {
					numWorkThread = numThreads * k_numThreadCreateFactor;
				} else {
					numWorkThread = threadNumArray[numThreads];
				}
				logFile.print("\n\n" + numWorkThread);
				for (cpuCycles = 0, heapCycles = totalCycles; cpuCycles <= totalCycles; cpuCycles++, heapCycles--) {

					Barrier goFlag = new Barrier(numWorkThread);
					for (i = 0; i < numWorkThread; i++) {
						threads[i] = new HeapThread(i, numWorkThread,
								new int[k_numDataPoints], k_numDataPoints,
								k_numIterations, k_numNodesToAlloc, heapCycles,
								cpuCycles, goFlag);
					}

					long elapsedTime = System.currentTimeMillis();
					for (i = 0; i < numWorkThread; i++) {
						threads[i].start();
					}

					for (i = 0; i < numWorkThread; i++) {
						threads[i].join();
					}
					elapsedTime = System.currentTimeMillis() - elapsedTime;
					logFile.print("\t" + elapsedTime + "ms ");
				}
			}
			logFile.flush();
		} catch (Exception e) {
			System.out.println("Caught exception!!");
			e.printStackTrace();
		}

	}

	public static void usage() {
		System.out
				.println("java -jar developer.JVMPerf-0.0.1-SNAPSHOT.jar <numThreads/10> <num of (CPU + Heap) cycles> [log file name]");

		System.out
				.println("see also: http://java.sun.com/developer/technicalArticles/Programming/JVMPerf/");
		System.out
				.println("Note: Load increased by factor 10 up to SUN original to meet todays systems capabilities.");

		System.exit(1);
	}
}
