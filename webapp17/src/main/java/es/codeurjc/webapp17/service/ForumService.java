package es.codeurjc.webapp17.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.webapp17.entity.Issues;
import es.codeurjc.webapp17.repository.ForumRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ForumService {
    @Autowired
    private ForumRepository forumRepository;

    public List<Issues> getForums() {
        return forumRepository.findAll();
    }
}
