package com.test.grading;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.test.process.MatProcess;
import com.test.process.RectCompareNoise;

public class GetResltExamFrame {

	public static final double X_DAP_AN_PERCENT = 0.1;
	public static final double Y_DAP_AN_PERCENT = 0.4755;
//	public static final double W_DAP_AN_PERCENT = 0.9704;
	public static final double H_DAP_AN_PERCENT = 0.5245;

	public static int thresh = 100;

	public Mat getResultFrame(Mat src) {
		System.out.println("GetResltExamFrame: getResultFrame");

//		System.err.println("\nstart đáp án: ");
		int x = (int) (X_DAP_AN_PERCENT * src.cols());
		int y = (int) (Y_DAP_AN_PERCENT * src.rows() - 20);
		int w = (int) ((1 - X_DAP_AN_PERCENT) * src.cols() - 50);
		int h = (int) (H_DAP_AN_PERCENT * src.rows() + 20);
//		Mat res = new Mat(src, new Rect(x + 60, y - 10, w - 100, h + 20));
		Mat res = new Mat(src, new Rect(x, y, w, h));
//		System.out.println("res.width()" + res.width());
//		System.out.println("res.height()" + res.height());
		Mat gray = MatProcess.toColorGray(res);
		Mat thresh_image = MatProcess.toThreshBinary(gray, 100);
		List<MatOfPoint> contours = MatProcess.getContour_1(thresh_image);
		// lọc phần tử gây nhiễu
		Set<Rect> rects = new TreeSet<Rect>(RectCompareNoise.RECT_COMPARE_Y);

		for (int i = 0; i < contours.size(); i++) {
			Rect rect = Imgproc.boundingRect(contours.get(i));
//			System.out.println("rect: " + rect);
			if (rect.area() > 50 && rect.area() < 400 && rect.x < (thresh_image.cols() * 0.2)
					&& Math.abs(rect.width - rect.height) < 5) { // && (rect.y < (thresh_image.rows() * 0.08) || rect.y
																	// > (thresh_image.rows() * 0.92))
				rects.add(rect);
			}
		}

//		System.out.println("rects: " + rects);

		ArrayList<Rect> list = new ArrayList<Rect>();
		for (Rect r : rects) {
			list.add(r);
		}

//		System.out.println("list: " + list);

		ArrayList<Rect> rsListPosition = new ArrayList<Rect>();
		Rect rectFirst = list.get(0);
		Rect rectLast = list.get(list.size() - 1);

		rsListPosition.add(rectFirst);
		rsListPosition.add(rectLast);

//		System.out.println("arrayList position RESULT Frame: " + rsListPosition);

		Point p1 = new Point((rsListPosition.get(0).x), (rsListPosition.get(0).y));
		Point p2 = new Point((rsListPosition.get(1).x), (rsListPosition.get(1).y));
		int iX = (res.width()) / 2;
		int iY = ((rsListPosition.get(1).y) - (rsListPosition.get(0).y)) / 2 + (rsListPosition.get(0).y);

		Imgcodecs.imwrite("src/img/dap-an-b3-before-romate.jpg", res);
//		System.out.println("xoay ảnh mã đề");

		Mat table = MatProcess.rotate(res, MatProcess.computeAngleRotate(p1, p2, iX, iY));
		Imgcodecs.imwrite("src/img/dap-an-b4-after-romate.jpg", table);

		int x_drop = ((rsListPosition.get(0).x) - 15 < 0) ? 0 : (rsListPosition.get(0).x) - 15;
		int y_drop = ((rsListPosition.get(0).y) - 15 < 0) ? 0 : (rsListPosition.get(0).y) - 15;

//		System.out.println("x_drop: " + x_drop);
//		System.out.println("y_drop: " + y_drop);

		int w_drop_temp = rsListPosition.get(0).width * 50;
		int w_drop = ((w_drop_temp + x_drop) >= table.width()) ? table.width() - x_drop - 1 : w_drop_temp - 1;

		int h_drop_temp = rsListPosition.get(0).height * 55;
		int h_drop = ((h_drop_temp + y_drop) >= table.height()) ? table.height() - y_drop - 1 : h_drop_temp - 1;

//		System.out.println("w_drop: " + w_drop);
//		System.out.println("h_drop: " + h_drop);
		Mat drop_Mat = new Mat(table, new Rect(x_drop, y_drop, w_drop, h_drop));
//		System.err.println("end đáp án\n");
		return drop_Mat;
	}
}