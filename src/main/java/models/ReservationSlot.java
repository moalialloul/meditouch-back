package models;

public class ReservationSlot {
	int reservationId;
	int slotFk;
	int userFk;

	public String toString() {
		return "{\"reservationId\":" + reservationId + ", \"slotId\":" + slotFk + ", \"userFk\":" + userFk + "}";
	}

	public ReservationSlot(int reservationId, int slotFk, int userFk) {
		super();
		this.reservationId = reservationId;
		this.slotFk = slotFk;
		this.userFk = userFk;
	}

	public ReservationSlot() {

	}

	public int getReservationId() {
		return reservationId;
	}

	public void setReservationId(int reservationId) {
		this.reservationId = reservationId;
	}

	public int getSlotFk() {
		return slotFk;
	}

	public void setSlotFk(int slotFk) {
		this.slotFk = slotFk;
	}

	public int getUserFk() {
		return userFk;
	}

	public void setUserFk(int userFk) {
		this.userFk = userFk;
	}
}
