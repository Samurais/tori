package org.vaadin.tori.widgetset.client.ui.threadlisting;

import java.util.List;

import com.vaadin.shared.ComponentState;

public class ThreadListingState extends ComponentState {
    private static final long serialVersionUID = -4869218779372629528L;

    public static class RowInfo {
        public boolean isSticky;
        public boolean isLocked;
        public boolean isFollowed;
        public String topic;
        public String author;
        public int postCount;
        public String latestPostPretty;
        public String latestPostDate;
        public String latestPostAuthor;
        public String url;
        public boolean showTools;
        public boolean isRead;

        @Override
        public String toString() {
            return "RowInfo [isSticky=" + isSticky + ", isLocked=" + isLocked
                    + ", isFollowed=" + isFollowed + ", topic=" + topic
                    + ", author=" + author + ", postCount=" + postCount
                    + ", latestPostPretty=" + latestPostPretty
                    + ", latestPostDate=" + latestPostDate
                    + ", latestPostAuthor=" + latestPostAuthor + ", url=" + url
                    + "]";
        }

    }

    public static final int UNINITIALIZED_ROWS = -1;

    public int rows = UNINITIALIZED_ROWS;
    public List<RowInfo> preloadedRows;

}
