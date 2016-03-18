package io.dongyue.gitlabandroid.utils.eventbus.events;

/**
 * Created by Brotherjing on 2016/3/11.
 */
public class NewActivitiesEvent {

    private int count;

    public NewActivitiesEvent(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
