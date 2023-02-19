package models;

public class PaginationProps {
	int pageNumber;
	int recordsByPage;

	public PaginationProps(int pageNumber, int recordsByPage) {
		super();
		this.pageNumber = pageNumber;
		this.recordsByPage = recordsByPage;
	}

	public PaginationProps() {

	}

	@Override
	public String toString() {
		return "{\"pageNumber\":" + pageNumber + ", \"recordsByPage\":" + recordsByPage + "]";
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getRecordsByPage() {
		return recordsByPage;
	}

	public void setRecordsByPage(int recordsByPage) {
		this.recordsByPage = recordsByPage;
	}
}
