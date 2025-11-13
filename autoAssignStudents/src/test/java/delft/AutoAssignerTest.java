package delft;

import static org.assertj.core.api.Assertions.*;

import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;

class AutoAssignerTest {

    private ZonedDateTime date(int year, int month, int day, int hour, int minute) {
        return ZonedDateTime.of(year, month, day, hour, minute, 0, 0, ZoneId.systemDefault());
    }

    @Test
    void assignsToEarliestDate_singleStudent_singleWorkshop() {
        ZonedDateTime d1 = date(2025, 1, 10, 9, 0);
        ZonedDateTime d2 = date(2025, 1, 11, 9, 0);
        Map<ZonedDateTime, Integer> spots = Map.of(
                d2, 5,
                d1, 3
        );
        Workshop ws = new Workshop(1, "Java", spots);
        Student s = new Student(1, "Alice", "a@ex.com");

        AssignmentsLogger log = new AutoAssigner().assign(List.of(s), List.of(ws));

        assertThat(log.getErrors()).isEmpty();
        assertThat(log.getAssignments())
                .containsExactly("Java,Alice,10/01/2025 09:00");
    }

    @Test
    void picksNextDateWhenEarliestIsFull() {
        ZonedDateTime d1 = date(2025, 2, 1, 14, 0);
        ZonedDateTime d2 = date(2025, 2, 2, 14, 0);
        Map<ZonedDateTime, Integer> spots = Map.of(
                d1, 0,
                d2, 1
        );
        Workshop ws = new Workshop(1, "Python", spots);
        Student s = new Student(2, "Bob", "b@ex.com");

        AssignmentsLogger log = new AutoAssigner().assign(List.of(s), List.of(ws));

        assertThat(log.getErrors()).isEmpty();
        assertThat(log.getAssignments())
                .containsExactly("Python,Bob,02/02/2025 14:00");
    }

    @Test
    void consumesSpotsAcrossStudents_thenMovesToNextDate() {
        ZonedDateTime d1 = date(2025, 3, 5, 10, 0);
        ZonedDateTime d2 = date(2025, 3, 6, 10, 0);
        Map<ZonedDateTime, Integer> spots = Map.of(
                d1, 1,
                d2, 1
        );
        Workshop ws = new Workshop(10, "Kotlin", spots);
        List<Student> students = List.of(
                new Student(1, "Carl", "c@ex.com"),
                new Student(2, "Dana", "d@ex.com")
        );

        AssignmentsLogger log = new AutoAssigner().assign(students, List.of(ws));

        assertThat(log.getErrors()).isEmpty();
        assertThat(log.getAssignments())
                .containsExactlyInAnyOrder(
                        "Kotlin,Carl,05/03/2025 10:00",
                        "Kotlin,Dana,06/03/2025 10:00"
                );
    }

    @Test
    void noAvailableDate_logsError() {
        ZonedDateTime d1 = date(2025, 4, 1, 8, 0);
        Map<ZonedDateTime, Integer> spots = Map.of(
                d1, 0
        );
        Workshop ws = new Workshop(2, "Scala", spots);
        Student s = new Student(3, "Eve", "e@ex.com");

        AssignmentsLogger log = new AutoAssigner().assign(List.of(s), List.of(ws));

        assertThat(log.getAssignments()).isEmpty();
        assertThat(log.getErrors()).containsExactly("Scala,Eve");
    }

    @Test
    void multipleWorkshops_mixedAvailability() {
        ZonedDateTime a1 = date(2025, 5, 10, 9, 0);
        ZonedDateTime a2 = date(2025, 5, 11, 9, 0);
        Map<ZonedDateTime, Integer> ws1Spots = Map.of(
                a1, 0,
                a2, 2
        );
        Workshop ws1 = new Workshop(100, "WS1", ws1Spots);

        ZonedDateTime b1 = date(2025, 6, 10, 9, 0);
        Map<ZonedDateTime, Integer> ws2Spots = Map.of(
                b1, 0
        );
        Workshop ws2 = new Workshop(200, "WS2", ws2Spots);

        List<Student> students = List.of(
                new Student(10, "Frank", "f@ex.com"),
                new Student(11, "Grace", "g@ex.com")
        );

        AssignmentsLogger log = new AutoAssigner().assign(students, List.of(ws1, ws2));

        assertThat(log.getAssignments())
                .containsExactlyInAnyOrder(
                        "WS1,Frank,11/05/2025 09:00",
                        "WS1,Grace,11/05/2025 09:00"
                );
        assertThat(log.getErrors())
                .containsExactlyInAnyOrder(
                        "WS2,Frank",
                        "WS2,Grace"
                );
    }

    @Test
    void unsortedInsertion_stillChoosesChronologicallyEarliest() {
        ZonedDateTime d3 = date(2025, 7, 20, 9, 0);
        ZonedDateTime d1 = date(2025, 7, 18, 9, 0);
        ZonedDateTime d2 = date(2025, 7, 19, 9, 0);
        Map<ZonedDateTime, Integer> spots = new HashMap<>();
        spots.put(d3, 1);
        spots.put(d1, 1);
        spots.put(d2, 1);

        Workshop ws = new Workshop(3, "C++", spots);
        Student s = new Student(5, "Hank", "h@ex.com");

        AssignmentsLogger log = new AutoAssigner().assign(List.of(s), List.of(ws));

        assertThat(log.getAssignments())
                .containsExactly("C++,Hank,18/07/2025 09:00");
        assertThat(log.getErrors()).isEmpty();
    }

    @Test
    void moreStudentsThanTotalCapacity_partialAssignments_thenErrors() {
        ZonedDateTime d1 = date(2025, 8, 1, 13, 0);
        ZonedDateTime d2 = date(2025, 8, 2, 13, 0);
        Map<ZonedDateTime, Integer> spots = Map.of(
                d1, 1,
                d2, 1
        );
        Workshop ws = new Workshop(4, "Go", spots);
        List<Student> students = List.of(
                new Student(1, "Ivy", "i@ex.com"),
                new Student(2, "Jake", "j@ex.com"),
                new Student(3, "Kim", "k@ex.com")
        );

        AssignmentsLogger log = new AutoAssigner().assign(students, List.of(ws));

        assertThat(log.getAssignments())
                .containsExactlyInAnyOrder(
                        "Go,Ivy,01/08/2025 13:00",
                        "Go,Jake,02/08/2025 13:00"
                );
        assertThat(log.getErrors())
                .containsExactly("Go,Kim");
    }

    @Test
    void noStudents_producesNoLogs() {
        ZonedDateTime d1 = date(2025, 10, 1, 9, 0);
        Map<ZonedDateTime, Integer> spots = Map.of(d1, 2);
        Workshop ws = new Workshop(999, "EmptyClass", spots);

        AssignmentsLogger log = new AutoAssigner().assign(List.of(), List.of(ws));

        assertThat(log.getAssignments()).isEmpty();
        assertThat(log.getErrors()).isEmpty();
    }

    @Test
    void noWorkshops_producesNoLogs() {
        List<Student> students = List.of(
                new Student(1, "A", "a@ex.com"),
                new Student(2, "B", "b@ex.com")
        );

        AssignmentsLogger log = new AutoAssigner().assign(students, List.of());

        assertThat(log.getAssignments()).isEmpty();
        assertThat(log.getErrors()).isEmpty();
    }
    @Test
    void oneDateCapacityOne_manyStudents_thenErrorsForTheRest() {
        // Un seul créneau avec 1 place, 3 étudiants => 1 assignation puis 2 erreurs
        ZonedDateTime d = date(2026, 1, 5, 9, 0);
        Map<ZonedDateTime, Integer> spots = Map.of(d, 1);
        Workshop ws = new Workshop(7, "Rust", spots);
        List<Student> students = List.of(
                new Student(1, "S1", "s1@ex.com"),
                new Student(2, "S2", "s2@ex.com"),
                new Student(3, "S3", "s3@ex.com")
        );

        AssignmentsLogger log = new AutoAssigner().assign(students, List.of(ws));

        assertThat(log.getAssignments())
                .containsExactly("Rust,S1,05/01/2026 09:00");
        assertThat(log.getErrors())
                .containsExactlyInAnyOrder("Rust,S2", "Rust,S3");
    }

    @Test
    void multipleWorkshops_branchingWithinSameCall() {

        ZonedDateTime tA1 = date(2026, 2, 10, 10, 0);
        Map<ZonedDateTime, Integer> spotsA = Map.of(tA1, 1);
        Workshop wsA = new Workshop(50, "A", spotsA);

        ZonedDateTime tB1 = date(2026, 2, 11, 10, 0);
        ZonedDateTime tB2 = date(2026, 2, 12, 10, 0);
        Map<ZonedDateTime, Integer> spotsB = Map.of(
                tB1, 0,
                tB2, 1
        );
        Workshop wsB = new Workshop(60, "B", spotsB);

        List<Student> students = List.of(
                new Student(1, "X", "x@ex.com"),
                new Student(2, "Y", "y@ex.com"),
                new Student(3, "Z", "z@ex.com")
        );

        AssignmentsLogger log = new AutoAssigner().assign(students, List.of(wsA, wsB));

        assertThat(log.getAssignments())
                .containsExactlyInAnyOrder(
                        "A,X,10/02/2026 10:00",
                        "B,X,12/02/2026 10:00"
                );

        assertThat(log.getErrors())
                .containsExactlyInAnyOrder("A,Y", "A,Z", "B,Y", "B,Z");
    }

}