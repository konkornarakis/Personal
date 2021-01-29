package project_top_k;

public class Quicksort {

	private Song array[];
	private int length;

	public void sort(Song[] inputArr) {

		if (inputArr == null || inputArr.length == 0) {
			return;
		}
		this.array = inputArr;
		length = inputArr.length;
		quickSort(0, length - 1);
	}

	private void quickSort(int lowerIndex, int higherIndex) {

		int i = lowerIndex;
		int j = higherIndex;
		Song pivot = array[lowerIndex + (higherIndex - lowerIndex) / 2];
		while (i <= j) {
			while (Song.compareTo(array[i], pivot) < 0) {
				i++;
			}
			while (Song.compareTo(array[j], pivot) > 0) {
				j--;
			}
			if (i <= j) {
				exchangeSongs(i, j);
				i++;
				j--;
			}
		}

		if (lowerIndex < j)
			quickSort(lowerIndex, j);
		if (i < higherIndex)
			quickSort(i, higherIndex);
	}

	private void exchangeSongs(int i, int j) {
		Song temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

}