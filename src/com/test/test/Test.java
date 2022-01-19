package com.test.test;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import com.test.grading.GetAnswers;
import com.test.grading.GetFrame;
import com.test.model.Line;
import com.test.process.Plantain_StudentID;


public class Test {
	public static void main(String[] args) {
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

//		String filePath = "src/rs-dap-an-.jpg";
		String filePath = "src/img/86.jpg";
		Mat src = Imgcodecs.imread(filePath);
		
		Mat idExam = new Mat();
		Mat idStudent = new Mat();
		Mat resultExam = new Mat();

		// xử lý cắt ảnh
		GetFrame getFrame = new GetFrame();
		getFrame.execute(src, idExam, idStudent, resultExam); 
		System.out.println("height "+resultExam.height());

		
		// lấy kết quả
		GetAnswers getAns = new GetAnswers();
		Map<Integer, Line> ok = getAns.getAnswers(resultExam);
		System.out.println("tog cau: "+ok.size());
		print(ok);

//		Plantain_StudentID stu = new Plantain_StudentID(idStudent, true);
//		String MSSV = stu.getCodeID();
//		System.out.println("Mã sinh viên: " +MSSV);
//
//		Plantain_StudentID plan = new Plantain_StudentID(idExam, false);
//		String MaDe =plan.getCodeID();
//		System.out.println("Mã đề: " +MaDe);
	}
	
	
	public static void print(Map<Integer, Line> ok) {
		for (Entry<Integer, Line> en : ok.entrySet()) {
			System.out.println(en.getKey() + "  " + en.getValue().getValue());
		}
	}
}
