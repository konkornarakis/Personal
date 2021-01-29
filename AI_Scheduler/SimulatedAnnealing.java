
import java.util.ArrayList;
import java.util.Random;

public class SimulatedAnnealing {

	// Each slot belongs to a class which contains an 2d array with the week
	// schedule
	// of this class
	public static ArrayList<Class> schedule = new ArrayList<Class>();

	// A list with all the teachers
	static ArrayList<Teacher> teachers = new ArrayList<Teacher>();

	// A list with all the lessons
	static ArrayList<Lesson> lessons = new ArrayList<Lesson>();

	// maximum temperature
	static final double Tmax = 100;

	// minimum temperature
	static final double Tmin = 1;

	// current temperature
	static double T = Tmax;

	// rate of decrease of temperature
	static final double alpha = 0.96;

	// generate initial state
	public static ArrayList<Class> genInitSol() {

		int hour, day;
		Lecture lecture;

		for (Class cl : schedule) {

			hour = 0;
			day = 0;

			for (Lesson l : lessons) {

				if (l.getClasses().charAt(0) == (cl.getName().charAt(0))) {
					Teacher teacher = null;

					for (Teacher t : teachers) {
						for (int i = 0; i < t.getLessonID().length; i++) {
							if (l.getID().equals(t.getLessonID()[i])) {
								teacher = t;
								break;
							}
						}
					}

					for (int i = 0; i < l.getHours(); i++) {

						lecture = new Lecture(teacher, l);
						cl.setTimetable(hour, day, lecture);

						hour++;
						if (hour == 7) {

							day++;
							hour = 0;
						}
					}
				}
			}
		}

		for (Teacher t : teachers)
			t.setSchedule();

		//System.out.print(schedule.get(0));

		return schedule;
	}

	public static ArrayList<Class> neighbor(ArrayList<Class> currentSol) {

		Random r = new Random();
		int a, b, c, d;
		for (Class cl : currentSol) {
			a = r.nextInt(7);
			b = r.nextInt(5);
			c = r.nextInt(7);
			d = r.nextInt(5);
			while (a == c && d == b) {
				a = r.nextInt(7);
				b = r.nextInt(5);
				c = r.nextInt(7);
				d = r.nextInt(5);
			}

			cl.swap(a, b, c, d);
		}

		return currentSol;

	}

	// heuristic function, check restrictions and calculate score of a solution
	public static int score(ArrayList<Class> s) {
		int i;
		int j;
		int a, b;
		int v = 0;
		int day1 = 0;
		int day2 = 0;
		int day3 = 0;
		int day4 = 0;
		int day5 = 0;
		Class class1, class2;

		// 6th restriction
		for (int k = 0; k < s.size(); k++)
			for (int l = k + 1; l < s.size(); l++) {

				class1 = s.get(k);
				class2 = s.get(l);

				for (i = 0; i < class1.getTimetable().length; i++)
					for (j = 0; j < class2.getTimetable()[0].length; j++)
						if (class1.getTimetable()[i][j] != null && class2.getTimetable()[i][j] != null)
							if ((class1.getTimetable()[i][j].getTeacher())
									.equals(class2.getTimetable()[i][j].getTeacher()))
								return Integer.MAX_VALUE;
			}

		// 1st restriction
		for (Class c : s)
			for (i = 0; i < 5; i++) {
				j = 0;
				while (j < 7) {
					while ((c.getTimetable()[i][j]) == null && j < 7)
						j++;
					while ((c.getTimetable()[i][j]) != null && j < 7)
						j++;
					a = j;
					while ((c.getTimetable()[i][j]) == null && j < 7)
						j++;
					b = j;
					if (i < 7) {
						v += b - a;
						j = b;
					}

					// c.getRestr1().add(Integer.valueOf(i), Integer.valueOf(b));
				}
			}

		for (Class c : s)
			for (i = 0; i < 5; i++) {

				// 3rd restriction
				for (Class c2 : s) {
					for (i = 0; i < c2.getTimetable().length; i++)
						for (j = 0; j < c2.getTimetable()[0].length; j++) {
							if (c2.getTimetable()[i][j] != null) {
								if (i == 0)
									day1++;
								if (i == 1)
									day2++;
								if (i == 2)
									day3++;
								if (i == 3)
									day4++;
								if (i == 4)
									day5++;

							}

						}
					if (Math.abs(day1 - day2) > 3)
						v++;// c.getRestr3().add(Integer.valueOf(1), Integer.valueOf(2));
					if (Math.abs(day1 - day3) > 3)
						v++;
					if (Math.abs(day1 - day4) > 3)
						v++;
					if (Math.abs(day1 - day5) > 3)
						v++;
					if (Math.abs(day2 - day3) > 3)
						v++;
					if (Math.abs(day2 - day4) > 3)
						v++;
					if (Math.abs(day2 - day5) > 3)
						v++;
					if (Math.abs(day3 - day4) > 3)
						v++;
					if (Math.abs(day4 - day5) > 3)
						v++;

				}

				// 4th restriction
				for (Class c3 : s)
					for (i = 0; i < c3.getTimetable().length; i++)
						for (j = 0; j < c3.getTimetable()[0].length; j++)
							for (int k = i; k < c3.getTimetable().length; k++)
								if ((c3.getTimetable()[i][j]).equals(c3.getTimetable()[k][j]))
									v++; // c.getRestr4().add(c.getTimetable()[i][j].getID());
			}
		return 0; // change this

	}

	public static void runSimulatedAnnealing() {

		// initial state
		ArrayList<Class> currentSol = genInitSol();

		while (T > Tmin) {

			ArrayList<Class> newSol = neighbor(currentSol);
			double ap = Math.pow(Math.E, (score(currentSol) - score(newSol)) / T);
			if (ap > Math.random())
				currentSol = newSol;

			T *= alpha; // decrease temperature
		}

		schedule = currentSol;

	}

	public SimulatedAnnealing() {

	}

	// this method must be called in main to return the solution-schedule in a form
	// of Strings
	public static ArrayList<String[]> getCompatibleSchedule() {
		ArrayList<String[]> compatibleSchedule = new ArrayList<String[]>();

		for (int i = 0; i < schedule.size(); i++) {
			String[] classWeekCourses = new String[6];
			classWeekCourses[5] = schedule.get(i).getName();
			for (int day = 0; day < 5; day++) {
				String daySchedule = "";
				for (int hour = 0; hour < 7; hour++) {
					if (((schedule.get(i)).getTimetable())[hour][day] != null) {
						String lessonName = ((((schedule.get(i)).getTimetable())[hour][day]).getLesson()).getName();
						daySchedule += lessonName;
					} else {
						daySchedule += " - ";
					}
					if (hour != 6)
						daySchedule += ", ";
				}
				classWeekCourses[day] = daySchedule;
			}
			compatibleSchedule.add(classWeekCourses);
		}

		return compatibleSchedule;
	}

	// must NOT be called in main
	public ArrayList<Class> getSchedule() {
		return schedule;
	}

}