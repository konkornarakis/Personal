#ifndef UTILITIES
#define UTILITIES

/* The utilities.h provides 2 template methods so as to not have to write the same code again and again. These are:
	- T convertStringToNumbeer (const string & number)
	- void applyFilter (int counter , T & filter_array, string & filter_name, Image & image)
*/
#include <sstream>
#include <string>

using namespace std;
using namespace math;

//This method converts a string which represents a number to an actual number, depending on what type (T) of number we want to return.
template <typename T>
T convertStringToNumber(const string & number) {
	istringstream iss(number);	//Creating a stream for the string
	T converted_number = 0; //The variable where the converted number will be stored. 0 is the default value
	iss >> converted_number; //Giving the correct converted value using the characters made in the stream
	return converted_number;
}

//This method will apply the desired filter and show a confirmation message for the filter application
template <typename T>
void applyFilter(int counter, T & filter_array, string & filter_name, Image & image) {
	//Applying the filter i times, depending on the counter
	for (int i = 0; i < counter; i++) {
		filter_array[i] << image;
	}
	cout << filter_name << " Filter has been applied!" << endl;
}

#endif
