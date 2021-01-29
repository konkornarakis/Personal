
import java.util.Objects;

public class Lesson {

	private String ID;
	private String name;
	private String classes;
	private int hours;

	public Lesson(Lesson lesson) {

		this.ID = lesson.getID();
		this.name = lesson.getName();
		this.classes = lesson.getClasses();
		this.hours = lesson.getHours();
	}

	public Lesson() {

	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Lesson lesson = (Lesson) o;
		return Objects.equals(ID, lesson.ID);
	}

	@Override
	public String toString() {
		return "Lesson{" + "ID='" + ID + '\'' + ", name='" + name + '\'' + ", classes='" + classes + '\'' + ", hours="
				+ hours + '}';
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setClasses(String classes) {
		this.classes = classes;
	}

	public void setHours(int hours) {
		this.hours = hours;
	}

	public String getID() {
		return ID;
	}

	public String getName() {
		return name;
	}

	public String getClasses() {
		return classes;
	}

	public int getHours() {
		return hours;
	}
}
