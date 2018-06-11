package pt.base.incubator.container.queue.priority;

import java.util.PriorityQueue;
import java.util.Queue;

import org.springframework.util.StringUtils;

public class StudentPriorityQueue {

	private Queue<Student> queue;

	public StudentPriorityQueue() {
		queue = new PriorityQueue<Student>();
	}

	public StudentPriorityQueue(int initialCapacity) {
		queue = new PriorityQueue<Student>(initialCapacity);
	}

	public boolean offer(Student student) {
		return queue.offer(student);
	}

	public boolean offer(String id, String name, double score) {
		return queue.offer(new Student(id, name, score));
	}

	public String poll() {
		return queue.poll().getId();
	}

	public static class Student implements Comparable<Student> {

		private String id, name;
		private Double score;

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public Double getScore() {
			return score;
		}

		public Student(String id, String name, double score) {

			if (StringUtils.isEmpty(id) || StringUtils.isEmpty(name)) {
				throw new IllegalStateException("Student identifiers (id, name) cannot be empty");
			}

			this.id = id;
			this.name = name;
			this.score = score;
		}

		@Override
		public int compareTo(Student other) {
			return this.score.compareTo(other.score);
		}
	}
}
