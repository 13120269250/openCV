package match;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.features2d.DescriptorMatcher;

import bean.MatchItem;

/**
 * @Description Opencv特征点算法
 */
public class OpenCv_FeatureAlgorithm {

	public static List<MatchItem> matchInner(Mat picDescMat, String descRoot) {

		// 加载的文件
		String[] descFileNames = null;
		try {
			descFileNames = new File(descRoot).list();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (descFileNames == null) {
			return null;
		}

		// 逐一匹配
		DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_L1);
		List<MatchItem> matchItems = new ArrayList<MatchItem>();
		long fileCheckTimeSave;
		
		long readFileDuration = 0;
		long matchDuration = 0;
		long allTimeSave = System.currentTimeMillis();
		
		int totailMatchCount = 0;
		for (int i = 0; i < descFileNames.length; i++) {
			fileCheckTimeSave = System.currentTimeMillis();
			String descName = descFileNames[i];
			String descPath = descRoot + File.separator + descName;
			
			File descFile = new File(descPath);
			if (!descFile.exists()) {
				System.out.println("文件 " + descFile.getAbsolutePath() + " 不存在");
				continue;
			}
			
			//如果是文件夹 递归处理
			if (descFile.isDirectory()) {
				List<MatchItem> tempMatchItems = null;
				tempMatchItems = matchInner(picDescMat, descRoot + File.separator + descName);
				if (tempMatchItems != null) {
					matchItems.addAll(tempMatchItems);
				}
				continue;
			}
			
			if (!descName.endsWith(".desc")) {
				continue;
			}

			System.out.println("描述文件位置: " + descPath);
			
			long desc_start = System.currentTimeMillis();
			Mat descriptors2 = DescMatReader.loadFile(descFile.getAbsolutePath()); //描述文件读为mat
			
			long desc_end = System.currentTimeMillis();
			System.out.println("load desc mat time:" + (desc_end - desc_start) + ",mat:" + descriptors2);
			
			if (descriptors2 == null) {
				System.out.println("" + descFile + " 描述文件 无法被创建");
				continue;
			}
			
			readFileDuration += System.currentTimeMillis() - fileCheckTimeSave;
			long eachmatchtime_start = System.currentTimeMillis();
			MatOfDMatch matOfDMatch = new MatOfDMatch();
			matcher.match(picDescMat, descriptors2, matOfDMatch); //描述文件与 源文件的match文件的mat
			MatOfDMatch goodMatch = MatchValueAlg.getGoodMatch(matOfDMatch); //mat进一步的处理
			int value = MatchValueAlg.calcValueFromGoodMatch(goodMatch); // 算出匹配值 值越大越相似
			long eachmatchtime_end = System.currentTimeMillis();
			System.out.println("each match time:" + (eachmatchtime_end - eachmatchtime_start));

			if (value != 0) {
				MatchItem matchItem = new MatchItem(descPath, null, value, descName);
				matchItems.add(matchItem);
			}
			
			// 释放资源
			descriptors2.release();
			matOfDMatch.release();
			goodMatch.release();
			totailMatchCount++;
		}
		
		Collections.sort(matchItems);
		
		System.out.println("total match desc count:" + totailMatchCount);
		System.out.println("~~~~ [fileDuration " + readFileDuration + "] [match duration " + matchDuration + "]");
		System.out.println("~~~~ [allDuration " + (System.currentTimeMillis() - allTimeSave) + "]");
		System.out.println("~~~~~ match size:" + matchItems.size());
		
		
		/*//只返回前3条数据
		List<MatchItem> subMatchItems = null;
		if(matchItems.size() > 3){
			subMatchItems = matchItems.subList(0, 3);
		}else {
			subMatchItems = matchItems;
		}*/
		return matchItems;
	}

}
