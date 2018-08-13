package match;

import org.opencv.core.DMatch;
import org.opencv.core.MatOfDMatch;

/**
 * 匹配点集的评分算法 
 */
public class MatchValueAlg {
	
//	private static final int GOOD_DISTANCE_THREASHOLD = 2200;
	private static final int GOOD_DISTANCE_THREASHOLD = 2200;
	private static final float ITEM_VALUE_BASE = 0.6f;
	private static final int MIN_VALUE_LINE = 25;
	
	/**
	 * match点集的评分算法
	 * 每个点的distance越小越匹配
	 * 点集的数量越多越匹配
	 * @return 越大越分数越高
	 */
	public static int calcValueFromGoodMatch(MatOfDMatch goodMatch) {
		
		//System.out.println("total:" + goodMatch.total());
		if (goodMatch.total() == 0) {
			return 0;
		}
		DMatch[] dMatchArr = goodMatch.toArray();
		float totalValue = 0f;
		for (DMatch dMatch : dMatchArr) {
			float distance = dMatch.distance <= 1 ? 1 : dMatch.distance;
			totalValue += GOOD_DISTANCE_THREASHOLD / distance + ITEM_VALUE_BASE;
		}
		
		//System.out.println("totalValue:" + totalValue);
		int value = (int) (totalValue * 10);
		//System.out.println("value:" + value);
		return value > MIN_VALUE_LINE ? value : 0;
	}
	
	public static MatOfDMatch getGoodMatch(MatOfDMatch allDMatch) {

//		double max_dist = 0;
//		double min_dist = 800;
//		DMatch[] dMatchArr = matOfDMatch.toArray();
//		for (int i = 0; i < dMatchArr.length; i++) {
//			double dist = dMatchArr[i].distance;
//			if (dist < min_dist)
//				min_dist = dist;
//			if (dist > max_dist)
//				max_dist = dist;
//		}
		
		MatOfDMatch goodMatch = new MatOfDMatch();

		int num = (int) allDMatch.total();
		int channel = allDMatch.channels();
		// TODO 不能一次create这么大的缓存
		float buff[] = new float[num * channel];
		allDMatch.get(0, 0, buff);
		for (int i = 0; i < num; i++) {
			// 每一个channel的第三个变量为distance
			if (buff[channel * i + 3] < GOOD_DISTANCE_THREASHOLD) {
				DMatch dMatch = new DMatch((int) buff[channel * i + 0], (int) buff[channel * i + 1],
						(int) buff[channel * i + 2], buff[channel * i + 3]);
				goodMatch.push_back(new MatOfDMatch(dMatch));
			}
		}
        	
		return goodMatch;
	}
}
