package com.diplom.teacher_assistant.service;

import com.diplom.teacher_assistant.dto.SubjectDTO;
import com.diplom.teacher_assistant.entity.Subject;
import com.diplom.teacher_assistant.entity.Tutor;
import com.diplom.teacher_assistant.repository.SubjectRepository;
import com.diplom.teacher_assistant.repository.TopicRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SecurityService securityService;
    private final TopicRepository topicRepository;
    private final SubjectRepository subjectRepository;

    public List<Subject> getSubjectByCurrenTutor(){
        Long tutorId = securityService.getCurrentTutorId();
        return subjectRepository.findByTutor_TutorIdOrderByName(tutorId);
    }

    @Transactional
    public void createNewSubject(SubjectDTO subjectDTO){
        Long tutorId = securityService.getCurrentTutorId();
        Tutor tutor = securityService.getCurrentTutor();
        String nameFromDTO = subjectDTO.getName();

        if (subjectRepository.existsByNameAndTutor_TutorId(nameFromDTO, tutorId)){
            throw new IllegalArgumentException("Данный предмет уже добавлен");
        }

        Subject subject = new Subject();
        subject.setName(nameFromDTO);
        subject.setTutor(tutor);

        subjectRepository.save(subject);
    }

    public Subject getSubjectByCurrenTutor(Long id){
        Long tutorId = securityService.getCurrentTutorId();

        return subjectRepository.findById(id)
                .filter(subject -> subject.getTutor().getTutorId().equals(tutorId))
                .orElseThrow(() -> new IllegalArgumentException("Предмет не найден"));
    }

    @Transactional
    public void updateSubject(Long id, SubjectDTO subjectDTO){
        Subject subject = getSubjectByCurrenTutor(id);

        subject.setName(subjectDTO.getName());
        subjectRepository.save(subject);
    }

    @Transactional
    public void deleteSubject(Long id) {
        Subject subject = getSubjectByCurrenTutor(id);
        subjectRepository.delete(subject);
    }

    public List<SubjectWithTopicCount> getSubjectsWithTopicCount() {
        Long tutorId = securityService.getCurrentTutorId();
        List<Subject> subjects = subjectRepository.findByTutor_TutorIdOrderByName(tutorId);

        return subjects.stream()
                .map(subject -> {
                    long topicCount = topicRepository.countBySubject_SubjectId(subject.getSubjectId());
                    return new SubjectWithTopicCount(subject, topicCount);
                })
                .collect(Collectors.toList());
    }

    public List<Subject> getSubjectsNotEnrolled(Long studentId) {
        Long tutorId = securityService.getCurrentTutorId();
        return subjectRepository.findSubjectsNotEnrolledByStudent(studentId, tutorId);
    }


    @Data
    @AllArgsConstructor
    public static class SubjectWithTopicCount {
        private Subject subject;
        private long topicCount;
    }


}
