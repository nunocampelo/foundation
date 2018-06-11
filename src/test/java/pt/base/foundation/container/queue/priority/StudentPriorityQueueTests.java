package pt.base.foundation.container.queue.priority;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Test;

import pt.base.foundation.container.queue.priority.StudentPriorityQueue;
import pt.base.foundation.container.queue.priority.StudentPriorityQueue.Student;

public class StudentPriorityQueueTests {

	private StudentPriorityQueue queue = new StudentPriorityQueue();

	@Test
	public void studentCompareToShouldOrderByScore() {

		Student alpha = new StudentBuilder().withRandomIdentifiers().withScore(1d).build();
		Student anne = new StudentBuilder().withRandomIdentifiers().withScore(19d).build();
		Student any = new StudentBuilder().withRandomIdentifiers().withScore(19d).build();
		Student frank = new StudentBuilder().withRandomIdentifiers().withScore(10d).build();

		assertTrue(alpha.compareTo(anne) < 0);
		assertTrue(anne.compareTo(any) == 0);
		assertTrue(anne.compareTo(frank) > 0);
	}

	@Test
	public void pollOfStudentWithGreaterScoreShouldOccurFirst() {

		Student anne = new StudentBuilder().withRandomIdentifiers().withScore(19d).build();
		Student frank = new StudentBuilder().withRandomIdentifiers().withScore(10d).build();

		queue.offer(frank);
		queue.offer(anne);

		assertEquals(frank.getId(), queue.poll());
		assertEquals(anne.getId(), queue.poll());
	}

	@Test
	public void pollOfStudentsWithSameScoreShouldHappenByEntranceOrder() {

		Student anne = new StudentBuilder().withRandomIdentifiers().withScore(19d).build();
		Student any = new StudentBuilder().withRandomIdentifiers().withScore(19d).build();

		queue.offer(any);
		queue.offer(anne);

		assertEquals(any.getId(), queue.poll());
		assertEquals(anne.getId(), queue.poll());
	}

	private class StudentBuilder {

		private String id, name;
		private Double score;

		private StudentBuilder withScore(Double score) {
			this.score = score;
			return this;
		}

		private StudentBuilder withRandomIdentifiers() {
			this.id = UUID.randomUUID().toString();
			this.name = UUID.randomUUID().toString();
			return this;
		}

		private Student build() {
			return new StudentPriorityQueue.Student(id, name, score);
		}
	}
}
