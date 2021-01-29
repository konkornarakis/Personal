import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Exercise2 {
	private int[] unorderedTable;

	public Exercise2(ArrayList<Integer> A) {
		unorderedTable = new int[A.size()];
		for (int i = 0; i < unorderedTable.length; i++)
			unorderedTable[i] = A.get(i);
	}

	public int[] getAnswers() {
		QuickSort ob = new QuickSort();
		ob.sort(unorderedTable, 0, unorderedTable.length - 1);
		//ob.printArray(unorderedTable);
		return unorderedTable;
	}

	static class QuickSort {

		

		static void swap(int[] arr, int i, int j) {
			int temp = arr[i];
			arr[i] = arr[j];
			arr[j] = temp;
		}

		static void sort(int[] arr, int lowIndex, int highIndex) {

			if (highIndex <= lowIndex) {
				return;
			}
			int randomNum = ThreadLocalRandom.current().nextInt(lowIndex, highIndex + 1);

			swap(arr, randomNum, highIndex);
			
			int start = lowIndex;
			int end = highIndex;
			int i = lowIndex;
			int pivot = arr[highIndex];

			while (i <= end) {
				if (arr[i] < pivot) {
					swap(arr, i, start);
					i++;
					start++;
				} else if (arr[i] > pivot) {
					swap(arr, i, end);
					end--;
				} else {
					i++;
				}
				
			}

			sort(arr, lowIndex, start - 1);
			sort(arr, end + 1, highIndex);
		}

		static void printArray(int[] arr) {
			int n = arr.length;
			for (int i = 0; i < n; ++i)
				System.out.print(arr[i] + " ");
			System.out.println();
		}
	}

}
