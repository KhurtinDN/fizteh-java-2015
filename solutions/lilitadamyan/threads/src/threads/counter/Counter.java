package threads.counter;

import java.util.ArrayList;
import java.util.List;

public class Counter {
	public static List<Boolean> mutex = new ArrayList<>();
	
	public static class Helper implements Runnable {

		private final int number;
		
		public Helper(int number) {
			this.number = number;
		}

		@Override
		public void run() {
			while(true) {
				if(!mutex.get(number-1)) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					System.out.println("thread number = " + number);
					mutex.set(number-1, false);
					if(number == mutex.size()) {
						mutex.set(0, true);
					} else 
						mutex.set(number, true);
				}
			}
		}

	}
	
	public static void main(String[] args) {
		
		int n = Integer.parseInt(args[0]);
		List<Thread> threads = new ArrayList<>();
		mutex.add(true);
		for (int i = 1; i <n; i++) {
			threads.add(new Thread(new Helper(i)));
			mutex.add(false);
		}
		threads.add(new Thread(new Helper(n)));
		
		for (int i = 0; i <threads.size(); i++) {
			threads.get(i).start();
		}
	}
}
