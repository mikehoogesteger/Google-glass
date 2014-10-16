package com.infosupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

/**
 * @author Mike The ImageFilter can make a bitmap out of a jpg, crop a bitmap,
 *         make a bitmap black and white and can make a jpg out of a bitmap.
 */
public class ImageFilter {

	private static final String TAG = "ImageFilter";

	/**
	 * Makes a bitmap out of a file source.
	 * 
	 * @param image
	 *            is the path to the image file
	 * @return the bitmap that was made out of the image file
	 */
	public Bitmap makeBitmapOutJpg(File image) {
		Bitmap bitmap = BitmapFactory.decodeFile(image.toString());
		return bitmap;
	}

	/**
	 * Crops the image with the dimensions that are used in the front end (The
	 * border where people scan with).
	 * 
	 * @param image
	 *            is the bitmap to crop
	 * @return the cropped bitmap
	 */
	public Bitmap cropImage(Bitmap image) {
		image = Bitmap.createBitmap(image, 365, 434, 567, 130);
		return image;
	}

	/**
	 * Creates a jpg from a given bitmap and saves it on the imageLocation.
	 * 
	 * @param bitmap
	 *            is the image that will be saved to jpg
	 * @param imageLocation
	 *            is the location where the bitmap will be saved to
	 */
	public void createJpgFromBitmap(Bitmap bitmap, File imageLocation) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(imageLocation);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		} catch (Exception e) {
			Log.e(TAG, "Exception", e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				Log.i(TAG,
						"Bestand is opgeslagen onder: "
								+ imageLocation.getName());
			} catch (IOException e) {
				Log.e(TAG, "IOException", e);
			}
		}
	}

	/**
	 * Makes the given bitmap a black and white picture.
	 * 
	 * @param src
	 *            is the bitmap that is in color
	 * @return the src that was changed into a black and white picture
	 */
	public Bitmap makeBlackAndWhite(Bitmap src) {
		int width = src.getWidth();
		int height = src.getHeight();
		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

		int alpha, red, green, blue;
		int pixel;
		// Scan through all pixels
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				pixel = src.getPixel(x, y);
				alpha = Color.alpha(pixel);
				red = Color.red(pixel);
				green = Color.green(pixel);
				blue = Color.blue(pixel);
				int gray = (int) (0.2989 * red + 0.5870 * green + 0.1140 * blue);

				// use 128 as threshold, above -> white, below -> black
				if (gray > 85)
					gray = 255;
				else
					gray = 0;
				// set new pixel color to output bitmap
				bmOut.setPixel(x, y, Color.argb(alpha, gray, gray, gray));
			}
		}
		return bmOut;
	}

}
