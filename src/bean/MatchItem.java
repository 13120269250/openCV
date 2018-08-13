package bean;

/**
 * @Description 比对结果
 */
public class MatchItem implements Comparable<MatchItem>{
	
	public String DescPath;
	public String BitmapPath;
	public int value;
	public String descName;
	
	public MatchItem(String descPath, String bitmapPath, int value, String descName) {
		super();
		DescPath = descPath;
		BitmapPath = bitmapPath;
		this.value = value;
		this.descName = descName;
	}
	
	public String getDescPath() {
		return DescPath;
	}
	
	public void setDescPath(String descPath) {
		DescPath = descPath;
	}
	
	public String getBitmapPath() {
		return BitmapPath;
	}

	public void setBitmapPath(String bitmapPath) {
		BitmapPath = bitmapPath;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public String getDescName() {
		return descName;
	}
	
	public void setDescName(String descName) {
		this.descName = descName;
	}
	
	@Override
	public int compareTo(MatchItem another) {
		return another.value - value;
	}
	
}

	
