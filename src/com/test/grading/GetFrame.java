package com.test.grading;

import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import com.test.model.Paper;

public class GetFrame {

	Paper paper = new Paper();
	IdExamFrame getIdExamFrame = new IdExamFrame();
	IdStudentFrame getIdStudentFrame = new IdStudentFrame();
	ResltExamFrame getResltExamFrame = new ResltExamFrame();
	GetAnswers getAnswers = new GetAnswers();

	public void execute(Mat src, Mat idExam, Mat idStudent, Mat resultExam) {

		System.err.println("GetFrame: execute");

		executeIdExamAndStudent(src, idExam, idStudent);

		executeResult(src, resultExam);
	}

	public void executeIdExamAndStudent(Mat src, Mat idExam, Mat idStudent) {

		System.err.println("GetFrame: executeIdExam");

		Mat get_Area = paper.getRectIn4RectForId(src);
		Imgcodecs.imwrite("src/img/a-rect-in-4-rect-id-exam-n-student.jpg", get_Area);

		getIdExamFrame.getIDExameFrame(get_Area).copyTo(idExam);
		Imgcodecs.imwrite("src/img/rs-ma-de.jpg", idExam);

		getIdStudentFrame.getIDStudentFrame(get_Area).copyTo(idStudent);
		Imgcodecs.imwrite("src/img/rs-mssv.jpg", idStudent);
	}

	public void executeResult(Mat src, Mat resultExam) {

		System.err.println("GetFrame: executeResult");

		List<Rect> listRect = paper.getPositionPaperForId_Exam(src);
		Mat get_Area = paper.getRectIn4RectForResult(listRect, src);

		Imgcodecs.imwrite("src/img/a-rect-in-4-rect-result.jpg", get_Area);

		getResltExamFrame.getAnswerFrame(get_Area).copyTo(resultExam);
		Imgcodecs.imwrite("src/img/rs-dap-an.jpg", resultExam);

	}
}