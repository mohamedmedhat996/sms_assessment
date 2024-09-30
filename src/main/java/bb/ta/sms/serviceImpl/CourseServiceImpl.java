package bb.ta.sms.serviceImpl;

import bb.ta.sms.exception.BaseException;
import bb.ta.sms.model.Course;
import bb.ta.sms.model.CourseRegistration;
import bb.ta.sms.model.User;
import bb.ta.sms.repository.CourseRepository;
import bb.ta.sms.repository.RegistrationCourseRepository;
import bb.ta.sms.repository.UserRepository;
import bb.ta.sms.service.CourseService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final RegistrationCourseRepository registrationCourseRepository;
    private final UserRepository userRepository;

    @Override
    @Cacheable(cacheNames = "courses", key = "'getAllCourses'")
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public void registerCourse(Long courseId, String userName) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BaseException("Course not found"));

        User currentUser = userRepository.findByUsername(userName)
                .orElseThrow(() -> new BaseException("User not found"));

        Optional<CourseRegistration> exRegistration = registrationCourseRepository.findByUserAndCourse(currentUser,course);

        if (exRegistration.isPresent()){
            CourseRegistration existingRegistration = exRegistration.get();
            if(existingRegistration.getCanceledAt() == null) {
                throw new BaseException("This user registered before on this Course");
            } else {
                existingRegistration.setCanceledAt(null);
                registrationCourseRepository.save(existingRegistration);
            }
        } else {
            CourseRegistration registration = CourseRegistration.builder()
                    .course(course)
                    .user(currentUser)
                    .registeredAt(new Timestamp(System.currentTimeMillis()))
                    .build();
            registrationCourseRepository.save(registration);
        }
    }

    @Override
    public void cancelRegistration(Long courseId, String userName) {
        CourseRegistration registration = registrationCourseRepository.findByUserAndCourseAndCanceledAtIsNull(
                userRepository.findByUsername(userName).orElseThrow(() -> new BaseException("User not found")),
                courseRepository.findById(courseId).orElseThrow(() -> new BaseException("Course not found"))
        ).orElseThrow(() -> new BaseException("No registration found for this course"));

        registration.setCanceledAt(new Timestamp(System.currentTimeMillis()));
        registrationCourseRepository.save(registration);
    }

    @Override
    @Cacheable(cacheNames = "courses", key = "'generateCourseSchedulePdf_' + #courseId")
    public byte[] generateCourseSchedulePdf(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BaseException("Course not found with ID: " + courseId));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            addTitle(document);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            addCourseDetailsTable(document, course);
            document.add(Chunk.NEWLINE);
            addFooter(document);

        } catch (DocumentException e) {
            throw new BaseException("Error generating course schedule PDF: " + e);
        } finally {
            document.close();
        }

        return outputStream.toByteArray();
    }

    private void addTitle(Document document) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Course Schedule", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
    }

    private void addCourseDetailsTable(Document document, Course course) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 5f});

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

        addCourseDetailRow(table, "Course Name:", course.getCourseName(), headerFont);
        addCourseDetailRow(table, "Instructor:", course.getInstructor(), headerFont);
        addCourseDetailRow(table, "Credits:", String.valueOf(course.getCredits()), headerFont);
        addCourseDetailRow(table, "Description:", course.getDescription(), headerFont);

        document.add(table);
    }

    private void addCourseDetailRow(PdfPTable table, String header, String detail, Font headerFont) {
        table.addCell(createCell(header, headerFont));
        table.addCell(createCell(detail));
    }

    private void addFooter(Document document) throws DocumentException {
        Paragraph footer = new Paragraph("Generated on: " + new Date(), FontFactory.getFont(FontFactory.HELVETICA, 10));
        footer.setAlignment(Element.ALIGN_RIGHT);
        document.add(footer);
    }

    private PdfPCell createCell(String content) {
        return createCell(content, FontFactory.getFont(FontFactory.HELVETICA, 12));
    }

    private PdfPCell createCell(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setPadding(8f);
        cell.setBorderWidth(0.5f);
        return cell;
    }
}
