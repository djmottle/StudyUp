package edu.studyup.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
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

class EventServiceImplTest{

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
		
		DataStorage.eventData.put(event.getEventID(), event);
	}

	@AfterEach
	void tearDown() throws Exception {
		DataStorage.eventData.clear();
	}

	@Test
	void testUpdateEvent_nameWith20Char_goodCase() throws StudyUpException {
		int eventID = 1;
		assertEquals("This has exactly 20." ,eventServiceImpl.updateEventName(eventID, "This has exactly 20.").getName());
	}

	@Test
	void testUpdateEventName_LessThan20_goodCase() throws StudyUpException {
		int eventID = 1;
		eventServiceImpl.updateEventName(eventID, "Event name");
		assertEquals("Event name", DataStorage.eventData.get(eventID).getName());
  }
	
  @Test
  void testDeleteEvent_goodCase() throws StudyUpException {
		int eventID = 1;
		eventServiceImpl.deleteEvent(1);
		assertEquals(null , DataStorage.eventData.get(eventID));
	}
    
	@Test
	void testUpdateEvent_WrongEventID_badCase() {
		int eventID = 3;
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.updateEventName(eventID, "Renamed Event 3");
  }
	
   void testUpdateEvent_nameTooLong_badCase() throws StudyUpException {
		int eventID = 1;
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.updateEventName(eventID, "Wow this is a really long name, it should probably be shorter than this.");
		  });
	}
	
	@Test
	void testUpdateEventName_LessThan20_goodCase() throws StudyUpException {
		int eventID = 1;
		eventServiceImpl.updateEventName(eventID, "Event name");
		assertEquals("Event name", DataStorage.eventData.get(eventID).getName());
	}
	
	@Test
	void testUpdateEvent_WrongEventID_badCase() {
		int eventID = 3;
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.updateEventName(eventID, "Renamed Event 3");
		  });
	}
	
	@Test
	//Sets the date of the only event to a past date and checks if active events is empty.
	void testGetActiveEvents_NoActiveEvents() {
		int eventID = 1;
		@SuppressWarnings("deprecation")
		Date pastDate = new Date(100,0, 1);
		DataStorage.eventData.get(eventID).setDate(pastDate);
		List<Event> activeEvents = eventServiceImpl.getActiveEvents();
		assertTrue(activeEvents.isEmpty());
	}
	
	@Test
	//Sets the date of the only event to a future date and checks if active events returns the event.
	void testGetActiveEvents_ActiveEvent() {
		int eventID = 1;
		@SuppressWarnings("deprecation")
		Date futureDate = new Date(3000, 0, 1);
		DataStorage.eventData.get(eventID).setDate(futureDate);
		List<Event> expectedEvents = new ArrayList<>();
		expectedEvents.add(DataStorage.eventData.get(eventID));
		List<Event> activeEvents = eventServiceImpl.getActiveEvents();
		assertEquals(expectedEvents, activeEvents);
	}
	
	@Test
	//Sets the date of the only event to a past date and checks if past events returns the event.
	void testGetPastEvents_PastEvent() {
		int eventID = 1;
		@SuppressWarnings("deprecation")
		Date pastDate = new Date(100, 0, 1);
		DataStorage.eventData.get(eventID).setDate(pastDate);
		List<Event> expectedEvents = new ArrayList<>();
		expectedEvents.add(DataStorage.eventData.get(eventID));
		List<Event> pastEvents = eventServiceImpl.getPastEvents();
		assertEquals(expectedEvents, pastEvents);
	}	
	
	@Test
	//Sets the date of the only event to a future date and checks if past events returns the event.
	void testGetPastEvents_FutureEvent() {
		int eventID = 1;
		@SuppressWarnings("deprecation")
		Date futureDate = new Date(2000, 0, 1);
		DataStorage.eventData.get(eventID).setDate(futureDate);
		List<Event> expectedEvents = new ArrayList<>();
		expectedEvents.add(DataStorage.eventData.get(eventID));
		List<Event> futureEvents = eventServiceImpl.getPastEvents();
		assertTrue(futureEvents.isEmpty());
	}
	
      @Test
	void testAddStudentToEvent_nullEvent_badCase() {
		int eventID = 3; //Event does not exist in DataStorage
		Student studentToAdd = new Student();
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.addStudentToEvent(studentToAdd, eventID);
		  });
	}
	
	@Test
	void testAddStudentToEvent_startsWithNoStudents_goodCase() throws StudyUpException {
		int eventID = 1;
		Student studentToAdd = new Student();
		List<Student> expectedStudentList = new ArrayList<>();
		expectedStudentList.add(studentToAdd);
		DataStorage.eventData.get(eventID).setStudents(null);
		
		assertEquals(expectedStudentList, eventServiceImpl.addStudentToEvent(studentToAdd, eventID).getStudents());
	}
	
	@Test
	void testAddStudentToEvent_startsWithStudent_goodCase() throws StudyUpException {
		int eventID = 1;
		Student studentToAdd = new Student();
		List<Student> expectedStudentList = DataStorage.eventData.get(eventID).getStudents();
		expectedStudentList.add(studentToAdd);
		assertEquals(expectedStudentList, eventServiceImpl.addStudentToEvent(studentToAdd, eventID).getStudents());
	}
	
	/*
	 * An Event should have no more than 2 students, so adding more should
	 * throw an exception
	 */
	@Test
	void testAddStudentToEvent_addThreeStudents_badCase() throws StudyUpException {
		int eventID = 1;
		int maxStudents = 2;
		Student studentToAdd = new Student();
		Assertions.assertThrows(StudyUpException.class, () -> {
			for(int i = 0; i != maxStudents + 1; ++i) {
			eventServiceImpl.addStudentToEvent(studentToAdd, eventID);
			}
		  });
	}
	

	@Test
	//Sets the date of the only event to a future date and checks if past events returns the event.
	void testDeleteEvent() {
		int eventID = 1;
		@SuppressWarnings("deprecation")
		Date pastDate = new Date(100, 0, 1);
		DataStorage.eventData.get(eventID).setDate(pastDate);
		List<Event> expectedEvents = new ArrayList<>();
		expectedEvents.add(DataStorage.eventData.get(eventID));
		Event deletedEvent = eventServiceImpl.deleteEvent(eventID);
		assertEquals(expectedEvents, deletedEvent);
  }
}