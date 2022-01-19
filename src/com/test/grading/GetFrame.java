package com.test.grading;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;

import com.test.model.Line;
import com.test.model.Paper;
import com.test.process.Plantain_StudentID;

public class GetFrame {

	Paper paper = new Paper();
	GetIdExamFrame getIdExamFrame = new GetIdExamFrame();
	GetIdStudentFrame getIdStudentFrame = new GetIdStudentFrame();
	GetResltExamFrame getResltExamFrame = new GetResltExamFrame();
	GetAnswers getAnswers = new GetAnswers();

	public void execute(Mat src, Mat idExam, Mat idStudent, Mat resultExam) {
		executeIdExam(src, idExam);

		executeIdStudent(src, idStudent);

		executeResult(src, resultExam);
	}

	public void executeIdExam(Mat src, Mat idExam) {
		Mat get_Area = paper.getRectIn4RectForId(src);
		getIdExamFrame.getIDExameFrame(get_Area).copyTo(idExam);
	}

	public void executeIdStudent(Mat src, Mat idStudent) {
		Mat get_Area = paper.getRectIn4RectForId(src);
		getIdStudentFrame.getIDStudentFrame(get_Area).copyTo(idStudent);
	}

	public void executeResult(Mat src, Mat resultExam) {
		List<Rect> listRect = paper.getPositionPaperForId_Exam(src);
		Mat get_Area = paper.getRectIn4RectForResult2(listRect, src);
		getResltExamFrame.getResultFrame(get_Area).copyTo(resultExam);
	}

}