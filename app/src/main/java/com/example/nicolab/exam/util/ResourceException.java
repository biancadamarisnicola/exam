package com.example.nicolab.exam.util;


import com.example.nicolab.exam.net.Issue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bianca on 30.11.2016.
 */
public class ResourceException extends RuntimeException {
    private final List<Issue> issues;

    public ResourceException(List<Issue> issues) {
        super();
        this.issues = issues;
    }

    public ResourceException(Exception e) {
        super();
        issues = new ArrayList<Issue>();
        issues.add(new Issue().add("error", e.getMessage()));
    }

    public List<Issue> getIssues() {
        return issues;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        for(Issue issue: issues) {
            sb.append(issue.toString()).append("\n");
        }
        return sb.toString();
    }
}
