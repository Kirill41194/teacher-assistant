package com.diplom.teacher_assistant.service;


import com.diplom.teacher_assistant.dto.TopicDTO;
import com.diplom.teacher_assistant.entity.Subject;
import com.diplom.teacher_assistant.entity.Topic;
import com.diplom.teacher_assistant.entity.Tutor;
import com.diplom.teacher_assistant.repository.SubjectRepository;
import com.diplom.teacher_assistant.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectService subjectService;
    private final SecurityService securityService;

    public List<Topic> getTopicsBySubjectId(Long subjectId){

        return topicRepository.findBySubject_SubjectIdOrderByName(subjectId);
    }

    public boolean existBySubjectId(Long subjectId){
        return topicRepository.existsBySubject_SubjectId(subjectId);
    }

    @Transactional
    public void createNewTopic(TopicDTO topicDTO){
        Subject subject = subjectRepository.findById(topicDTO.getSubjectId())
                .orElseThrow(() -> new IllegalArgumentException("Предмет не найден"));

        if (topicRepository.existsByNameAndSubject_SubjectId(topicDTO.getName(), topicDTO.getSubjectId())) {
            throw new IllegalArgumentException("Тема с таким названием уже существует в этом предмете");
        }

        Topic topic = new Topic();
        topic.setName(topicDTO.getName());
        topic.setDescription(topicDTO.getDescription());
        topic.setSubject(subject);

        topicRepository.save(topic);
    }

    public Topic getTopicById(Long id) {
        Long tutorId = securityService.getCurrentTutorId();
        return topicRepository.findByTopicIdAndSubject_Tutor_TutorId(id, tutorId)
                .orElseThrow(() -> new IllegalArgumentException("Тема не найдена"));
    }

    @Transactional
    public void updateTopic(Long id, TopicDTO topicDTO) {
        Topic topic = getTopicById(id);

        if (!topic.getSubject().getSubjectId().equals(topicDTO.getSubjectId())) {
            Subject newSubject = subjectRepository.findById(topicDTO.getSubjectId())
                    .orElseThrow(() -> new IllegalArgumentException("Новый предмет не найден"));

            Long tutorId = securityService.getCurrentTutorId();
            if (!newSubject.getTutor().getTutorId().equals(tutorId)) {
                throw new IllegalArgumentException("Доступ к новому предмету запрещен");
            }

            topic.setSubject(newSubject);
        }

        topic.setName(topicDTO.getName());
        topic.setDescription(topicDTO.getDescription());

        topicRepository.save(topic);
    }

    @Transactional
    public void deleteTopic(Long id) {
        Topic topic = getTopicById(id);
        topicRepository.delete(topic);
    }


}
