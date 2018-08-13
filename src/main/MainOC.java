package main;

import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import bean.MatchItem;
import match.DescCreater;
import match.FeatureDescriptor;
import match.OpenCv_FeatureAlgorithm;

public class MainOC {
	
	//引入c运行库_java320.dll
	static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
	
	public static void main(String[] args) {
		
		//生成描述文件
		createDesc();
	
		//进行比对
		match();
		
		
	}
	
	public static void createDesc() {
		//图片根路径
		String picDirPath = "C:/Users/Administrator/Desktop/picDirPath";
		//生成的描述文件根路径
		String descDirPath = "C:/Users/Administrator/Desktop/descDirPath";
		//创建描述文件
		DescCreater.getInstance().BatchCreater(picDirPath, descDirPath);
	}
	
	public static void match() {
		long currentTimeMillis = System.currentTimeMillis();
		// 图片文件路径
		String imgPath = "C:/Users/Administrator/Desktop/person.jpg";
		// 比对文件根路径
		String descPath = "C:/Users/Administrator/Desktop/descDirPath";
		// 生成Opencv的二维矩阵类
		Mat picDescMat = new FeatureDescriptor().calcFeatureDescription(imgPath, descPath);
		// 特征点算法
		List<MatchItem> matchItems = OpenCv_FeatureAlgorithm.matchInner(picDescMat, descPath);
		long currentTimeMillis2 = System.currentTimeMillis();
		System.out.println("耗时："+(currentTimeMillis2 - currentTimeMillis)+"");
		System.out.println("匹配度最高的图片分别是：");
		for (MatchItem matchItem : matchItems) {
			System.out.println("------------------------------------------");
			System.out.println("名称：" + matchItem.getDescName() + ",得分：" + matchItem.getValue());
		}
	}
	
	
	
}