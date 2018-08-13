package match;

import java.io.File;
import java.io.FilenameFilter;

import org.opencv.core.Mat;

import util.PicDescNameUtil;

/**
 * @Description 描述文件生成类
 */
public class DescCreater {

	public static final int RESULT_NONE = 0; //文件为空
	public static final int RESULT_SUCCESS = 1; //生成成功
	public static final int RESULT_FAIL = 2; //生成失败
	public static final int RESULT_HALF = 3; //部分文件生成成功
	
	private static DescCreater sInstance;
	
	public static DescCreater getInstance() {
		if (sInstance == null) {
			synchronized (DescCreater.class) {
				if (sInstance == null) {
					sInstance = new DescCreater();
				}
			}
		}
		return sInstance;
	}
	
	/**
	 * @Description 批量生成描述文件（可以是文件夹或者单个文件）
	 * @param picDirPath 源文件的路径    descDirPath 生成后的描述文件存放路径
	 * @return int 生成结果
	 */
	public int BatchCreater(String picDirPath, String descDirPath) {
		synchronized (DescCreater.class) {
			if (picDirPath == null || descDirPath == null) {
				System.out.println("源文件或存放文件路径为空");
				return RESULT_FAIL;
			}
			
			File picDirFile = new File(picDirPath);
			if (!picDirFile.exists()) {
				System.out.println("picDirPath 文件夹不存在");
				return RESULT_FAIL;
			}
			
			//对文件夹和单独的文件分别判断和处理
			File[] files = null;
			if(picDirFile.isDirectory()){
				files = picDirFile.listFiles();
			}else{
				files = new File[]{picDirFile};
			}
			
			int fileCount = files.length;
			if (fileCount == 0) {
				return RESULT_NONE;
			}
			
			//处理状态
			boolean hasSuccess = false;
			boolean hasFail = false;
			
			FeatureDescriptor featureDescriptor = new FeatureDescriptor();
			for (int i = 0; i < fileCount; i++) {
				File bitmapFile = files[i];
				
				// 如果是文件夹则递归处理
				if (bitmapFile.isDirectory()) {
					String name = bitmapFile.getName();
					String newDescDirPath = descDirPath + name;
					BatchCreater(name, newDescDirPath);
					continue;
				}
				
				//生成.desc结尾的文件
				String descFileName = PicDescNameUtil.convertBitmapNameToDesc(bitmapFile.getName());
				if (descFileName == null) {
					System.out.println(bitmapFile.getAbsolutePath() + "~文件不存在 ");
					continue;
				}
				
				String descFilePath = descDirPath + File.separator + descFileName;
				boolean isSuccess = saveItem(featureDescriptor, bitmapFile.getAbsolutePath(), descFilePath);
				
				if (isSuccess) {
					hasSuccess = true;
				} else {
					hasFail = true;
				}
			}
			
			// 有一些遗留的临时文件需要删除
			if(picDirFile.isDirectory()){
				String[] tempFilePaths = picDirFile.list(sBitmapFileFilter);
				for (String tempFile : tempFilePaths) {
					new File(tempFile).delete();
				}
			}
			
			if (hasSuccess && !hasFail) {
				return RESULT_SUCCESS;
			} else if (hasSuccess && hasFail) {
				return RESULT_HALF;
			} else {
				return RESULT_FAIL;
			}
		}
	}
	
	/**
	 * @Description 将图片生成描述文件
	 * @param featureDescriptor  bitmapPath 源图片路径  descPath 目标desc文件路径
	 * @return boolean 
	 */
	public boolean saveItem(FeatureDescriptor featureDescriptor, String bitmapPath, String descPath) {

		if (!bitmapPath.toLowerCase().endsWith(".jpg") && !bitmapPath.toLowerCase().endsWith(".png")) {
			System.out.println(" [" + bitmapPath + "] 图片不是jpg或png格式");
			return false;
		}
		
		File bitmapFile = new File(bitmapPath);
		File descFile = new File(descPath);
		if (descFile.exists() && descFile.lastModified() > bitmapFile.lastModified()) { //lastModified 生成时间
			System.out.println(bitmapPath + "~目标desc文件已存在");
			return true;
		}
		
		// 生成desc缓存
		String parentPath = new File(descPath).getParent();
		Mat descMat = featureDescriptor.calcFeatureDescription(bitmapPath, parentPath);
		if (descMat == null) {
			System.out.println("无法得到描述点集合 " + bitmapPath);
			return false;
		}
				
		// 保存成desc目标文件
		boolean isSuccess = DescMatWriter.saveFile(descMat, descPath);
		descMat.release();
		return isSuccess;
	}
	
	
	/**
	 * 文件后缀名过滤
	 */
	private static FilenameFilter sBitmapFileFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".jpg");
		}
	};
	
}
