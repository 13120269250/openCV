package match;

import java.util.concurrent.ConcurrentHashMap;

public class DescCache {
	
	public static DescCache sInstance;
	
	private ConcurrentHashMap<String,byte[]> descMap = new ConcurrentHashMap<String,byte[]>();
	
	private ConcurrentHashMap<Long,String> tutorialMap = new ConcurrentHashMap<Long,String>();
	
	public static DescCache getInstance() {
		synchronized (DescCache.class) {
			if (sInstance == null) { 
				sInstance = new DescCache();
			}
		}
		return sInstance;
	}
	
	private DescCache() {
		
	}
	
	public ConcurrentHashMap<Long,String> getTutorialMap(){
		return tutorialMap;
	}
	
	public String putTutorialMap(Long key,String value){
		String ret = tutorialMap.get(key);
		if(null == ret){
			ret = tutorialMap.putIfAbsent(key, value);
		}
		return ret;
	}
	
	
	public byte[] getDesc(String key){
		return descMap.get(key);
	}
	
	
	public byte[] put(String key,byte[] bytes){
		byte[] ret = descMap.get(key);
		if(null == ret){
			ret = descMap.putIfAbsent(key, bytes);
		}
		return ret;
	}
	
//	public List<Mat> getDescList() {
//		return mDescMatList;
//	}
	
	public void prepareCacheIfNeed() {
		// TODO
	}
}
