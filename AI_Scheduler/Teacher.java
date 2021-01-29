
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Teacher {

	// id of teacher
	private String ID;

	// name of teacher
	private String name;

	// id's of lessons of teacher
	private String[] lessonID;

	// ???
	private int[] hours;

	// schedule of teacher (create get/set)
	private Lecture[][] schedule;

	// Constructor
	// ??

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Teacher teacher = (Teacher) o;
		return Objects.equals(ID, teacher.ID);
	}

	public Teacher(String ID, String name, String[] lessonID, int[] hours) {
		this.ID = ID;
		this.name = name;
		this.lessonID = lessonID;
		this.hours = hours;
		this.schedule = new Lecture[7][5];
		for (int i = 0; i < schedule.length; i++)
			for (int j = 0; j < schedule[0].length; j++)
				schedule[i][j] = null;
	}

	public Teacher() {
		this.schedule = new Lecture[7][5];
		for (int i = 0; i < schedule.length; i++)
			for (int j = 0; j < schedule[0].length; j++)
				schedule[i][j] = null;
	}

	public Teacher(Teacher teacher) {

		this.ID = teacher.getID();
		this.name = teacher.getName();
		setHours(teacher.getHours());
		setLessonID(teacher.getLessonID());

		this.schedule = new Lecture[7][5];
		for (int i = 0; i < schedule.length; i++)
			for (int j = 0; j < schedule[0].length; j++)
				schedule[i][j] = null;
	}

	public void setSchedule() {
		ArrayList<Class> s = SimulatedAnnealing.schedule;

		for (Class c : s)
			for (int i = 0; i < c.getTimetable().length; i++)
				for (int j = 0; j < c.getTimetable()[0].length; j++) {
					if (c.getTimetable()[i][j] != null)
						if ((c.getTimetable()[i][j].getTeacher()).equals(this))
							schedule[i][j] = c.getTimetable()[i][j];
				}
	}

	@Override
	public String toString() {
		return "Teacher{" + "ID='" + ID + '\'' + ", name='" + name + '\'' + ", lessonID=" + Arrays.toString(lessonID)
				+ ", hours=" + Arrays.toString(hours) + '}';
	}

	public boolean equals2(Object obj) {
		return this.ID.equals(((Teacher) obj).getID());
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getLessonID() {
		return lessonID;
	}

	public void setLessonID(String[] lessonID) {

		this.lessonID = new String[lessonID.length];
		for (int i = 0; i < lessonID.length; i++)
			this.lessonID[i] = lessonID[i];
	}

	public int[] getHours() {
		return hours;
	}

	public void setHours(int[] hours) {

		this.hours = new int[hours.length];
		for (int i = 0; i < hours.length; i++)
			this.hours[i] = hours[i];
	}

	public void setHours(String[] hours) {

		this.hours = new int[hours.length];
		for (int i = 0; i < hours.length; i++)
			this.hours[i] = Integer.parseInt(hours[i]);
	}
}
