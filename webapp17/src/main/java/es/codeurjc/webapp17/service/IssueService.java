package es.codeurjc.webapp17.service;

import java.util.ArrayList;
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
    public void addIssue(Issue issue) {
        IssueRepository.save(issue);
    }
    public ArrayList<Issue> getAllIssues() {
        return new ArrayList<Issue>(IssueRepository.findAll());
        
    }

     public void deleteIssue(Long id) {
        IssueRepository.delete(IssueRepository.findById(id).get());
    }
}
