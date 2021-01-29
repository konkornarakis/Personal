import java.io.File;
import java.util.Scanner;

public class stTest {

	public static void main(String[] args) {
		
		ST st = new ST(0);
		st.load(args[0]);
		st.print‘reeAlphabetically(System.out);
		System.out.println();
		st.print‘reeByFrequency(System.out);
		System.out.println();
		System.out.println(st.getMeanFrequency());
		System.out.println(st.getTotalWords());
		System.out.println(st.getDistinctWords());
	}
}
