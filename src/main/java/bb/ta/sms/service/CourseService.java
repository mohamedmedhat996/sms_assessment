package bb.ta.sms.service;


import bb.ta.sms.dto.CourseDto;
import bb.ta.sms.model.Course;

import java.util.List;

public interface CourseService {
    List<CourseDto> getAllCourses(String userName);
    Course createCourse(Course course);
    void registerCourse(Long courseId, String userName);
    void cancelRegistration(Long courseId, String userName);
    byte[] generateCourseSchedulePdf(Long courseId);
}
