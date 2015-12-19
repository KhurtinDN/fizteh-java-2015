package threads.roollcall;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


public class RollCall {

	public static boolean finish = false;
	public static List<Boolean> mutex = new ArrayList<>();
	
	public static class Roll implements Runnable {

		private final CyclicBarrier cyclicBarrier;
		private final int number;
		
		public Roll(CyclicBarrier cyclicBarrier, int number) {
			this.cyclicBarrier = cyclicBarrier;
			this.number = number;
		}

		@Override
		public void run() {
			while(true) {
				
				Random random = new Random();
				boolean t = random.nextFloat() < 0.8;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(finish)
					break;
				if(t) {
					System.out.println("Yes");
					mutex.set(number, true);
					
				} else {
					System.out.println("No");
					mutex.set(number, false);
				}
				try {
					cyclicBarrier.await();
				} catch (InterruptedException e) {
					System.out.println("Service one interrupted!");
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					System.out.println("Service one interrupted!");
					e.printStackTrace();
				}
				
			}
		}

	}
	
	public static void main(String[] args) {
		
		int n = 4;
		final CyclicBarrier barrier = new CyclicBarrier(n+1);
		List<Thread> threads = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			threads.add(new Thread(new Roll(barrier, i)));
			mutex.add(false);
		}

		System.out.println("Are you ready?");
		for (int i = 0; i <threads.size(); i++) {
			threads.get(i).start();
		}
		while(!finish) {
			
			try {
				barrier.await();
				if(!mutex.contains(false)) {
					finish = true;
					break;
				} else {
					System.out.println("Are you ready?");
				}
				
			} catch (InterruptedException e) {
				System.out.println("Main Thread interrupted!");
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				System.out.println("Main Thread interrupted!");
				e.printStackTrace();
			}
		}
	}
	
	
}
