package com.test.grading;

import java.util.List;

import org.opencv.core.Rect;

public class PositonSquare {
	
	public static Rect findRectTopLeft(List<Rect> rects) {
		int minValue = Integer.MAX_VALUE;
		Rect result = null;
		for (Rect rect : rects) {
			if(rect.x + rect.y < minValue) {
				result = rect;
				minValue = rect.x + rect.y;
			}
		}
		return result;
	}
	
	public static Rect findRectBottomRight(List<Rect> rects) {
		int maxValue = Integer.MIN_VALUE;
		Rect result = null;
		for (Rect rect : rects) {
			if(rect.x + rect.y > maxValue) {
				result = rect;
				maxValue = rect.x + rect.y;
			}
		}
		return result;
	}
	public static Rect findRectTopRight(List<Rect> rects) {
		int maxValue = Integer.MIN_VALUE;
		Rect result = null;
		for (Rect rect : rects) {
			if(rect.x > maxValue) {
				result = rect;
				maxValue = rect.x;
			}
		}
		return result;
	}
	
	public static Rect findRectBottomLeft(List<Rect> rects) {
		int minValue = Integer.MAX_VALUE;
		Rect result = null;
		for (Rect rect : rects) {
			if(rect.x < minValue) {
				result = rect;
				minValue = rect.x;
			}
		}
		return result;
	}
	
	public static Rect findRectBottomCentral(List<Rect> rects) {
		int minValue = Integer.MIN_VALUE;
		Rect result = null;
		for (Rect rect : rects) {
			if(rect.y > minValue) {
				result = rect;
				minValue = rect.y;
			}
		}
		return result;
	}
	
	public static Rect findRectTopCentral(List<Rect> rects) {
		int minValue = Integer.MAX_VALUE;
		Rect result = null;
		for (Rect rect : rects) {
			if(rect.y < minValue) {
				result = rect;
				minValue = rect.y;
			}
		}
		return result;
	}

}
