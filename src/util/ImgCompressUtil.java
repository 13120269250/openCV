package util;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.mortennobel.imagescaling.ResampleOp;

/**
 * @Description 图片压缩
 */
public class ImgCompressUtil {
	
	private static int COMPRESS_IMG_WIDTH = 200;
	
	/**
	 * 压缩算法2
	 * @param srcImgPath 源文件路径	
	 * @param destImgPath 压缩文件路径
	 * @return boolean
	 */
	public static boolean compress2(String srcImgPath, String destImgPath) {
		return compress2(srcImgPath, destImgPath, COMPRESS_IMG_WIDTH, 0);
	}
	
	public static boolean compress2(String srcImgPath, String destImgPath, int w, int h) {
		try {
			// 读入文件
			File file = new File(srcImgPath);
			// 构造Image对象
			BufferedImage srcImg = ImageIO.read(file);
			// 得到源图宽
			int width = srcImg.getWidth(null); 
			// 得到源图高
			int height = srcImg.getHeight(null); 
	
			//比限定值大的图片转成限定值的大小 并按比例改变宽高
			if (height == 0 || h == 0 || width / height > w / h) {
				// resize by width
				if (w > width) {
					w = width;
					h = height;
				}
				h = (int) (height * w / width);
			} else {
				// resize by height
				if (h > height) {
					w = width;
					h = height;
				}
				w = (int) (width * h / height);
			}
			
			if (w == 0 || h == 0) {
				System.out.println("ImgCompress error，限定值为 0！设置错误");
				return false;
			}
			
			ResampleOp resizeOp = new ResampleOp(w, h);
			BufferedImage resizedImage = resizeOp.filter(srcImg, null);
			return ImageIO.write(resizedImage, "jpeg", new File(destImgPath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}

