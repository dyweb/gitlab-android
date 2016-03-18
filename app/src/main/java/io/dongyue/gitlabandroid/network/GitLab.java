package io.dongyue.gitlabandroid.network;

import android.support.annotation.Nullable;

import java.util.List;

import io.dongyue.gitlabandroid.model.api.Branch;
import io.dongyue.gitlabandroid.model.api.Contributor;
import io.dongyue.gitlabandroid.model.api.Diff;
import io.dongyue.gitlabandroid.model.api.Group;
import io.dongyue.gitlabandroid.model.api.GroupDetail;
import io.dongyue.gitlabandroid.model.api.Issue;
import io.dongyue.gitlabandroid.model.api.Member;
import io.dongyue.gitlabandroid.model.api.MergeRequest;
import io.dongyue.gitlabandroid.model.api.Milestone;
import io.dongyue.gitlabandroid.model.api.Note;
import io.dongyue.gitlabandroid.model.api.Project;
import io.dongyue.gitlabandroid.model.api.RepositoryCommit;
import io.dongyue.gitlabandroid.model.api.RepositoryFile;
import io.dongyue.gitlabandroid.model.api.User;
import io.dongyue.gitlabandroid.model.api.UserBasic;
import io.dongyue.gitlabandroid.model.api.UserFull;
import io.dongyue.gitlabandroid.model.api.UserLogin;
import retrofit.Call;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.Url;
import rx.Observable;

/**
 * Defines the interface for Retrofit for the GitLab API
 * http://doc.gitlab.com/ce/api/README.html
 */
public interface GitLab {

    String BASE_URL = "https://git.tongqu.me/";
    String API_VERSION = "api/v3";

    /* --- LOGIN --- */

    @FormUrlEncoded
    @POST(API_VERSION + "/session")
    Observable<UserLogin> loginWithUsername(@Field("login") String login,
                                      @Field("password") String password);

    @FormUrlEncoded
    @POST(API_VERSION + "/session")
    Observable<UserLogin> loginWithEmail(@Field("email") String email,
                                   @Field("password") String password);

    /* --- USERS --- */

    /**
     * Get currently authenticated user
     */
    @GET(API_VERSION + "/user")
    Observable<UserFull> getThisUser();

    @GET(API_VERSION + "/users")
    Observable<List<UserBasic>> getUsers();

    @GET
    Observable<List<UserBasic>> getUsers(@Url String url);

    @GET(API_VERSION + "/users")
    Observable<List<UserBasic>> searchUsers(@Query("search") String query);

    @GET
    Observable<List<UserBasic>> searchUsers(@Url String url, @Query("search") String query);

    @GET(API_VERSION + "/users/{id}")
    Call<User> getUser(@Path("id") long userId);

    /* --- GROUPS --- */

    @GET(API_VERSION + "/groups")
    Call<List<Group>> getGroups();

    @GET
    Call<List<Group>> getGroups(@Url String url);

    @GET(API_VERSION + "/groups/{id}")
    Call<GroupDetail> getGroup(@Path("id") long id);

    @GET(API_VERSION + "/groups/{id}/projects?order_by=last_activity_at")
    Call<List<Project>> getGroupProjects(@Path("id") long id);

    @GET(API_VERSION + "/groups/{id}/members")
    Call<List<Member>> getGroupMembers(@Path("id") long groupId);

    @FormUrlEncoded
    @POST(API_VERSION + "/groups/{id}/members")
    Call<Member> addGroupMember(@Path("id") long groupId,
                                @Field("user_id") long userId,
                                @Field("access_level") int accessLevel);

    @FormUrlEncoded
    @PUT(API_VERSION + "/groups/{id}/members/{user_id}")
    Call<Member> editGroupMember(@Path("id") long groupId,
                                 @Path("user_id") long userId,
                                 @Field("access_level") int accessLevel);

    @DELETE(API_VERSION + "/groups/{id}/members/{user_id}")
    Call<Void> removeGroupMember(@Path("id") long groupId,
                                 @Path("user_id") long userId);

    /* --- PROJECTS --- */

    @GET(API_VERSION + "/projects")
    Observable<List<Project>> getAllProjects();

    @GET(API_VERSION + "/projects")
    Observable<List<Project>> getAllProjects(@Query("page") int page);

    /*@GET("act/type")
    Observable<ActListResponse> getActList(@Query("type") int typeId,@Query("offset") int offset,@Query("order") String order);*/

    @GET(API_VERSION + "/projects/owned?order_by=last_activity_at")
    Call<List<Project>> getMyProjects();

    @GET(API_VERSION + "/projects/starred")
    Call<List<Project>> getStarredProjects();

    @GET(API_VERSION + "/projects/{id}")
    Observable<Project> getProject(@Path("id") long projectId);

    @GET
    Call<List<Project>> getProjects(@Url String url);

    @GET(API_VERSION + "/projects/search/{query}")
    Call<List<Project>> searchAllProjects(@Path("query") String query);

    @GET(API_VERSION + "/projects/{id}/members")
    Call<List<Member>> getProjectMembers(@Path("id") long projectId);

    @GET
    Call<List<Member>> getProjectMembers(@Url String url);

    @FormUrlEncoded
    @POST(API_VERSION + "/projects/{id}/members")
    Call<Member> addProjectMember(@Path("id") long projectId,
                                  @Field("user_id") long userId,
                                  @Field("access_level") int accessLevel);

    @FormUrlEncoded
    @PUT(API_VERSION + "/projects/{id}/members/{user_id}")
    Call<Member> editProjectMember(@Path("id") long projectId,
                                   @Path("user_id") long userId,
                                   @Field("access_level") int accessLevel);

    @DELETE(API_VERSION + "/projects/{id}/members/{user_id}")
    Call<Void> removeProjectMember(@Path("id") long projectId,
                                   @Path("user_id") long userId);

    /* --- MILESTONES --- */

    @GET(API_VERSION + "/projects/{id}/milestones")
    Call<List<Milestone>> getMilestones(@Path("id") long projectId);

    @GET
    Call<List<Milestone>> getMilestones(@Url String url);

    @GET(API_VERSION + "/projects/{id}/milestones/{milestone_id}/issues")
    Call<List<Issue>> getMilestoneIssues(@Path("id") long projectId,
                                         @Path("milestone_id") long milestoneId);
    @GET
    Call<List<Issue>> getMilestoneIssues(@Url String url);

    @FormUrlEncoded
    @POST(API_VERSION + "/projects/{id}/milestones")
    Call<Milestone> createMilestone(@Path("id") long projectId,
                                    @Field("title") String title,
                                    @Field("description") String description,
                                    @Field("due_date") String dueDate);

    @FormUrlEncoded
    @PUT(API_VERSION + "/projects/{id}/milestones/{milestone_id}")
    Call<Milestone> editMilestone(@Path("id") long projectId,
                                  @Path("milestone_id") long milestoneId,
                                  @Field("title") String title,
                                  @Field("description") String description,
                                  @Field("due_date") String dueDate);

    /* --- MERGE REQUESTS --- */

    @GET(API_VERSION + "/projects/{id}/merge_requests")
    Call<List<MergeRequest>> getMergeRequests(@Path("id") long projectId,
                                              @Query("state") String state);

    @GET
    Call<List<MergeRequest>> getMergeRequests(@Url String url,
                                              @Query("state") String state);

    @GET(API_VERSION + "/projects/{id}/merge_request/{merge_request_id}")
    Call<MergeRequest> getMergeRequest(@Path("id") long projectId,
                                       @Path("merge_request_id") long mergeRequestId);

    @GET(API_VERSION + "/projects/{id}/merge_requests/{merge_request_id}/notes")
    Call<List<Note>> getMergeRequestNotes(@Path("id") long projectId,
                                          @Path("merge_request_id") long mergeRequestId);

    @GET
    Call<List<Note>> getMergeRequestNotes(@Url String url);

    @FormUrlEncoded
    @POST(API_VERSION + "/projects/{id}/merge_requests/{merge_request_id}/notes")
    Call<Note> addMergeRequestNote(@Path("id") long projectId,
                                   @Path("merge_request_id") long mergeRequestId,
                                   @Field("body") String body);

    /* --- ISSUES --- */

    @GET(API_VERSION + "/issues")
    Observable<List<Issue>> getAllIssues();

    @GET(API_VERSION + "/projects/{id}/issues")
    Call<List<Issue>> getIssues(@Path("id") long projectId,
                                @Query("state") String state);

    @GET
    Call<List<Issue>> getIssues(@Url String url,
                                @Query("state") String state);

    @GET(API_VERSION + "/projects/{id}/issues/{issue_id}")
    Call<Issue> getIssue(@Path("id") long projectId,
                         @Path("issue_id") long issueId);

    @FormUrlEncoded
    @POST(API_VERSION + "/projects/{id}/issues")
    Call<Issue> createIssue(@Path("id") long projectId,
                            @Field("title") String title,
                            @Field("description") String description,
                            @Field("assignee_id") @Nullable Long assigneeId,
                            @Field("milestone_id") @Nullable Long milestoneId);

    @PUT(API_VERSION + "/projects/{id}/issues/{issue_id}")
    Call<Issue> updateIssue(@Path("id") long projectId,
                            @Path("issue_id") long issueId,
                            @Query("title") String title,
                            @Query("description") String description,
                            @Query("assignee_id") @Nullable Long assigneeId,
                            @Query("milestone_id") @Nullable Long milestoneId);

    @PUT(API_VERSION + "/projects/{id}/issues/{issue_id}")
    Call<Issue> updateIssueStatus(@Path("id") long projectId,
                                  @Path("issue_id") long issueId,
                                  @Query("state_event") @Issue.EditState String status);

    @GET(API_VERSION + "/projects/{id}/issues/{issue_id}/notes")
    Call<List<Note>> getIssueNotes(@Path("id") long projectId,
                                   @Path("issue_id") long issueId);

    @GET
    Call<List<Note>> getIssueNotes(@Url String url);

    @FormUrlEncoded
    @POST(API_VERSION + "/projects/{id}/issues/{issue_id}/notes")
    Call<Note> addIssueNote(@Path("id") long projectId,
                            @Path("issue_id") long issueId,
                            @Field("body") String body);

    /* --- REPOSITORY --- */

    @GET(API_VERSION + "/projects/{id}/repository/branches?order_by=last_activity_at")
    Call<List<Branch>> getBranches(@Path("id") long projectId);

    @GET(API_VERSION + "/projects/{id}/repository/contributors")
    Call<List<Contributor>> getContributors(@Path("id") long projectId);

    /*@GET(API_VERSION + "/projects/{id}/repository/tree")
    Call<List<RepositoryTreeObject>> getTree(@Path("id") long projectId,
                                             @Query("ref_name") String branchName,
                                             @Query("path") String path);*/

    @GET(API_VERSION + "/projects/{id}/repository/files")
    Call<RepositoryFile> getFile(@Path("id") long projectId,
                                 @Query("file_path") String path,
                                 @Query("ref") String ref);

    @GET(API_VERSION + "/projects/{id}/repository/commits")
    Call<List<RepositoryCommit>> getCommits(@Path("id") long projectId,
                                            @Query("ref_name") String branchName,
                                            @Query("page") int page);

    @GET(API_VERSION + "/projects/{id}/repository/commits/{sha}")
    Call<RepositoryCommit> getCommit(@Path("id") long projectId,
                                     @Path("sha") String commitSHA);

    @GET(API_VERSION + "/projects/{id}/repository/commits/{sha}/diff")
    Call<List<Diff>> getCommitDiff(@Path("id") long projectId,
                                   @Path("sha") String commitSHA);
}