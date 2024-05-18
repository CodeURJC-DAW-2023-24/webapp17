package es.codeurjc.webapp17.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.webapp17.entity.Issues;
import es.codeurjc.webapp17.repository.IssuesRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IssuesService {
    @Autowired
    private IssuesRepository IssuesRepository;

    public List<Issues> getForums() {
        return (List<Issues>) IssuesRepository.findAll();
    }
}
