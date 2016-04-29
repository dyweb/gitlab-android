package io.dongyue.gitlabandroid.utils.eventbus.events;

/**
 * Created by Brotherjing on 2016/4/29.
 */
public class SwitchBranchEvent {

    private String branchName;

    public SwitchBranchEvent(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchName() {
        return branchName;
    }
}
