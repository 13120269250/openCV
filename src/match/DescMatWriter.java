package match;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.opencv.core.Mat;

/**
 * @Description 写入描述文件
 */
public class DescMatWriter {

	private static final int EDITION_1 = 1;
	
	/**
	 * 将descMat保存到文件中
	 * @param descriptionMat （该参数是featureDescriptor.calcFeatureDescription返回的点集矩阵）
	 */
	public static boolean saveFile(Mat descriptionMat, String filePath) {
		if (filePath == null) {
			System.out.println("[DescMatWriter.save] 路径为空");
			return false;
		}
		
		if (filePath.endsWith(".desc")) {
			return saveFileWithoutSuffix(descriptionMat, filePath);
		} else {
			return saveFileWithoutSuffix(descriptionMat, filePath + ".desc");
		}
	}

	/**
	 * 将descMat保存到文件中
	 */
	public static boolean saveFileWithoutSuffix(Mat descriptionMat, String filePath) {
		
		if (filePath == null) {
			System.out.println("[DescMatWriter.saveWithoutSuffix] 文件路径为空");
			return false;
		}
		File file = new File(filePath);
		if (!file.exists()) {
			File parentFile = file.getParentFile();
			if (!parentFile.exists() && !parentFile.mkdirs()) {
				System.out.println("[DescMatWriter.saveWithoutSuffix] 根目录 [" + parentFile.getAbsolutePath() + "] 创建失败");
				return false; 
			}
		} else {
			try {
				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

        DataOutputStream out = null;
        try {
            out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            return save(out, descriptionMat);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (out != null) {
                try {
                	out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
		
		return false;
	}
	
	private static boolean save(DataOutputStream out, Mat mat) {
		int rows = mat.rows();
		int cols = mat.cols();
		int type = mat.type();
		int depth = mat.depth();
		int channel = mat.channels();
		int dataCount = rows * cols * channel;
		
		try {
			out.writeInt(EDITION_1);
			out.writeInt(rows);
			out.writeInt(cols);
			out.writeInt(type);
			out.writeInt(depth);
			out.writeInt(channel);
			out.writeInt(dataCount);
			
			return saveByte(out, mat, dataCount);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private static boolean saveByte(DataOutputStream out, Mat mat, int count) {

		try {
			// TODO 修改算法，不能一次创建那么大的缓存byte[]
			byte[] data = new byte[count];
			mat.get(0, 0, data);
			out.write(data);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
		return false;
	}
		
}
