package match;

import java.io.File;
import java.util.Random;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;

import util.ImgCompressUtil;

/**
 * @Description 计算特征数，返回Mat
 */
public class FeatureDescriptor {
	
	private FeatureDetector mFeatureDetector;
	private Random mRandom;
	
	public FeatureDescriptor() {

		mFeatureDetector = FeatureDetector.create(FeatureDetector.DYNAMIC_BRISK);
		mRandom = new Random();
	}
	
	public Mat calcFeatureDescription(String bitmapPath) {
		return calcFeatureDescription(bitmapPath, null);
	}

	/**
	 * 将bitmap生成描述Mat 压缩后转灰度图
	 * @param compressDir 临时压缩文件父路径，路径不能有中文
	 */
	public Mat calcFeatureDescription(String bitmapPath, String compressDir) {
		
		if (mFeatureDetector == null) {
			System.out.println("mFeatureDetector 为空!");
			return null;
		}
		
		if (bitmapPath == null) {
			System.out.println("bitmapPath 路径为空!");
			return null;
		}
		
		File bitmapFile = new File(bitmapPath);
		if (!bitmapFile.exists()) {
			System.out.println("文件[" + bitmapPath + "] 不存在!");
			return null;
		}
		
		Mat mat = null;
		
		if (compressDir != null) {
			if (!new File(compressDir).exists()) {
				if (!new File(compressDir).mkdirs()) {
					System.out.println("压缩文件夹创建失败" + compressDir);
					return null;
				}
			}
			// 压缩文件创建
			String compressFilePath = compressDir + "/temp" + mRandom.nextInt(10000000) + ".jpg";
			boolean compressResult = ImgCompressUtil.compress2(bitmapPath, compressFilePath);
			if (!compressResult) {
				System.out.println("FeatureDescriptor 压缩失败 " + bitmapFile.getAbsolutePath());
				deleteFile(compressFilePath);
				return null;
			}
			
			mat = Imgcodecs.imread(compressFilePath, Imgcodecs.IMREAD_GRAYSCALE);
			deleteFile(compressFilePath);
		} else {
			mat = Imgcodecs.imread(bitmapPath, Imgcodecs.IMREAD_GRAYSCALE);
		}
		
		if (mat == null) {
			System.out.println("mat is null");
			return null;
		}
		
		MatOfKeyPoint keyPoint = new MatOfKeyPoint();
		mFeatureDetector.detect(mat, keyPoint);
		DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.BRISK);
		Mat descriptMat = new Mat();
		extractor.compute(mat, keyPoint, descriptMat);
		
		keyPoint.release();
		mat.release();
		
		return descriptMat;
	}
	
	private static void deleteFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}
}
