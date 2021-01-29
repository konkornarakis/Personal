
public class Lecture {

	private Teacher teacher;

	private Lesson lesson;

	public Lecture(Teacher teacher, Lesson lesson) {
		this.teacher = teacher;
		this.lesson = lesson;
	}

	public Lecture(Lecture lecture) {
		this.teacher = new Teacher(lecture.getTeacher());
		this.lesson = new Lesson(lecture.getLesson());
	}

	// getters

	public Teacher getTeacher() {
		return this.teacher;
	}

	public Lesson getLesson() {
		return this.lesson;
	}

	// setters

	public void setTeacher(Teacher newTeacher) {
		this.teacher = newTeacher;
	}

	public void setLesson(Lesson newLesson) {
		this.lesson = newLesson;
	}
}
