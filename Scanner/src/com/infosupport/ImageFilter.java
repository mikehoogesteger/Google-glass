package com.infosupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

public class ImageFilter {

	private static final String TAG = "ImageFilter";

	public ImageFilter(File image) {
		Log.i(TAG, "Creating bitmap out of jpg");
		Bitmap bitmap = makeBitmapOutJpg(image);
		Log.i(TAG, "Making the image black and white");
		bitmap = makeBlackAndWhite(bitmap);
		Log.i(TAG, "Saving the black and white image");
		createJpgFromBitmap(bitmap, image);
		Log.i(TAG, "Image has been saved");
	}

	public Bitmap makeBitmapOutJpg(File image) {
		Bitmap bitmap = BitmapFactory.decodeFile(image.toString());
		return bitmap;
	}

	public Bitmap makeBitmapBlackAndWhite(Bitmap image) {
		ColorMatrix colorMatrix = new ColorMatrix();
		colorMatrix.setSaturation(0);

		ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
				colorMatrix);

		Bitmap blackAndWhiteBitmap = image.copy(Bitmap.Config.ARGB_8888, true);

		Paint paint = new Paint();
		paint.setColorFilter(colorMatrixFilter);

		return blackAndWhiteBitmap;
	}

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
			} catch (IOException e) {
				Log.e(TAG, "IOException", e);
			}
		}
	}

	public Bitmap makeBlackAndWhite(Bitmap src) {
		int width = src.getWidth();
		int height = src.getHeight();
		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
		// color information
		int A, R, G, B;
		int pixel;

		// scan through all pixels
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);
				int gray = (int) (0.2989 * R + 0.5870 * G + 0.1140 * B);

				// use 128 as threshold, above -> white, below -> black
				if (gray > 128)
					gray = 255;
				else
					gray = 0;
				// set new pixel color to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, gray, gray, gray));
			}
		}
		return bmOut;
	}

}
