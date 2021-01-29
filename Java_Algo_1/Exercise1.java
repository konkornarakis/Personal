import java.util.ArrayList;

public class Exercise1 {

	private int x;
	private ArrayList<Integer> A;

	public Exercise1(int x, ArrayList<Integer> A) {
		this.x = x;
		this.A = A;
	}

	public int[] getAnswers() {

		BinarySearch bs = new BinarySearch();
		int posF = bs.binarySearchFirst(A, 0, A.size() - 1, x);
		int posL = bs.binarySearchLast(A, 0, A.size() - 1, x);

		int[] ans = {posF, posL};
		
		return ans;
	}
	
	static class BinarySearch {

		int binarySearchFirst(ArrayList<Integer> array, int l, int r, int key) {
			if (r >= l) {
				int mid = l + (r - l) / 2;

				if ((mid == 0 || key > array.get(mid - 1)) && array.get(mid) == key) {
					return mid;
				} else if (key > array.get(mid)) {
					return binarySearchFirst(array, (mid + 1), r, key);
				} else {
					return binarySearchFirst(array, l, (mid - 1), key);
				}
			}
			return -1;
		}

		int binarySearchLast(ArrayList<Integer> array, int l, int r, int key) {
			if (r >= l) {
				int mid = l + (r - l) / 2;

				if (array.get(mid) == key && array.get(mid + 1) > key) {
					return mid;
				} else if (array.get(mid) > key) {
					return binarySearchLast(array, l, (mid - 1), key);
				} else {
					return binarySearchLast(array, (mid + 1), r, key);
				}
			}

			return -1;
		}

	}

}