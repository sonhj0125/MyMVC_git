package myshop.domain;

// VO(Value Object) == DTO(Data Transfer Object)

public class ImageVO {
	
	// ===== field =====
	private int imgno;
	private String imgfilename;
	
	
	// ===== method (getter, setter) =====
	public int getImgno() {
		return imgno;
	}
	
	public void setImgno(int imgno) {
		this.imgno = imgno;
	}
	
	public String getImgfilename() {
		return imgfilename;
	}
	
	public void setImgfilename(String imgfilename) {
		this.imgfilename = imgfilename;
	}
	
	
}
