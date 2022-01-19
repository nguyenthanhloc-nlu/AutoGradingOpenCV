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

public class GetIdStudentFrame {

	public static final double X_ID_STUDENT_PERCENT = 0.55;
	public static final double Y_ID_STUDENT_PERCENT = 0.005;
	public static final double W_ID_STUDENT_PERCENT = 0.4;
	public static final double H_ID_STUDENT_PERCENT = 0.3;

	private static int thresh = 100;

	public Mat getIDStudentFrame(Mat start) {

		int x = (int) (X_ID_STUDENT_PERCENT * start.cols());
		int y = (int) (Y_ID_STUDENT_PERCENT * start.rows());
		int w = (int) (W_ID_STUDENT_PERCENT * start.cols());
		int h = (int) (H_ID_STUDENT_PERCENT * start.rows());
		Mat res = new Mat(start, new Rect(x, y, w, h));
		Mat gray = MatProcess.toColorGray(res);

		Set<Rect> rects = new TreeSet<Rect>(RectCompareNoise.RECT_COMPARE_Y);
		for (int thresh_ = 140; thresh_ > 90; thresh_ -= 3) {
			rects.clear();
			Mat thresh_image = MatProcess.toThreshBinary(gray, thresh_);
			List<MatOfPoint> contours = MatProcess.getContour_1(thresh_image);

			for (int i = 0; i < contours.size(); i++) {
				Rect rect = Imgproc.boundingRect(contours.get(i));
				if (rect.area() > 50 && rect.area() < 250 && rect.x < (thresh_image.cols() * 0.12)) {
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


		Point p1 = new Point((list.get(0).x), (list.get(0).y));
		Point p2 = new Point((list.get(1).x), (list.get(1).y));
		int iX = (res.width()) / 2;
		int iY = ((list.get(1).y) - (list.get(0).y)) / 2 + (list.get(0).y);

		Mat table = MatProcess.rotate(res, MatProcess.computeAngleRotate(p1, p2, iX, iY));

		int x_drop = ((list.get(0).x) - 10 < 0) ? 0 : (list.get(0).x) - 10;
		int y_drop = ((list.get(0).y) - 10 < 0) ? 0 : (list.get(0).y) - 10;

		int w_drop_temp = list.get(0).width * 13;
		int w_drop = ((w_drop_temp + x_drop) > table.width()) ? table.width() - x_drop : w_drop_temp;

		int h_drop_temp = list.get(0).height * 23;
		int h_drop = ((h_drop_temp + y_drop) > table.height()) ? table.height() - y_drop : h_drop_temp;
		Mat drop_Mat = new Mat(table, new Rect(x_drop, y_drop, w_drop, h_drop));
		return drop_Mat;
	}

}