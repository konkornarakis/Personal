
public class Class {

	// number of days
	private int days = 5;

	// max hours in a day
	private int hours = 7;

	@Override
	public String toString() {
		return "Class [days=" + days + ", hours=" + hours + ", name=" + name + "]";
	}

	// default name of a new class
	private String name;

	// timetable of the class
	private Lecture[][] timetable;

	public Class(String name) {
		this.name = name;
		this.timetable = new Lecture[hours][days];
		for (int i = 0; i < timetable.length; i++)
			for (int j = 0; j < timetable[0].length; j++)
				timetable[i][j] = null;
	}

	public int getDays() {
		return days;
	}

	public int getHours() {
		return hours;
	}

	public String getName() {
		return this.name;
	}

	public Lecture[][] getTimetable() {
		return this.timetable;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public void setHours(int hours) {
		this.hours = hours;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTimetable(int hour, int day, Lecture lecture) {
		this.timetable[hour][day] = new Lecture(lecture);
	}

	public void swap(int a, int b, int c, int d) {
		Lecture temp = timetable[a][b];
		timetable[a][b] = timetable[c][d];
		timetable[c][d] = temp;
	}
}