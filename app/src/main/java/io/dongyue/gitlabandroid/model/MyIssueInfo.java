package io.dongyue.gitlabandroid.model;

import io.dongyue.gitlabandroid.model.api.Issue;
import io.dongyue.gitlabandroid.model.api.Project;

/**
 * Created by Vincent on 16/3/11.
 */
public class MyIssueInfo implements Comparable<MyIssueInfo> {
    private Issue issue;
    private Project project;

    public MyIssueInfo(Issue issue, Project project) {
        this.issue = issue;
        this.project = project;
    }

    public Issue getIssue() {
        return issue;
    }

    public Project getProject() {
        return project;
    }

    public int compareTo(MyIssueInfo issueInfo) {
        return issue.getCreatedAt().compareTo(issueInfo.issue.getCreatedAt());
    }
}
