import java.util.ArrayList;
import java.util.Queue;

class Main {
    
    private static abstract class Base {
        public void Print() {
            System.out.println("base!");
        }
        abstract void doNothing();
    }
    
    private Main() {}
    
    private static class Child extends Base {
        @Override
        public void Print() {
            super.Print();
            System.out.println("child!" + hashCode());
        }
        
        void doNothing() {
            // TODO Auto-generated method stub
            
        }
    }
	
	public static void main(String[] args) {
		for (int i = args.length - 1; i >= 0; --i) {
			String[] parsed = args[i].split("\\s");
			for (int j = parsed.length - 1; j >= 0; --j) {
				System.out.print(parsed[j] + " ");
			}
		}
		Base child = new Child();
		child.Print();
	}
}