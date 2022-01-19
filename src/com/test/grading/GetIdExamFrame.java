package com.test.grading;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.test.process.MatProcess;
import com.test.process.RectCompareNoise;

public class GetIdExamFrame {

	public static final double X_MADETHI_STUDENT_PERCENT = 0.8;
	public static final double Y_MADETHI_STUDENT_PERCENT = 0.005;
	public static final double W_MADETHI_STUDENT_PERCENT = 0.1739;
	public static final double H_MADETHI_STUDENT_PERCENT = 0.3;

	private static int thresh = 100;

	public Mat getIDExameFrame(Mat start) {

		int x = (int) (X_MADETHI_STUDENT_PERCENT * start.cols() - 5);
		int y = (int) (Y_MADETHI_STUDENT_PERCENT * start.rows());
		int w = (int) (W_MADETHI_STUDENT_PERCENT * start.cols());
		int h = (int) (H_MADETHI_STUDENT_PERCENT * start.rows());
		Mat res = new Mat(start, new Rect(x, y, w, h));
		Mat gray = MatProcess.toColorGray(res);

		Set<Rect> rects = new TreeSet<Rect>(RectCompareNoise.RECT_COMPARE_Y);
		for (int thresh_ = 140; thresh_ > 90; thresh_ -= 3) {
			rects.clear();

			Mat thresh_image = MatProcess.toThreshBinary(gray, thresh_);
			List<MatOfPoint> contours = MatProcess.getContour_1(thresh_image);
			// lọc phần tử gây nhiễu

			for (int i = 0; i < contours.size(); i++) {
				Rect rect = Imgproc.boundingRect(contours.get(i));
				if (rect.area() > 50 && rect.area() < 250 && rect.x < (thresh_image.cols() * 0.29)) {
					rects.add(rect);
				}
			}

			if (rects.size() == 2) {
				this.thresh = thresh_;
				break;
			}

		}

		ArrayList<Rect> list = new ArrayList<Rect>();
		for (Rect r : rects) {
			list.add(r);
		}

		ArrayList<Rect> rsListPosition = new ArrayList<Rect>();
		Rect rectFirst = list.get(0);
		Rect rectLast = list.get(list.size() - 1);

		rsListPosition.add(rectFirst);
		rsListPosition.add(rectLast);


		// rotate
		Point p1 = new Point((rsListPosition.get(0).x), (rsListPosition.get(0).y));
		Point p2 = new Point((rsListPosition.get(1).x), (rsListPosition.get(1).y));
		int iX = (res.width()) / 2;
		int iY = ((rsListPosition.get(1).y) - (rsListPosition.get(0).y)) / 2 + (rsListPosition.get(0).y);

		Mat table = MatProcess.rotate(res, MatProcess.computeAngleRotate(p1, p2, iX, iY));

		int x_drop = ((rsListPosition.get(0).x) - 10 < 0) ? 0 : (rsListPosition.get(0).x) - 10;
		int y_drop = ((rsListPosition.get(0).y) - 10 < 0) ? 0 : (rsListPosition.get(0).y) - 10;

		int w_drop_temp = rsListPosition.get(0).width * 10;
		int w_drop = ((w_drop_temp + x_drop) >= table.width()) ? table.width() - x_drop - 2 : w_drop_temp - 2;

		int h_drop_temp = rsListPosition.get(0).height * 23;
		int h_drop = ((h_drop_temp + y_drop) >= table.height()) ? table.height() - y_drop - 2 : h_drop_temp - 2;

		Mat drop_Mat = new Mat(table, new Rect(x_drop, y_drop, w_drop, h_drop));
		return drop_Mat;
	}

}