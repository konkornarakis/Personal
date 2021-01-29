#include <iostream>
#include "ppm.h"
#include "Vec3.h"
#include "Array.h"
#include "Filter.h"
#include "utilities.h"


using namespace std;
using namespace math;

int main(int argc, char *argv[]) {
	string filename;	
	string input;	   
	string blank = " ";
	string filter_1 = "Linear";
	string filter_2 = "Gamma";
	string filter_3 = "Blur";
	string filter_4 = "Laplace";
	string f_apply = "-f";	//The identifier, after which we must give the name of the filter we want to apply and the corresponding parameters.
	//Two bool variables which change to true if we applied a filter and when we haven't given the name of the image respectively.
	bool applied_filter = false;
	bool no_image = false;

	//The parameters for the first 3 filters (Linear, Gamma, Blur)
	float aR, aG, aB, cR, cG, cB;
	float gamma_value;
	int N;

	//Creating an instance for the first 3 filters, using the default constructor
	FilterLinear linear;
	FilterGamma gamma;
	FilterBlur blur;

	//Creating 3 vectors that hold objects of the first 3 filters
	vector<FilterLinear> linear_application;
	vector<FilterGamma> gamma_application;
	vector<FilterBlur> blur_application;
	/*The only exception is for the laplace filter. When we created the vector for the laplace filter we noticed that the results were not right.
	  Using an array though, it worked properly, however it has a limit. */
	FilterLaplace * laplace_application = new FilterLaplace[5];
	//A vector to store the name of the filters. It will be used on the filter application
	vector<string> filter_names;


	Image * image = new Image(); 

	//Counter initializations. Counter i is used for the while-loop and the others count how many times we want to apply the corresponding filter
	int i = 1;
	int linear_counter = 0;
	int gamma_counter = 0;
	int blur_counter = 0;
	int laplace_counter = 0;
	int filter_counter = 0;

	//The first if-statement will be executed if we run the program from the CMD and we give more than one argument (argv[0] is the .exe name)
	if (argc > 1) {
		while (i < argc) {	//The while-loop is used in order for us to give as many filters as we want the program to apply to the image
			int count = 0;	//Counter initialization that will help us the counting of the i counter (the i counter is used for the argv[i] section)
			filename = argv[argc - 1];	//The last argument we must give should be the filename
			/*Since it wasn't made clear when an error message should be displayed when we don't give a filename, we assumed that the user gives all the valid arguments
			  except for the filename. In other words the last argument will be a parameter (a number) or the the name laplace (since the laplace filter doesn't have any
			  arguments.
			*/
			if (isdigit(filename[0]) || filename == "laplace") {
				no_image = true;
				break;
			}
			/*Checks if the user gave the identifier -f as an argument. From this point on the user should give the name of the filter and its parameters (if it has).
			  Another check is done to see if the user gave the correct name of the filter or not.
			*/
			else if (argv[i] == f_apply) {
				if ((string)argv[i + 1] == "linear") {
					applied_filter = true;
					//Using the convertStringToNumber method from the utilities.h to convert the arguments to float
					aR = convertStringToNumber<float>(argv[i + 2]);
					aG = convertStringToNumber<float>(argv[i + 3]);
					aB = convertStringToNumber<float>(argv[i + 4]);
					cR = convertStringToNumber<float>(argv[i + 5]);
					cG = convertStringToNumber<float>(argv[i + 6]);
					cB = convertStringToNumber<float>(argv[i + 7]);
					count += 8;	//We raise the counter by 8 because we gave 6 arguments for the linear filter + the name of the .exe + the identifier -f
					//Using the setters to configure the linear filter parameters
					linear.setA(Color(aR, aG, aB));
					linear.setC(Color(cR, cG, cB));
					linear_application.push_back(linear);	//Putting the FilterLinear object on the corresponding vector
					linear_counter++;
					filter_names.push_back(filter_1);	//Putting the filter name on the corresponding vector (useful for the correct order of the filter applications
					filter_counter++;
				}
				else if ((string)argv[i + 1] == "gamma") {
					applied_filter = true;
					gamma_value = convertStringToNumber<float>(argv[i + 2]);
					count += 3;	//One parameter for the gamma filter + .exe name + -f
					gamma.setGamma(gamma_value);	//Using the setter for the correct gamma filter parameter configuration
					gamma_application.push_back(gamma);	//Putting the FilterGamma object on the corresponding vector
					gamma_counter++;
					filter_names.push_back(filter_2);
					filter_counter++;
				}
				else if ((string)argv[i + 1] == "blur") {
					applied_filter = true;
					N = convertStringToNumber<int>(argv[i + 2]); //Using the convertStringToNumber method to convert the argument to an integer
					count += 3;	//One paraneter for the blur filter + .exe name + -f
					/*Creating the blur filter by calling the FilterBlur constructor with N value specification (with the way we wanted to implement the basic program
					  using the setter setN gave an error message.
					*/
					blur = FilterBlur(N);
					blur_application.push_back(blur);	//Putting the FilterBlur object on the corresponding vector
					blur_counter++;
					filter_names.push_back(filter_3);
					filter_counter++;
				}
				else if ((string)argv[i + 1] == "laplace") {
					applied_filter = true;
					count += 2;	// .exe name + -f (laplace filter doesn't have parameters)
					laplace_counter++;
					filter_names.push_back(filter_4);
					filter_counter++;
				}
				//If we didn't gave the correct name of the available filter an error message is displayed!
				else {
					cerr << "ERROR! The filter " << (string)argv[i + 1] << " doesn't exist!" << endl;
					break;
				}
			}
			//The following is happened when the argument the user gave is a filename
			else {
				if (applied_filter) {	//If we applied any filters to it, continue.
					count += 1;	//Counting the filename
					if (image->load(filename, "ppm")) {	//If the image is of ppm origin and it is loaded successfully then proceed to the filter application
						for (int i = 0; i < filter_counter; i++) {
							/*The application of the filters we want to apply must be done in order. Therefore, checking which filter we want to apply (filter_names) and using
							 the applyFilter method from the utilities.h we will be able to do so1
							 */
							if (filter_names[i] == filter_1) {
								applyFilter(linear_counter, linear_application, filter_1, *image);
							}
							else if (filter_names[i] == filter_2) {
								applyFilter(gamma_counter, gamma_application, filter_2, *image);
							}
							else if (filter_names[i] == filter_3) {
								applyFilter(blur_counter, blur_application, filter_3, *image);
							}
							else {
								applyFilter(laplace_counter, laplace_application, filter_4, *image);
							}
						}
						cout << endl;
						image->save(filename, "ppm");	//Saving the now filtered ppm image
					} //If not break from the while-loop (the error message is contained on the ReadPPM method of the PPM library
					else {
						break;
					}
				} //If not, then the corresponding message is displayed
				else {
					cout << "No filter given, the image " << filename << " is UNCHANGED!" << endl;
					break;
				}
			}
			i += count;	//Raising the i counter for the correct usage of the whle-loop and handling of the arguments
		}
		//If we didn't give a filename the corresponding message is displayed.
		if (no_image) {
			cout << "ERROR! No image was given!" << endl;
		}
	}
	//The else statement will be executed if we run the program by clicking on the .exe or give only the name of the .exe on the CMD
	else {
		float * linear_par = new float[6]; //Allocating 6 blocks of memory to store the float arguments for the linear filter
		cout << "> filter ";
		bool foundPPM = false;
		while (!foundPPM) {
			int count = 0;
			cin >> input; //If we run the program via the .exe program then the user must type the arguments
			//To "symbolize" that we didn't give a filename, we will just use a random number after the filter and its parameter(s) selection
			if (isdigit(input[0])) {
				no_image = true;
				break;
			}
			else if (input == f_apply) {
				cin >> input;
				if (input == "linear") {
					applied_filter = true;
					for (int l = 0; l < 6; l++) {
						cin >> input;
						linear_par[l] = convertStringToNumber<float>(input);
					}
					count += 8;
					linear.setA(Color(linear_par[0], linear_par[1], linear_par[2]));
					linear.setC(Color(linear_par[3], linear_par[4], linear_par[5]));
					linear_application.push_back(linear);
					linear_counter++;
					filter_names.push_back(filter_1);
					filter_counter++;
				}
				else if (input == "gamma") {
					applied_filter = true;
					cin >> input;
					count += 3;
					gamma_value = convertStringToNumber<float>(input);
					gamma.setGamma(gamma_value);
					gamma_application.push_back(gamma);
					gamma_counter++;
					filter_names.push_back(filter_2);
					filter_counter++;
				}
				else if (input == "blur") {
					applied_filter = true;
					cin >> input;
					count += 3;
					N = convertStringToNumber<int>(input);
					blur = FilterBlur(N);
					blur_application.push_back(blur);
					blur_counter++;
					filter_names.push_back(filter_3);
					filter_counter++;
				}
				else if (input == "laplace") {
					applied_filter = true;
					count += 2;
					laplace_counter++;
					filter_names.push_back(filter_4);
					filter_counter++;
				}
				else {
					cerr << "ERROR! The filter " << input << " doesn't exist!" << endl;
					break;
				}
			}
			else if (input.find("ppm")) {	//Finds whether the input contains the "ppm" string (finding the filename)
				filename = input;
				if (applied_filter) {
					foundPPM = true;
					count += 1;
					if (image->load(filename, "ppm")) {
						for (int i = 0; i < filter_counter; i++) {
							if (filter_names[i] == filter_1) {
								applyFilter(linear_counter, linear_application, filter_1, *image);
							}
							else if (filter_names[i] == filter_2) {
								applyFilter(gamma_counter, gamma_application, filter_2, *image);
							}
							else if (filter_names[i] == filter_3) {
								applyFilter(blur_counter, blur_application, filter_3, *image);
							}
							else {
								applyFilter(laplace_counter, laplace_application, filter_4, *image);
							}
						}
						cout << endl;
						image->save(input, "ppm");
					}
					else {
						break;
					}
				}
				else {
					cout << "No filter was given. The image " << filename << " is UNCHANGED!" << endl;
					break;
				}
			}
			i += count;
		}
		if (no_image) {
			cout << "ERROR! No image was given!" << endl;
		}
		delete[] linear_par;	//Freeing memeory by deleting the linear_par array pointer
	}
	/*We cannot use delete[] on vectors (except laplace_application). The closest thing to this is using the clear() method
	  It will remove all the elements from them. In actuality, it will delete the objectes stored in them and free memory by
	  calling their respective Destructor.
	*/
	linear_application.clear();
	gamma_application.clear();
	blur_application.clear();
	delete[] laplace_application;
	system("pause");
	return 0;
}