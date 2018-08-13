package match;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.opencv.core.Mat;

public class DescMatReader {
	
	public static Mat loadFile(String descPath) {
		if (descPath.endsWith(".desc")) {
			return loadFileWithoutSuffix(descPath);
		} else if (new File(descPath).exists()){
			return loadFileWithoutSuffix(descPath);
		} else {
			return loadFileWithoutSuffix(descPath + ".desc");
		}
	}
	
	@SuppressWarnings("unused")
	public static Mat loadFileWithoutSuffix(String descPath) {
		Mat descriptionMat = null;

        File file = new File(descPath);
        if (!file.exists())
        {
            return descriptionMat;
        }
        
        DataInputStream in = null;
        try
        {
        	in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        	
        	int edition = in.readInt();
			int rows = in.readInt();
			int cols = in.readInt();
			int type = in.readInt();
			int depth = in.readInt();
			int channel = in.readInt();
			int dataCount = in.readInt();
			
			//将图片路径和byte[] 缓存，如果有修改，则需要更新缓存,TODO
			byte[] bytes = DescCache.getInstance().getDesc(descPath);
			if(null == bytes || bytes.length == 0){
				bytes = loadByte(in,dataCount);
				if(null != bytes && bytes.length > 0){
					DescCache.getInstance().put(descPath, bytes);
				}
			}
        	descriptionMat = getMat(bytes, rows, cols, type);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
        	if (in != null) {
        		try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
		return descriptionMat;
	}
	
	public static Mat get(byte[] data) {
		DataInputStream in = null;
		try {
			in = new DataInputStream(new ByteArrayInputStream(data));
			return load(in);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("unused")
	public static Mat load(DataInputStream in) {
		try {
			int edition = in.readInt();
			int rows = in.readInt();
			int cols = in.readInt();
			int type = in.readInt();
			int depth = in.readInt();
			int channel = in.readInt();
			int dataCount = in.readInt();
			
			//byte[] bytes = loadByte(in, rows, cols, type, dataCount);
			byte[] bytes = loadByte(in,dataCount);
			return getMat(bytes,rows, cols, type);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	//private static byte[] loadByte(DataInputStream in, int rows, int cols, int type, int dataCount) {
	private static byte[] loadByte(DataInputStream in,int dataCount) {
		try {
			//Mat mat = new Mat(rows, cols, type);
			// TODO 修改算法，不能一次创建那么大的缓存byte[]
			byte[] data = new byte[dataCount];
			in.read(data);
			//mat.put(0, 0, data);
			return data;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private static Mat getMat(byte[] bytes, int rows, int cols, int type) {
		try {
			Mat mat = new Mat(rows, cols, type);
			mat.put(0, 0, bytes);
			return mat;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
