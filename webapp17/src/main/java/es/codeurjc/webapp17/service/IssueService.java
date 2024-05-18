package es.codeurjc.webapp17.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.webapp17.entity.Issue;
import es.codeurjc.webapp17.repository.IssueRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IssueService {
    @Autowired
    private IssueRepository IssueRepository;

    public List<Issue> getForums() {
        return (List<Issue>) IssueRepository.findAll();
    }
    public void createIssue(String content, String name, String email) {
        Issue issue =  new Issue(content, name, email, LocalDateTime.now());
        IssueRepository.save(issue);
    }
}
