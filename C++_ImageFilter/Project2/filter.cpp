#include "Vec3.h"
#include "Array.h"
#include "ppm.h"
#include "Filter.h"

namespace math {
	//-------------------------------------------------------------- FILTER ------------------------------------------------------------------------------------------------
	//Filter Constructor
	Filter::Filter() {
		img = Image();
	}

	//Filter Copy Constructor
	Filter::Filter(const Image &src) {
		img = Image(src);
	}

	//Filter Destructor
	Filter::~Filter() {}


	//----------------------------------------------------------- FILTER_LINEAR --------------------------------------------------------------------------------------------
	//FilterLinear Default Constructor
	FilterLinear::FilterLinear() :Filter() {
		a = Color(0.0f);
		c = Color(0.0f);
	}

	//FilterLinear Constructor with the 2 colors initialization (a,c)
	FilterLinear::FilterLinear(Color a, Color c) {
		this->a = a;
		this->c = c;
	}

	//FilterLinear Copy Constructor
	FilterLinear::FilterLinear(const FilterLinear &src) :Filter(src) {
		a = src.a;
		c = src.c;
	}

	//FilterLinear Destructor
	FilterLinear::~FilterLinear() {}

	//Overloading the << operator in order to apply the linear filter to the image
	Image FilterLinear::operator << (const Image & image) {
		Image * final_image = const_cast<Image*>(&image);	//Using an Image class pointer and removing the const state of the parameter to setup the final image
		Image temp(image);	//Calling the Image's copy constructor
		for (unsigned int w = 0; w < image.getWidth(); w++) {
			for (unsigned int h = 0; h < image.getHeight(); h++) {
				//Using the formula to create the new pixel and store it to the final image using the set method. We also make sure to do the right clamping of the result
				final_image->set(w, h, *&((a*temp.get(w, h)) + c));
				final_image->set(w, h, *&(final_image->get(w, h).clampToUpperBound(1.0f)));
				final_image->set(w, h, *&(final_image->get(w, h).clampToLowerBound(0.0f)));
			}
		}
		return *final_image;
	}

	//Setters
	void FilterLinear::setA(Color a) {
		this->a = a;
	}

	void FilterLinear::setC(Color c) {
		this->c = c;
	}

	//Getters
	Color FilterLinear::getA() {
		return a;
	}

	Color FilterLinear::getC() {
		return c;
	}

	//----------------------------------------------------------------- FILTER_GAMMA ------------------------------------------------------------------------------------------
	//FilterGamma Default Constructor
	FilterGamma::FilterGamma() :Filter() {
		gamma = 0.5;
	}

	//FilterGamma Constructor with gamma initialization. Since the gamma value must be in the range of [0.5,2.0] we are making sure to set the right value
	FilterGamma::FilterGamma(float gamma) {
		if ((gamma >= 0.5) && (gamma <= 2.0)) {
			this->gamma = gamma;
		}
		else {
			this->gamma = 0.5; //Default value
		}
	}

	//FilterGamma Copy Constructor
	FilterGamma::FilterGamma(const FilterGamma & src) :Filter(src) {
		gamma = src.gamma;
	}

	//FilterGamma Destructor
	FilterGamma::~FilterGamma() {}

	//Overloading the << operator to apply the gamma filter
	Image FilterGamma::operator << (const Image & image) {
		Image * final_image = const_cast<Image*>(&image);
		Image temp(image);
		for (unsigned int w = 0; w < image.getWidth(); w++) {
			for (unsigned int h = 0; h < image.getHeight(); h++) {
				//Remember that the gamama value must be raised on every color channel. We also use the pow method to our advantage!
				final_image->set(w, h, *&(Color(pow(temp.get(w, h).r, gamma), pow(temp.get(w, h).g, gamma), pow(temp.get(w, h).b, gamma))));
				final_image->set(w, h, *&(final_image->get(w, h).clampToUpperBound(1.0f)));
				final_image->set(w, h, *&(final_image->get(w, h).clampToLowerBound(0.0f)));
			}
		}
		return *final_image;
	}

	//Setter
	void FilterGamma::setGamma(float gamma) {
		this->gamma = gamma;
	}

	//Getter
	float FilterGamma::getGamma() {
		return gamma;
	}

	//-------------------------------------------------------------- FILTER_BLUR -----------------------------------------------------------------------------------------
		//FilterBlur default constructor
	FilterBlur::FilterBlur() {
		buffer.resize(1 * 1);
		buffer.push_back(1);
	}

	//FilterBlur constructor with N value initialization
	FilterBlur::FilterBlur(int N) :Array(N, N), Filter(), N(N) {
		this->weight = 1 / (float)pow(N, 2); //Weight initialization. Using the pow method to represent N^2
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				set(i, j, weight);	//set the weight values on the float array
			}
		}
	}

	//FilterBlur copy constructor
	FilterBlur::FilterBlur(const FilterBlur & src) :Array(src), Filter(src) {
		N = src.N;
		for (int i = 0; i < src.N; i++) {
			buffer[i] = 1 / (float)pow(N, 2);
		}
	}

	//FilterBlur Destructor
	FilterBlur::~FilterBlur() {}

	//Overloading the << operator to apply the blur filter
	Image FilterBlur::operator << (const Image & image) {
		//NOTE : This is the only case where we didn't use the creation of a new Image. Doing that, it will give false results.
		//Creating the filter (float array) f and putting the weight value in it
		f = Array<float>(N, N);
		weight = 1 / (float)pow(N, 2);
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				f(i, j) = weight;
			}
		}
		for (unsigned int w = 0; w < image.getWidth(); w++) {
			for (unsigned int h = 0; h < image.getHeight(); h++) {
				// The Color sum (a second sum could be created just to agree with the formula, but it is not needed since the double for-loop for the m,n counters do that)
				Color sum = (0.0f, 0.0f, 0.0f);
				for (int m = -N / 2; m < N / 2; m++) {
					for (int n = -N / 2; n < N / 2; n++) {
						int s1 = w + m;
						int s2 = h + n;
						//Checking the bounds. We need to do every possible check and use the formula depending on the situation.
						if ((s1 >= 0 && s1 < (int)image.getWidth()) && (s2 >= 0 && s2 < (int)image.getHeight())) {
							sum += image.get(s1, s2)*f(m + (N / 2), n + (N / 2));
						}
						else if (s1 >= 0 && s1 < (int)image.getWidth()) {
							sum += image.get(w + m, h)*f(m + (N / 2), n + (N / 2));
						}
						else if (s2 >= 0 && s2 < (int)image.getHeight()) {
							sum += image.get(w, h + n)*f(m + (N / 2), n + (N / 2));
						}
						else {
							sum += image.get(w, h)*f(m + (N / 2), n + (N / 2));
						}
					}
				}
				//Since the set method is not const, we need to remove the const state of the image in order to utilize the method. Using const_cast will help us with that!
				const_cast<Image&>(image).set(w, h, sum);
				const_cast<Image&>(image).set(w, h, *&(image.get(w, h).clampToUpperBound(1.0f)));
				const_cast<Image&>(image).set(w, h, *&(image.get(w, h).clampToLowerBound(0.0f)));
			}
		}
		return image;
	}

	//Setter
	void FilterBlur::setN(int N) {
		this->N = N;
	}

	//Getter
	int FilterBlur::getN() {
		return N;
	}

	//----------------------------------------------------------- FILTER_LAPLACE ---------------------------------------------------------------------------------------------------
	//FilterLaplace Default Constructor. This is where we make the 3x3 array given.
	FilterLaplace::FilterLaplace() :FilterBlur(3) {
		float weight[3][3] = { 0,1,0,1,-4,1,0,1,0 };
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				set(i, j, weight[i][j]);
			}
		}
	}

	//FilterLaplace Copy Constructor
	FilterLaplace::FilterLaplace(const FilterLaplace & src) :FilterBlur(src) {}

	//FilterLaplace Destructor
	FilterLaplace::~FilterLaplace() {}

	//Overloading the << operator to apply the laplace filter to the image
	Image FilterLaplace::operator << (const Image & image) {
		Image * final_image = const_cast<Image*>(&image);
		Image temp(image);

		for (unsigned int i = 0; i < image.getWidth(); i++) {
			for (unsigned int j = 0; j < image.getHeight(); j++) {
				Color sum = (0.0f, 0.0f, 0.0f);	//The Color sum (again it is not necessary to have a second sum
				for (int m = -1; m <= 1; m++) {
					for (int n = -1; n <= 1; n++) {
						int s1 = i + m;
						int s2 = j + n;
						//This is the only bound check we need. We are utilizing the copy constructor to make good use of the get method
						if ((s1 >= 0 && s1 < (int)image.getWidth()) && (s2 >= 0 && s2 < (int)image.getHeight())) {
							sum += temp.get(s1, s2)*(*this)(m + 1, n + 1);
						}
					}
				}
				//Doing the right clamping, while taking advantage of the formula and setting the result to the final image
				Color max = (sum.clampToUpperBound(1.0f)).clampToLowerBound(0.0f);
				final_image->set(i, j, max);
			}
		}
		return *final_image;
	}
}