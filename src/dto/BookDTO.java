package dto;
import dto.BookDTO;
import dao.BookDAO;

public class BookDTO {
	private String BCode;
	private String BCategory;
	private String BTitle;
	private String BWriter;
	private String BPub;
	private int BYear;
	private String BIntro;
	private int BRes;
	private String reserve;
	
	public BookDTO(String bCode, String bCategory, String bTitle, String bWriter, String bPub, int bYear,
			String bIntro, int bRes) {
		super();
		BCode = bCode;
		BCategory = bCategory;
		BTitle = bTitle;
		BWriter = bWriter;
		BPub = bPub;
		BYear = bYear;
		BIntro = bIntro;
		BRes = bRes;
	}
	
	public String getBCode() {
		return BCode;
	}
	public void setBCode(String bCode) {
		BCode = bCode;
	}
	public String getBCategory() {
		return BCategory;
	}
	public void setBCategory(String bCategory) {
		BCategory = bCategory;
	}
	public String getBTitle() {
		return BTitle;
	}
	public void setBTitle(String bTitle) {
		BTitle = bTitle;
	}
	public String getBWriter() {
		return BWriter;
	}
	public void setBWriter(String bWriter) {
		BWriter = bWriter;
	}
	public String getBPub() {
		return BPub;
	}
	public void setBPub(String bPub) {
		BPub = bPub;
	}
	public int getBYear() {
		return BYear;
	}
	public void setBYear(int bYear) {
		BYear = bYear;
	}
	public String getBIntro() {
		return BIntro;
	}
	public void setBIntro(String bIntro) {
		BIntro = bIntro;
	}
	public int getBRes() {
		return BRes;
	}
	public void setBRes(int bRes) {
		BRes = bRes;
		if(BRes == 1) {
			reserve = "대여 가능";
		} else {
			reserve = "대여중";
		}
	}
	
	@Override
	public String toString() {
		return "BTableDTO [ BCode= " + BCode + ", BCategory= " + BCategory + ", BTitle= " + BTitle +
				", BWriter= " + BWriter + ", BPub= " + BPub + ", BYear+ " +  BYear + ", BIntro= " + BIntro
				+ ", BRes= " + reserve + "]";
	}
	
}
