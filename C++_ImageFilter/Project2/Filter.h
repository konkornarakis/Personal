#ifndef _FILTER
#define _FILTER

#include <iostream>
#include <vector>
#include "Array.h"
#include "ppm.h"
#include "Vec3.h"

/* The Filter.h contains the declarations of 5 classes which are:
	- Filter
	- FilterLinear
	- FilterGamma
	- FilterBlue
	- FilterLaplace
	Each class also has some methods, the respective constructors (default,copy,destructor,standard (with parameters)) and some fields depending on the class.
*/
namespace math {
	/*The Filter class is a base class for the 4 following classes. It uses an Image object for the default and copy constructor.
	  The class is also abstract, as it contains a pure virtual method, the operator << , which will be implemented on the other classes polymorphically.
	  Since we want to use the Filter class as a base class, we made the destructor virtual.
	*/
	class Filter {

	protected:
		Image img;
	public:
		Filter();
		Filter(const Image &src);
		virtual ~Filter();
		virtual Image operator << (const Image & image) = 0;	//Pure virtual method that the other classes are going to implement. It is used to apply the desired filter
	};

	/* The FilterLinear class implements the Linear filter. Some examples are making the negative of an image and brightening an image.
	   To implement the operator << method, the class uses two Color objects names a and c and takes advantage of a certain formula to create the new pixel.
		 - FORMULA : p'(i,j) = a*p(i,j) + c
	*/
	class FilterLinear : public Filter {

	protected:
		Color a, c; //Color objects
	public:
		FilterLinear();
		FilterLinear(Color a, Color c);
		FilterLinear(const FilterLinear & src);
		~FilterLinear();
		Image operator << (const Image & image); //Implementing the operator << to apply the linear filter based on the formula
		//Getters for the 2 Color variables
		Color getA();
		Color getC();
		//Setters for the 2 Color variables
		void setA(Color a);
		void setC(Color c);
	};

	/* The FilterGamma class implements the gamma filter. It has a few purposes in general, but for the images it makes the image either brighter or darker depending on the gamma value.
	   Typical gamma values are in the range of [0.5,2.0]. The higher the gamma value the darker the image becomes.
	   To implement the operator << the class uses a float value called gamma and takes advantage of its own formula.
		- FORMULA : p'(i,j) = (p(i,j))^gamma
		Note that the gamma value must be raised to every color channel (RGB).
	*/
	class FilterGamma : public Filter {
	protected:
		float gamma;	//Gamma value
	public:
		FilterGamma();
		FilterGamma(float gamma);
		FilterGamma(const FilterGamma &src);
		~FilterGamma();
		Image operator << (const Image & image);	//Implementing the operator << to apply the gamma filter based on the formula
		float getGamma();	//Getter for the gamma value
		void setGamma(float gamma);	//Setter for the gamma value
	};

	/* The FilterBlur class implements the blur filter. Its purpose is to reduce image noise and the detail of the image.
	   The reduction of these two, depends on an integer value called N. The higher the N value, the more blurry the image becomes.
	   For every pixel (i,j) , a location of NxN pixels is read with the pixel (i,j) as its center and the new pixel is the result of the weighted sum
	   of the maximum NxN pixels ïf the NxN neighbourhood.
	   The weight equals to 1/N^2
	   Note that the FilterBlur class inherits 2 classes, the abstract Filter class and the Array<float> class which will be used for an array,
	   which contains the desired weight.
		- FORMULA : p'(i,j) = double sum of p(i+m,j+n)*f(m+N/2,n+N/2)
			where:
				f : the array representation of the NxN pixels
				m : the counter of the first sum. The range of the m is [-N/2, N/2)
				n : the counter of the second sum. The range of the n is [-N/2, N/2)
	*/
	class FilterBlur : public Filter, public Array<float> {
	protected:
		int N;	//N value
		float weight;	//The weight value which is stored on the array f.
		Array<float> f;	//The filter f (float array)
	public:
		FilterBlur();
		FilterBlur(int N);
		FilterBlur(const FilterBlur &src);
		~FilterBlur();
		Image operator << (const Image & image);	//Implementing the operator << to apply the blur filter to the image
		void setN(int custom_N);  //Setter for the N value
		int getN();	//Getter for the N value
	};

	/* The FilterLaplace class implements the laplace filter. Its purpose is to find the edges of the image.
	   It uses a similiar technique to the blur filter (the reason why it derives from the FilterBlur class) only now, we use a specific 3x3 array
	   to do the same method as in the blur.
	   The 3x3 array is this: 0 1 0
							  1 -4 1
							  0 1 0
	  The filter (float array) f must contain these values.
		- FORMULA : p'(i,j) = max(0,min(1,double sum of p(i+m,j+n)*f(m+1,n+1)))
			where:
				f : the array representation of the 3x3 array
				m : counter of the first sum. Its range is [-1,1]
				n : counter of the second sum. Its range is [-1,1]
	*/
	class FilterLaplace : public FilterBlur {
	public:
		FilterLaplace();
		FilterLaplace(const FilterLaplace & src);
		~FilterLaplace();
		Image operator << (const Image & image);	//Implementing the operator << to apply the laplace filter to the image
	};
}
#endif