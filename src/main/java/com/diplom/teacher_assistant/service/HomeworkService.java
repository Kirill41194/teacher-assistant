package com.diplom.teacher_assistant.service;

import com.diplom.teacher_assistant.dto.HomeworkDTO;
import com.diplom.teacher_assistant.entity.Homework;
import com.diplom.teacher_assistant.entity.Topic;
import com.diplom.teacher_assistant.repository.HomeworkRepository;
import com.diplom.teacher_assistant.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeworkService {
    private final HomeworkRepository homeworkRepository;
    private final StudentService studentService;
    private final TopicService topicService;
    private final TopicRepository topicRepository;
    private final SecurityService securityService;


    public List<Homework> getHomeworksByStudentId(Long student_id){
        return homeworkRepository.findByStudent_StudentId(student_id);
    }

    @Transactional
    public void createNewHomework(HomeworkDTO homeworkDTO){
        if (!studentService.existByTutorId()){
            throw new IllegalArgumentException("Студент не существует");
        }

        if (!topicRepository.existsByTopicIdAndSubject_Tutor_TutorId(homeworkDTO.getTopic_id(), securityService.getCurrentTutorId())){
            throw new IllegalArgumentException("Данная тема не существует");
        }


        Homework homework = new Homework();
        homework.setStudent(studentService.getStudentById(homeworkDTO.getStudent_id()));
        homework.setTopic(topicService.getTopicById(homeworkDTO.getTopic_id()));
        homework.setCreatedAt(homeworkDTO.getCreatedAt());
        homework.setGeneratedText(homeworkDTO.getGeneratedText());
        homework.setDeadline(homeworkDTO.getDeadline());
        homeworkDTO.setDifficulty(homeworkDTO.getDifficulty());

        homeworkRepository.save(homework);

    }
}
