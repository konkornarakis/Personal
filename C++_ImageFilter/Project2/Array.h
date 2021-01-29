#ifndef _ARRAY
#define _ARRAY

#include "Vec3.h"
#include "ppm.h"
#include <string>
#include <vector>

/* The Array.h contains the declaration of the template class Array and the derived class Image, as well as the implementation of the former (because it is a template!
	It is written on the math namespace and it is a general representation of an Array. We use a vector to hold the data we want.
*/
typedef math::Vec3<float> Color;	//An alias representing the color of the pixel(x,y)

namespace math {
	template <typename T>	//declaring the template class Array<T>
	class Array {

	protected:
		std::vector<T> buffer;	//The buffer of the array, using a vector
		unsigned int width, height;	//The dimensions of the buffer (array)

	public:
		//Width Getter
		const unsigned int getWidth() const {
			return width;
		}

		//Height Getter
		const unsigned int getHeight() const {
			return height;
		}

		//Obtains a pointer to the internal data of the buffer
		T * getRawDataPtr() {
			return buffer.data();
		}

		//Returns the value of type T that is contained at the location (x,y). Return a default value if the x,y values are out of range.
		T get(unsigned int x, unsigned int y) const {
			if ((x <= width) && (y <= height)) {
				return buffer.at(y*width + x);
			}
			else {
				std::cout << "ERROR! Out of bounds!" << std::endl;
				return T();
			}
		}

		//Sets the value of type T at the location (x,y)
		void set(unsigned int w, unsigned int h, T&value) {
			if ((w <= width) && (h <= height)) {
				buffer.at(h*width + w) = value;
			}
			else {
				std::cout << "ERROR! Out of bounds!" << std::endl;
				return;
			}
		}

		//Copies the array data from an external raw buffer to the internal array buffer.
		void setData(const T * & data_ptr) {
			if ((width == 0) || (height == 0)) {
				std::cout << "ERROR! Width or height cannot be 0!" << std::endl;
				return;
			}
			else if (buffer.empty()) {
				std::cout << "ERROR! The image buffer was not allocated!" << std::endl;
				return;
			}
			else {
				buffer.push_back(*data_ptr);
			}
		}

		//Default Array Constructor
		Array() {
			width = 0;
			height = 0;
			buffer = {};	//The buffer should not contain anything
		}

		//Array Constructor with width and height specification
		Array(unsigned int width, unsigned int height) {
			this->width = width;
			this->height = height;
			buffer.resize(width*height); //
		}

		//Array Constructor with data initialization
		Array(unsigned int width, unsigned int height, const T * data_ptr) {
			this->width = width;
			this->height = height;
			buffer.resize(width*height);
			setData(data_ptr);
		}

		//Array Copy Constructor
		Array(const T & arr) {
			if (buffer.size() != 0) {
				buffer.clear();
			}
			width = arr.width;
			height = arr.height;
			buffer.resize(width*height);
			for (unsigned int w = 0; w < width; w++) {
				for (unsigned int h = 0; h < height; h++) {
					set(w, h, arr.get(w, h));
				}
			}
		}

		//Array Destructor
		~Array() {
			buffer.clear();
		}

		//Overloaded operator () to return a reference of type T, which represents the coordinates of T(x,y)
		T& operator () (int i, int j) {
			return buffer[j*width + i];
		}
	};

	/*Derived class Image (Image IS an Array which has Color elements). We only wrote the declarations of the Image Class.
	  The implementation is contained on the Image.cpp.
	*/
	class Image : public Array<Color> {

	public:
		//Image();
		//Image(unsigned int width, unsigned int height);
		//Image(unsigned int width, unsigned int height, const Color * data_ptr);
		//Image(const Image &src);
		~Image();
		Image & operator = (const Image&right);
		bool load(const std::string & filename, const std::string & format);
		bool save(const std::string & filename, const std::string & format);
	};
}

#endif