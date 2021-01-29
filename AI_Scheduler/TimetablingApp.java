
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.ArrayList;

public class TimetablingApp {

	public static void readFiles() {

		File file;

		try {

			file = new File("lessons.txt");
			Scanner sc = new Scanner(file);

			Lesson lesson = new Lesson();
			String[] lessonInfo;
			String line;

			while (sc.hasNextLine()) {

				line = sc.nextLine();

				if (line.startsWith("#") || line.isEmpty())
					continue;

				lessonInfo = line.split(", ");

				lesson.setID(lessonInfo[0]);
				lesson.setName(lessonInfo[1]);
				lesson.setClasses(lessonInfo[2]);
				lesson.setHours(Integer.parseInt(lessonInfo[3]));

				SimulatedAnnealing.lessons.add(new Lesson(lesson));

			}

			sc.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			file = new File("teachers.txt");
			Scanner sc2 = new Scanner(file);
			String line;
			Teacher teacher = new Teacher();
			String[] teacherInfo;

			while (sc2.hasNextLine()) {

				line = sc2.nextLine();

				if (line.startsWith("#") || line.isEmpty())
					continue;

				teacherInfo = line.split(", ");

				teacher.setID(teacherInfo[0]);
				teacher.setName(teacherInfo[1]);
				teacher.setLessonID(teacherInfo[2].split(" "));
				teacher.setHours(teacherInfo[3].split(" "));

				SimulatedAnnealing.teachers.add(new Teacher(teacher));

			}

			sc2.close();

		} catch (Exception e) {
			e.printStackTrace();
		} 

	}

	// printing schedule as .txt
	public static void outputFile(ArrayList<String[]> schedule) {
		BufferedWriter writer = null;
		try {

			// Create a temporary file
			File scheduleTXT = new File("schedule.txt");

			// This will output the full path where the file will be written to...
			System.out.println(scheduleTXT.getCanonicalPath());

			writer = new BufferedWriter(new FileWriter(scheduleTXT));

			// writer.write();

			for (int i = 0; i < schedule.size(); i++) {

				// write name of class
				writer.write(schedule.get(i)[schedule.get(i).length - 1]);
				for (int j = 0; j < schedule.get(i).length - 1; j++) {

					// write day schedule
					writer.write(schedule.get(i)[j]);
					writer.write("\n\r");
				}
				writer.write("\n\r");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// Close the writer regardless of what happens...
				writer.close();
			} catch (Exception e) {
			}
		}

	}

	public static void main(String args[]) {

		int A, B, C;
		if (args.length != 3) {
			A = 3;
			B = 3;
			C = 3;
		} else {

			A = Integer.parseInt(args[0]);
			B = Integer.parseInt(args[1]);
			C = Integer.parseInt(args[2]);
		}

		Class c;
		for (int i = 0; i < A; i++) {
			c = new Class("A" + (i + 1));
			SimulatedAnnealing.schedule.add(c);
		}
		for (int i = 0; i < B; i++) {
			c = new Class("B" + (i + 1));
			SimulatedAnnealing.schedule.add(c);
		}
		for (int i = 0; i < C; i++) {
			c = new Class("C" + (i + 1));
			SimulatedAnnealing.schedule.add(c);
		}

		readFiles();

		SimulatedAnnealing.runSimulatedAnnealing();

		outputFile(SimulatedAnnealing.getCompatibleSchedule());

		System.out.println("Press any key to exit ");

		Scanner scan = new Scanner(System.in);
		String s = scan.next();
		scan.close();
		System.out.println("Quiting");
	}
}
