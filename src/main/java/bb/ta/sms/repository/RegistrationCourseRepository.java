package bb.ta.sms.repository;

import bb.ta.sms.model.Course;
import bb.ta.sms.model.CourseRegistration;
import bb.ta.sms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationCourseRepository extends JpaRepository<CourseRegistration, Long> {
    List<CourseRegistration> findByUserId(Long userId);
    Optional<CourseRegistration> findByUserAndCourse(User user, Course course);
    Optional<CourseRegistration> findByUserAndCourseAndCanceledAtIsNull(User user, Course course);
}
