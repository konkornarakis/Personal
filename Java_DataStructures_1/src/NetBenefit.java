import java.io.*;
import java.util.Scanner;

public class NetBenefit {

	public static void main(String[] args) {
		if (args.length == 0) {	// checks for argument
			System.out.println("Proper Usage is: java NetBenefit filename");
			System.exit(-1);
		}

		File file = new File(args[0]);
		IntQueueImpl stockqueue = new IntQueueImpl();	//create queue for stocks
		IntQueueImpl pricequeue = new IntQueueImpl();	//create queue for prices
		
		try{
			Scanner input = new Scanner(file);
			String keyword;
			int stocks, price;	//values for stocks being sold
			int benefit = 0, remain = 0;
			boolean ranout = false;	//true if trying to sell with no stocks remaining
			
			
			while (input.hasNext()) {
				
				keyword=input.next();
				
				if (keyword.equals("buy")) {
					keyword = input.next();
					stockqueue.put(Integer.parseInt(keyword));	//put quantity of stocks in queue
					keyword = input.next();
					keyword = input.next();
					pricequeue.put(Integer.parseInt(keyword));	//put price of stocks in queue
				}
				
				if (keyword.equals("sell")) {
					keyword = input.next();
					stocks = Integer.parseInt(keyword);
					keyword = input.next();
					keyword = input.next();
					price = Integer.parseInt(keyword);
					
					while (stocks > 0) {
						if (stockqueue.isEmpty()) {	//attempt to sell with no stocks remaining
							System.out.println("Ran out of stocks.");
							ranout = true;
							break;
						}
						
						if ((int) stockqueue.peek() - remain <= stocks) {	//calculate benefit
							stocks -= (int) stockqueue.peek() - remain;
							benefit += ((int) stockqueue.get() - remain)*(price - (int) pricequeue.get());	
							
						} else {
							remain = (int) stockqueue.peek() - stocks;
							benefit += stocks*(price - (int) pricequeue.peek());
							stocks = 0;
						}
						
					}
					 
				}
			}
			
			if(!ranout){
				System.out.println(benefit);
			}
			input.close();
		} catch (IOException ioe) {
			System.err.print("IOException");
		}
	}
}
