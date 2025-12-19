package com.diplom.teacher_assistant.service;

import com.diplom.teacher_assistant.dto.StudentDTO;
import com.diplom.teacher_assistant.entity.Student;
import com.diplom.teacher_assistant.entity.StudentSubject;
import com.diplom.teacher_assistant.repository.StudentRepository;
import com.diplom.teacher_assistant.repository.StudentSubjectRepository;
import com.diplom.teacher_assistant.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final SecurityService securityService;
    private final StudentSubjectRepository studentSubjectRepository;

    public List<Student> getStudentByCurrentTutor(){
        Long tutorId = securityService.getCurrentTutorId();
        return studentRepository.findByTutor_TutorIdOrderByFullName(tutorId);
    }

    public List<StudentSubject> getStudentSubjects(Long studentId){
        return studentSubjectRepository.findByStudent_StudentId(studentId);
    }

    @Transactional
    public void createNewStudent(StudentDTO studentDTO){

        if (studentRepository.existsByEmailAndTutor_TutorId(studentDTO.getEmail(), securityService.getCurrentTutorId())){
            throw new IllegalArgumentException("Студент с таким email уже существует");
        }

        Student student = new Student();
        student.setFullName(studentDTO.getFullName());
        student.setEmail(studentDTO.getEmail());
        student.setTelegram(studentDTO.getTelegram());
        student.setLevel(studentDTO.getLevel());
        student.setAge(studentDTO.getAge());
        student.setNotes(studentDTO.getNotes());
        student.setTutor(securityService.getCurrentTutor());

        studentRepository.save(student);
    }

    public Student getStudentById(Long id){
        Long tutorId = securityService.getCurrentTutorId();

        return studentRepository.findById(id)
                .filter(student -> student.getTutor().getTutorId().equals(tutorId))
                .orElseThrow(() -> new IllegalArgumentException("Студент не найден"));
    }

    @Transactional
    public void updateStudent(Long id, StudentDTO studentDTO) {
        Student student = getStudentById(id);

        if (!student.getEmail().equals(studentDTO.getEmail()) &&
                studentRepository.existsByEmailAndTutor_TutorId(studentDTO.getEmail(), student.getTutor().getTutorId())) {
            throw new IllegalArgumentException("Студент с таким email уже существует");
        }

        student.setFullName(studentDTO.getFullName());
        student.setEmail(studentDTO.getEmail());
        student.setTelegram(studentDTO.getTelegram());
        student.setLevel(studentDTO.getLevel());
        student.setAge(studentDTO.getAge());
        student.setNotes(studentDTO.getNotes());

        studentRepository.save(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = getStudentById(id);
        studentRepository.delete(student);
    }

}
