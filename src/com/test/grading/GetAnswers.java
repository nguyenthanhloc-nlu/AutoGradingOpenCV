package com.test.grading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.test.model.Line;
import com.test.process.MatProcess;
import com.test.process.PositonSquare;
import com.test.process.RectCompareNoise;
import com.test.process.SortMatOfPoint;



public class GetAnswers {
	public static final int W_SQUARE_MAX = 30;
	public static final int H_SQUARE_MAX = 30;

	public static final int MARGIN = 30;//21

	public static final int W_ANSWER = 21;
	public static final int H_ANSWER = 21;

	public static final int SPACE_ANSWER = 18;

	public static final int UPPER_BOUND_Y = 700;
	public static final int LOWER_BOUND_Y = 50;

	public static final int UPPER_BOUND_X = 600;
	public static final int LOWER_BOUND_X = 0;
	
	private static int area_ans = 250;
	private static int thresh = 115;

	public GetAnswers() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}



	/*
	 * 6 ô vuông định vị đáp án
	 */
	public Set<Rect> getPositionAnsewer(Mat src) {
		// lấy contour
		// lấy vị trí 6 ô vuông
		// lọc phần tử gây nhiễu
		Mat copy = new Mat();
		src.copyTo(copy);
		Set<Rect> rects = new TreeSet<Rect>(RectCompareNoise.RECT_COMPARE_SIX_SQUARE);
		for (int thresh_ = 150; thresh_ >90; thresh_-=4) {
			
			Mat gray = MatProcess.toColorGray(src);
			Mat thresh = MatProcess.toThreshBinary(gray, thresh_);
			List<MatOfPoint> contours = MatProcess.getContour(thresh);
			Imgcodecs.imwrite("anh_6_o_thresh.jpg", thresh);

			// 80, 670
			// xác định 6 tọa độ 6 ô đen, lấy cột đán án
			// ctdl Set không chứa phần tử trùng, dùng để remove nhiễu
			Collections.sort(contours, SortMatOfPoint.MAT_OF_POINT_COMPARE_BY_Y);
			for (int i = 0; i < contours.size(); i++) {
				Rect rect = Imgproc.boundingRect(contours.get(i));
				if (rect.area() > 250 && rect.area() < 800) {
//					Imgproc.rectangle(copy, rect, new Scalar(0, 255, 0));
					if ((rect.y <= LOWER_BOUND_Y || rect.y >= UPPER_BOUND_Y)
							&& (rect.x <= UPPER_BOUND_X && rect.x >= LOWER_BOUND_X) && rect.width < W_SQUARE_MAX
							&& rect.height < H_SQUARE_MAX
							&& Math.abs(rect.height - rect.width) < 5) {
						rects.add(rect);
						Imgproc.rectangle(copy, rect, new Scalar(0, 255, 0));
					}
				}
			}
			Imgcodecs.imwrite("ok/anh_6_o"+thresh_+".jpg", copy);
			
			rects = filter(rects);
			
			
			
			if(rects.size() == 6) {
				this.thresh = thresh_;
				break;
			}
		}
		return rects;
		
	}
	
	public Set<Rect> filter(Set<Rect> rects) {
		
		Set<Rect> result = new TreeSet<Rect>(RectCompareNoise.RECT_COMPARE_SIX_SQUARE);
		List<Rect> data = rects.stream().collect(Collectors.toList());
		if(rects.size() >= 6) {
//			int yMin = data.get(0).y;
//			int yMax = data.get(data.size() - 1).y;
//			for (Rect r : data) {
//				if(Math.abs(r.y - yMin) <20 || Math.abs(r.y - yMax) <20)
//					result.add(r);
//			}
		
			
			for (int i = 0; i < rects.size(); i++) {
				for (int j = i+1; j < rects.size(); j++) {
					for (int l = j+1; l < rects.size(); l++) {
						for (int k = l+1; k < rects.size(); k++) {
							for (int m = k+1; m < rects.size(); m++) {
								for (int n = m+1; n < rects.size(); n++) {
									List<Rect> tmp = new ArrayList<Rect>();
									tmp.add(data.get(i));
									tmp.add(data.get(j));
									tmp.add(data.get(l));
									tmp.add(data.get(k));
									tmp.add(data.get(m));
									tmp.add(data.get(n));
									Set<Rect> result2 = new TreeSet<Rect>(RectCompareNoise.RECT_COMPARE);
									
									
									Rect topLeft = PositonSquare.findRectTopLeft(tmp);
									tmp.remove(topLeft);
									
									Rect bottomRight = PositonSquare.findRectBottomRight(tmp);
									tmp.remove(bottomRight);
									
									Rect bottomLeft = PositonSquare.findRectBottomLeft(tmp);
									tmp.remove(bottomLeft);
									
									Rect topRight = PositonSquare.findRectTopRight(tmp);
									tmp.remove(topRight);
									
									Rect bottomCentral = PositonSquare.findRectBottomCentral(tmp);
									tmp.remove(bottomCentral);
									
									Rect topCentral = PositonSquare.findRectTopCentral(tmp);
									tmp.remove(topCentral);
									
									// khoảnh cách 2 ô
									int width1 = topCentral.x - topLeft.x; // 1
									int width2 = topRight.x - topCentral.x; // 2
									int width3 = bottomCentral.x - bottomLeft.x; //3
									int width4 = bottomRight.x - bottomCentral.x; //4
									
									int height1 = bottomLeft.y - topLeft.y;
									int height2 = bottomCentral.y - topCentral.y;
									int height3 = bottomRight.y - topRight.y;
									
//									System.out.println(this.thresh);
//									System.out.println("width1: " +width1);
//									System.out.println("width2: " +width2);
//									System.out.println("width3 " +width3);
//									System.out.println("width4: " +width4);
//									
//									System.out.println("***************");
									
									
									if(Math.abs(width1 - width3) < 40 && //60
											Math.abs(width3 - width1) < 40 && //60
											Math.abs(width4 - width2) < 40 && //60
											Math.abs(height2 - height1) < 60 &&
											Math.abs(height3 - height1) < 40 &&
											Math.abs(height3 - height2) < 40 &&
											Math.abs(bottomLeft.x - topLeft.x) < 40 &&
											Math.abs(bottomCentral.x - topCentral.x) < 40 && //60
											Math.abs(bottomRight.x - topRight.x) < 40 &&
											
											Math.abs(bottomLeft.y - bottomCentral.y) < 20 &&
											Math.abs(bottomRight.y - bottomCentral.y) < 20 && //20
											Math.abs(topLeft.y - topCentral.y) < 20 &&
											Math.abs(topRight.y - topCentral.y) < 20) {
										
										
										result2.add(topLeft);
										result2.add(bottomRight);
										result2.add(bottomCentral);
										result2.add(bottomLeft);
										result2.add(topCentral);
										result2.add(topRight);
										return result2;
										
									}
									
								}
							}
						}
					}
				}
			}
			
			return result;
			
		}
		return rects;
	}
	
	
	public void getColumnAnswer(Mat table, Mat col_1, Mat col_2, Mat col_3) {
		
		Set<Rect> r = getPositionAnsewer(table);
		Mat copy = new Mat();
		table.copyTo(copy);
		for (Rect rect : r) {
			Imgproc.rectangle(copy, rect, new Scalar(0, 255, 0));
			
		}
		Imgcodecs.imwrite("anh_6_o_ok.jpg", copy);
		System.out.println(r);
		if (r.size() == 6) {
			Rect[] d = new Rect[6];
			r.toArray(d);
					

			// width, height column answer
			int width = d[2].x - d[0].x - d[0].width*2;
			int height = d[1].y - d[0].y - d[0].height;
			if(d[0].width > 22)
				area_ans = (d[0].width-10) * (d[0].height-10);
			else
				area_ans = (d[0].width-5) * (d[0].height-5);
			// rotate
			
			// column 1
			Point p1 = new Point(d[0].x, d[0].y);
			Point p2 = new Point(d[1].x, d[1].y);
			int iX = d[0].x +width/2;
			int iY = (d[1].y - d[0].y) / 2 + d[0].y;
			double angle = MatProcess.computeAngleRotate(p1, p2, iX, iY);
			Mat table2 = MatProcess.rotate(table, angle);
			Point p11 = MatProcess.pointAfterRotate(p1, -angle, new Point(iX, iY));
			Mat col1 = new Mat(table2, new Rect((int)p11.x +d[0].width, (int)p11.y +d[0].height, width, height));
			col1.copyTo(col_1);
			
			
		

			// column 2
			// x = x3, y = y3, w = x5 - x3, h = y4 - y3
			p1 = new Point(d[2].x, d[2].y);
			p2 = new Point(d[3].x, d[3].y);
			iX = d[2].x + width/2;
		
			
			angle = MatProcess.computeAngleRotate(p1, p2, iX, iY);
			System.out.println(angle + "cot2");
			Mat table3 = MatProcess.rotate(table, angle);
			Point p22 = MatProcess.pointAfterRotate(p1, -angle, new Point(iX, iY));
			Mat col2 = new Mat(table3, new Rect((int)p22.x +d[2].width, (int)p22.y +d[2].height, width, height));
			col2.copyTo(col_2);
			

			// column 3
			// x = x5, y = y5,
			p1 = new Point(d[4].x, d[4].y);
			p2 = new Point(d[5].x, d[5].y);
			iX = d[5].x + width/2;
			angle = MatProcess.computeAngleRotate(p1, p2, iX, iY);
			table2 = MatProcess.rotate(table, angle);
			Point p33 = MatProcess.pointAfterRotate(p1, -angle, new Point(iX, iY));
			if(table2.width() - p33.x - d[4].width < width)
				width =(int) (table2.width() - p33.x - d[4].width);
			Mat col3 = new Mat(table2, new Rect((int)p33.x + d[4].width , (int)p33.y+d[4].height, width, height));
			col3.copyTo(col_3);
			
			Imgcodecs.imwrite("col_2.jpg", table2);
			System.out.println(p1 +" " +p2);
			System.out.println(new Point(iX, iY));
			System.out.println(p33);
			System.out.println(angle);
		}

	}

	//// 1 ô 22*22
	// line 18
	public Map<Integer, Line> getRowAnswer(Mat colAnswer, int start) {
		Map<Integer, Line> result = new HashMap<Integer, Line>();
		while(true) {
			System.out.println(start);
			result.clear();
			Mat gray = MatProcess.toColorGray(colAnswer);
			Mat thresh = new Mat();
			thresh = MatProcess.toThreshBinary(gray,this.thresh);
	
			List<MatOfPoint> contours = MatProcess.getContour(thresh);
			System.out.println(contours.size() +"size");
			// remove contour có area() < 250
			System.out.println("area "+area_ans);
			removeContourHasAreaLess_N(contours, area_ans);
			
		
			if(contours.size() > 34) {
				this.thresh -=10;
				continue;
			}
			// sort theo y, duyệt từ trên xuống
			Collections.sort(contours, SortMatOfPoint.MAT_OF_POINT_COMPARE_BY_Y);
	
			if (contours.isEmpty())
				return result;
	
			int lineCurrent = 0;
			double lineHeight = (double) (colAnswer.height() ) / 17;
			
	
			while (lineCurrent < 17 && contours.size() > 0) {
				lineCurrent++;
				// lấy ra contour có y min là ô đáp án tiếp theo
				Rect min = Imgproc.boundingRect(contours.get(0));
	
				// những dòng khoanh lớn hơn 2 ô, bỏ qua
				boolean pause = rowHasTwoAnswer(contours, min);
	
				// remove nhiễu theo y, lấy sai số 21 - height of bounding answer
				removeNoiseAroundAnswer(contours, min.y);
				if (pause)
					continue;
	
	//			int line = (int) Math.round((((double) (min.y) / lineHeight) + start + 1));
				int line = (int) Math.round((double)(min.y / lineHeight))+ start + 1;
	//			System.out.println(lineHeight);
				System.out.print((double)(min.y / lineHeight) + "   ");
				System.out.print(Math.round((double)(min.y / lineHeight)));
				Imgproc.rectangle(colAnswer, min, new Scalar(0, 255, 0));
				// truong hợp k tô
				if(line - (lineCurrent+start) == 1 ) 
					line = lineCurrent+start;
				System.out.println("   "+" "+min +"  " +line );
				addAnswer(result, line, min);
			}
			Imgcodecs.imwrite(start+".jpg", thresh);
			Imgcodecs.imwrite(start+"src.jpg", colAnswer);
			break;
		}
		
		return result;
	}

	public void removeContourHasAreaLess_N(List<MatOfPoint> contours, int N) {
		for (int i = 0; i < contours.size(); i++) {
			Rect rect = Imgproc.boundingRect(contours.get(i));
			if (rect.area() < N || rect.area() > 1000 || rect.width > 35 || rect.height > 35)
				contours.remove(i--);
		}
	}

	public void removeNoiseAroundAnswer(List<MatOfPoint> contours, int y) {
		for (int i = 0; i < contours.size(); i++) {
			Rect rect = Imgproc.boundingRect(contours.get(i));
			if (Math.abs(y - rect.y) < 21)// +21 sai số
				contours.remove(i--);
		}
	}

	public boolean rowHasTwoAnswer(List<MatOfPoint> contours, Rect answer) {
		for (int i = 1; i < contours.size(); i++) {
			Rect rect = Imgproc.boundingRect(contours.get(i));
			if (Math.abs(answer.x - rect.x) > 21 && Math.abs(answer.y - rect.y) < 21) { // +21 sai số
				return true;
			}
		}
		return false;
	}

	public String converNumToTextAns(int n) {
		if (n == 3)
			return "D";
		if (n == 2)
			return "C";
		if (n == 1)
			return "B";
		return "A";
	}

	public void addAnswer(Map<Integer, Line> output, int line, Rect rectAns) {
		int x = rectAns.x;
		
		// 21 là ô
		// khoảng cách ô
		for (int i = 3; i >0; i--) {
			if (MARGIN + SPACE_ANSWER * (i - 1) + W_ANSWER * i < x) {
				System.out.println(line);
				output.put(line, new Line(line, converNumToTextAns(i)));
				return;
			}
		}
		// cuối cùng về A
		System.out.println(line);
		if(x > 10)
			output.put(line, new Line(line, converNumToTextAns(0)));
	}

	public Map<Integer, Line> getAnswers(Mat src) {

		Mat table2 = new Mat();
		Imgproc.resize(src, table2, new Size(800, 800));

		Mat col1 = new Mat();
		Mat col2 = new Mat();
		Mat col3 = new Mat();
		getColumnAnswer(table2, col1, col2, col3);
		Imgcodecs.imwrite("result_test3.jpg", table2);
		
		Map<Integer, Line> result = new TreeMap<Integer, Line>();
		result.putAll(getRowAnswer(col1, 0));
		result.putAll(getRowAnswer(col2, 17));
		result.putAll(getRowAnswer(col3, 34));
		System.out.println("thresh: "+thresh);
		
		
		return result;
	}

}
