package edu.studyup.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import edu.studyup.entity.Event;
import edu.studyup.entity.Location;
import edu.studyup.entity.Student;
import edu.studyup.util.DataStorage;
import edu.studyup.util.StudyUpException;

class EventServiceImplTest {

	private static final Object NULL = null;
	EventServiceImpl eventServiceImpl;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		eventServiceImpl = new EventServiceImpl();
		//Create Student
		Student student = new Student();
		student.setFirstName("John");
		student.setLastName("Doe");
		student.setEmail("JohnDoe@email.com");
		student.setId(1);
		
		//Create Event1
		Event event = new Event();
		event.setEventID(1);
		event.setDate(new Date());
		event.setName("Event 1");
		Location location = new Location(-122, 37);
		event.setLocation(location);
		List<Student> eventStudents = new ArrayList<>();
		eventStudents.add(student);
		event.setStudents(eventStudents);
		
		//create Event2
		Event event2 = new Event();
		event2.setEventID(2);
		event2.setDate(new Date());
		event2.setName("Event 2");
		Location location2 = new Location(122, 37);
		event2.setLocation(location2);
		List<Student> eventStudents2 = new ArrayList<>();
		event2.setStudents(eventStudents2);
		
		
		DataStorage.eventData.put(event.getEventID(), event);
		DataStorage.eventData.put(event2.getEventID(), event2);
	}

	@AfterEach
	void tearDown() throws Exception {
		DataStorage.eventData.clear();
	}
	
	@Test
	void testUpdateEventName_GoodCase() throws StudyUpException {
		int eventID = 1;
		eventServiceImpl.updateEventName(eventID, "Renamed Event 1");
		assertEquals("Renamed Event 1", DataStorage.eventData.get(eventID).getName());
	}
	
	@Test
	void testUpdateEventName2_GoodCase() throws StudyUpException {
		int eventID = 2;
		eventServiceImpl.updateEventName(eventID, "Renamed Event 2");
		assertEquals("Renamed Event 2", DataStorage.eventData.get(eventID).getName());
	}
	
	
	@Test
	void testUpdateEventname_WrongEventID_BadCase() {
		int eventID = 4;
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.updateEventName(eventID, "Renamed Event 4");
		  });
	}
	
	@Test
	void testUpdateEventName_WrongEventName_BadCase() {
		int eventID = 1;
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.updateEventName(eventID, "This name includes at least 20 characters");
		  });	
	}
	
	@Test
	void testgetActiveEvents_noActiveEvent_GoodCase() throws StudyUpException{
		List<Event> current = new ArrayList<>();
		
		current = eventServiceImpl.getActiveEvents();
		assertEquals(current.size(), 0);
	}
	
	@Test
	void testgetActiveEvents_exsitActiveEvent_GoodCase() throws StudyUpException{
		//set a date in the future
		Date mydate = new Date(System.currentTimeMillis()+9999);
		//change event2's date
		DataStorage.eventData.get(2).setDate(mydate);
		
		List<Event> current = new ArrayList<>();
		current = eventServiceImpl.getActiveEvents();
		assertEquals(current.size(), 1);
		assertEquals(current.get(0).getEventID(), 2);
	}

	
	@Test
	void testgetPastEvents_GoodCase() throws StudyUpException{
		List<Event> current = new ArrayList<>();
		current = eventServiceImpl.getPastEvents();
		assertEquals(current.size(), 2);
		assertEquals(current.get(0).getName(), "Event 1");
		assertEquals(current.get(1).getName(), "Event 2");
	}
	
	@Test
	void testdeleteEvent_GoodCase() throws StudyUpException{
		eventServiceImpl.deleteEvent(1);
		assertEquals(DataStorage.eventData.size(), 1);
		assertEquals(DataStorage.eventData.get(1), null);
	}
	
	@Test
	void testaddStudentToEvent_GoodCase() throws StudyUpException{
		//create a new student
		Student student2 = new Student();
		student2.setFirstName("Calvin");
		student2.setLastName("Huang");
		student2.setEmail("CalvinHuang@email.com");
		student2.setId(2);
		
		//add a student to event 2 which doesn't has any event student
		eventServiceImpl.addStudentToEvent(student2, 2);
		assertEquals(DataStorage.eventData.get(2).getStudents().size(), 1);
		assertEquals(DataStorage.eventData.get(2).getStudents().get(0).getId(), 2);
	}
	
	@Test
	void testaddStudentToEvent_WrongEventName_BadCase() {
		int eventID = 3;
		Student student2 = new Student();
		student2.setFirstName("Calvin");
		student2.setLastName("Huang");
		student2.setEmail("CalvinHuang@email.com");
		student2.setId(2);
		
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.addStudentToEvent(student2, eventID);
		  });	
	}
	
	@Test
	void testaddStudentToEvent_WrongStudentNumber_BadCase() {
		int eventID = 1;
		//create student 2 and student 3
		Student student2 = new Student();
		student2.setFirstName("Calvin");
		student2.setLastName("Huang");
		student2.setEmail("CalvinHuang@email.com");
		student2.setId(2);
		
		Student student3 = new Student();
		student3.setFirstName("Benny");
		student3.setLastName("Zhou");
		student3.setEmail("BennyZhou@email.com");
		student3.setId(3);
		
		//event 1 already included a student. it should be wrong since there are totally 3 students in the event
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.addStudentToEvent(student2, eventID);
			eventServiceImpl.addStudentToEvent(student3, eventID);
		  });
	}
	
	
}
