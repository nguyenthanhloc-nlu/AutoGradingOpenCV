package com.test.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import com.test.grading.GetAnswers;
import com.test.grading.GetFrame;
import com.test.model.Line;

public class test2 {
	public static void main(String[] args) throws IOException {
		String file = "";
		File f = new File("d:\\dapan.txt");
//		f.createNewFile();
		PrintWriter out = new PrintWriter(new FileOutputStream("d:\\\\dapan.txt"));
		for (int i = 1; i <= 100; i++) {
			String name = i + "";
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

//			String filePath = "src/rs-dap-an-.jpg";
			String filePath = "src/img/" + i + ".jpg";
			Mat src = Imgcodecs.imread(filePath);

			Mat idExam = new Mat();
			Mat idStudent = new Mat();
			Mat resultExam = new Mat();
			try {
				// xử lý cắt ảnh
				GetFrame getFrame = new GetFrame();
				getFrame.execute(src, idExam, idStudent, resultExam);
				System.out.println("height " + src.height());

				// lấy kết quả
				GetAnswers getAns = new GetAnswers();
				Map<Integer, Line> ok = getAns.getAnswers(resultExam);
				print(ok);
				out.println("hinh "+i + "  tổng câu:"+ok.size());
				out.println(text(ok));
				out.println();
				out.println("--------------------------------------------");
				out.flush();
			} catch (Exception e) {
				file += i + ", " +e.getMessage() +"\n";
			}

			
		}
		System.out.println(file);
		out.close();

	}

	public static void print(Map<Integer, Line> ok) {
		for (Entry<Integer, Line> en : ok.entrySet()) {
			System.out.println(en.getKey() + "  " + en.getValue().getValue());
		}
	}
	
	public static String text(Map<Integer, Line> ok) {
		String line = "";
		for (Entry<Integer, Line> en : ok.entrySet()) {
			System.out.println(en.getKey() + "" + en.getValue().getValue());
			line+=en.getKey() + "  " + en.getValue().getValue() +" \t";
		}
		return line;
	}
}
