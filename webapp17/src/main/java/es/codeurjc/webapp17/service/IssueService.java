package es.codeurjc.webapp17.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.webapp17.dto.IssueDto;
import es.codeurjc.webapp17.entity.Issue;
import es.codeurjc.webapp17.repository.IssueRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IssueService {
    
    @Autowired
    private IssueRepository issueRepository;

    /**
     * Retrieves all issues (forums).
     *
     * @return a list of IssueDto objects
     */
    public List<IssueDto> getForums() {
        List<Issue> issues = issueRepository.findAll();
        return issues.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Adds a new issue to the database.
     *
     * @param issueDto the issue DTO to be saved
     * @return the saved IssueDto
     */
    public IssueDto addIssue(IssueDto issueDto) {
        Issue issue = convertToEntity(issueDto);
        Issue savedIssue = issueRepository.save(issue);
        return convertToDto(savedIssue);
    }

    /**
     * Retrieves all issues from the database.
     *
     * @return a list of IssueDto objects
     */
    public List<IssueDto> getAllIssues() {
        List<Issue> issues = issueRepository.findAll();
        return issues.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Deletes an issue by its ID.
     *
     * @param id the ID of the issue to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteIssue(Long id) {
        try {
            if (issueRepository.existsById(id)) {
                issueRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves an issue by its ID.
     *
     * @param id the ID of the issue to retrieve
     * @return IssueDto if found, null otherwise
     */
    public IssueDto getIssueById(Long id) {
        return issueRepository.findById(id)
                .map(this::convertToDto)
                .orElse(null);
    }

    // Private mapping methods
    
    /**
     * Converts an Issue entity to IssueDto.
     *
     * @param issue the Issue entity to convert
     * @return the converted IssueDto
     */
    private IssueDto convertToDto(Issue issue) {
        if (issue == null) return null;
        
        return new IssueDto(
            issue.getId(),
            issue.getName(),
            issue.getEmail(),
            issue.getContent(),
            issue.getDate()
        );
    }

    /**
     * Converts an IssueDto to Issue entity.
     *
     * @param issueDto the IssueDto to convert
     * @return the converted Issue entity
     */
    private Issue convertToEntity(IssueDto issueDto) {
        if (issueDto == null) return null;
        
        Issue issue = new Issue();
        issue.setId(issueDto.id());
        issue.setName(issueDto.name());
        issue.setEmail(issueDto.email());
        issue.setContent(issueDto.content());
        issue.setDate(issueDto.date());
        
        return issue;
    }
}
