package util;

/**
 * @Description 文件名后缀转换工具
 */
public class PicDescNameUtil {
	
	public static String convertBitmapNameToDesc(String bitmapName) {

		int index = bitmapName.toLowerCase().lastIndexOf(".jpg");
		if (index < 0) {
			index = bitmapName.toLowerCase().lastIndexOf(".png");
		}
		if (index > 0) {
			return bitmapName.substring(0, index) + ".desc";
		}
		return null;
	}
	
	public static String convertDescNameToBitmap(String descName) {
		int index = descName.lastIndexOf(".desc");
		if (index > 0) {
			return descName.substring(0, index) + ".jpg"; 
		}
		return descName + ".jpg";
	}
}
