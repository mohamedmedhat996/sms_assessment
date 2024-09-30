package bb.ta.sms.controller;

import bb.ta.sms.model.Course;
import bb.ta.sms.service.CourseService;
import bb.ta.sms.util.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;


    @GetMapping("courses")
    public ResponseEntity<?> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @PostMapping("courses")
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        Course createdCourse = courseService.createCourse(course);
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }

    @PostMapping("courses/{courseId}/register")
    public ResponseEntity<?> registerToCourse(@PathVariable Long courseId) {
        String userName = SecurityContextUtil.getCurrentUsername();
        courseService.registerCourse(courseId, userName);
        return ResponseEntity.ok("Successfully registered to the course.");
    }

    @PostMapping("courses/{courseId}/cancel")
    public ResponseEntity<String> cancelRegistration(@PathVariable Long courseId) {
        String userName = SecurityContextUtil.getCurrentUsername();
        courseService.cancelRegistration(courseId, userName);
        return ResponseEntity.ok("Course registration cancelled.");
    }

    @GetMapping("courses/{courseId}/schedule")
    public ResponseEntity<byte[]> getCourseSchedulePdf(@PathVariable Long courseId) throws IOException {
        byte[] pdfContent = courseService.generateCourseSchedulePdf(courseId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("inline").filename("course-schedule.pdf").build());
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }

}
