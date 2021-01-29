#include <iostream>
#include <fstream>
#include <string>
#include "ppm.h"

using namespace std;
namespace imaging {
	float * ReadPPM(const char * filename, int * w, int * h) {
		fstream ppmFile(filename, ios::in | ios::binary);
		string ppm_type;
		int maxValue;
		/*Some checks must be made. These are:
			1. The file exists for reading and it is not opened somewhere!
			2. Valid format (PPM P6 format here!)
			3. Valid width/height values
			4. Valid maximum Value (255 here)
			Should any of these fail, the corresponding error message will be shown, the file is closed and it immediatly returs nullptr to the float array!
		*/
		if (!ppmFile.is_open()) {
			cout << "ERROR! Could not open the PPM file!" << endl;
			ppmFile.close();
			return nullptr;
		}
		else {
			ppmFile >> ppm_type;
			ppmFile >> *w;
			ppmFile >> *h;
			ppmFile >> maxValue;
			ppmFile.ignore(256, '\n');	//After we read the header we should ignore empty lines if necessary, before we read the binary data
			if (ppm_type.compare("P6") != 0) {
				cout << "ERROR! The PPM file is not of type P6" << endl;
				ppmFile.close();
				return nullptr;
			}
			else if (*w <= 0 || *h <= 0) {
				cout << "ERROR! Invalid width and/or height values!" << endl;
				ppmFile.close();
				return nullptr;
			}
			else if (maxValue != 255) {
				cout << "ERROR! Invalid maximum value! It should have been in the range [0,255]!" << endl;
				ppmFile.close();
				return nullptr;
			}
			else {
				cout << "\nImage dimensions are: " << *w << " X " << *h << "\n" << endl;
				int bufferSize = 3 * (*w)*(*h);
				unsigned char *readData = new unsigned char[bufferSize];	//Allocating an unsigned char array with a size of 3*width*height to hold the read data
				float * imageData = new float[bufferSize];	//Allcoating a float array to hold the image data that we will be later converted from unsigned char
				ppmFile.read((char*)readData, sizeof(int)*bufferSize);	//Reading the binary data
				for (int i = 0; i < bufferSize; i++) {
					//Converting the unsigned char(int) which are within the range of [0,255] to the typical (normalized( intensity values within the range of [0.0,1.0f]
					imageData[i] = (int)readData[i] / (float)maxValue;
				}
				delete[] readData;
				ppmFile.close();
				return imageData;
			}
		}
	}

	bool WritePPM(const float * data, int w, int h, const char * filename) {
		fstream wFile(filename, ios::out | ios::binary);
		wFile.clear();

		if (!wFile.is_open()) {
			printf("ERROR! Could not open the file!\n");
			return false;
		}
		wFile << "P6" << endl << w << endl << h << endl << "255" << endl;
		int bufferSize = 3 * w * h;
		unsigned char *writtenData = new unsigned char[bufferSize];	//Allocating an unsigned char array to hold the data which is going to be written to the PPM Image
		for (int i = 0; i < bufferSize; i++) {
			//The inversed procedure of the readPPM. From float values within the range of [0.0f,1.0f] to unsigned char withing the range of [0,255]
			writtenData[i] = static_cast<unsigned char>(data[i] * 255.0f);	//We used the static_cast just to make sure that there is no loss of data in the conversion (down-casting)
		}
		wFile.write((char*)writtenData, bufferSize);	//Writing the data in the PPM Image
		wFile.flush();	//Just to make sure that everything was passed into the PPM Image
		wFile.close();
		return true;
	}
}