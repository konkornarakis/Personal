#include <iostream>
#include <string>
#include "Array.h"
#include "ppm.h"

using namespace imaging;
namespace math {

	/*Image::Image():Array() {
		//std::cout << "Image - Default Constructor" << std::endl;
	}

	Image::Image(unsigned int width, unsigned int height):Array(width,height) {
		//std::cout << "Image - Constructor 1" << std::endl;
	}

	Image::Image(unsigned int width, unsigned int height, const Color * data_ptr):Array(width,height,data_ptr) {
		//std::cout << "Image - Constructor 2" << std::endl;
	}

	Image::Image(const Image &src):Array(src) {
		//std::cout << "Image - Copy Constructor" << std::endl;
	}*/

	//Image Destructor
	Image::~Image() {}

	//Copy assignment operator for Image objects
	Image & Image::operator = (const Image & right) {
		width = right.width;
		height = right.height;
		buffer = right.buffer;
		return *this;
	}

	//Loads the image data from the specified file, if the extension of the filename is the same as the format
	bool Image::load(const std::string & filename, const std::string & format) {
		std::string filename_extension = filename.substr(filename.find(format));
		if (format.size() != 3) {
			return false;
		}
		else {
			int counter = 0;
			for (unsigned int i = 0; i < format.size(); ++i) {
				if (tolower(format[i]) == tolower(filename_extension[i])) {
					counter++;
				}
			}
			if (counter == format.size()) {
				this->Array::Array(); //Calling the default Array Constructor to pass the values
				int W, H;
				const char* initial_name = filename.c_str();
				float *loadedData = ReadPPM(initial_name, &W, &H); //Using the ReadPPM method from the PPM libary to load the image data
				if (loadedData != nullptr) {
					width = W;
					height = H;
					/*Inserting the loaded data to the buffer. The data should be casted to Color *. The vector (buffer) size is increased due to the number of elements we want to insert.
						 - buffer.begin() : the iterator with the help of which, we will be able to store the loaded data,
						 - loadedData : casted to Color * and it is the data to be stored
						 - loadedData+(width*height) : the size of the buffer
					*/
					buffer.insert(buffer.begin(), (Color*)loadedData, (Color*)loadedData+(width*height)); 
					return true;
				}
				else return false;
			}
			else return false;
		}
	}

	//Stores the new image data to the specified file, if the extension is the same as the format.
	bool Image::save(const std::string & filename, const std::string & format) {
		std::string filename_extension = filename.substr(filename.find(format));
		if (format.size() != 3) {
			return false;
		}
		else {
			int counter = 0;
			for (unsigned int i = 0; i < format.size(); ++i) {
				if (tolower(format[i]) == tolower(filename_extension[i])) {
					counter++;
				}
			}
			if (counter == format.size()) {
				std::string whole_filename = "filtered_" + filename.substr(0); //The filtered_ prefix is added to the new image.
				const char* final_name = whole_filename.c_str();
				const float* savedData = (float*)buffer.data();	//The buffer's data must be cast to float * , in order to save the new data. Remember that the initial buffer data is a pointer to Color (Color *)
				return WritePPM(savedData, width, height, final_name);	//Using the WritePPM method from the PPM library to save the data to the PPM image
			}
			else return false;
		}
	}
}