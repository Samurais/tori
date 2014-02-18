package org.vaadin.tori.widgetset.client.ui.threadlisting;

import org.vaadin.tori.widgetset.client.ui.threadlisting.ThreadListingState.RowInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ui.AbstractComponentConnector;

public class ThreadListingRow extends Composite {

    private static final String ROW_CLASS_NAME = "threadlistingrow";

    @UiField
    public AnchorElement topic;
    @UiField
    public DivElement startedBy;
    @UiField
    public DivElement postCount;
    @UiField
    public DivElement latestTime;
    @UiField
    public FocusPanel settings;

    private static ThreadListingRowUiBinder uiBinder = GWT
            .create(ThreadListingRowUiBinder.class);

    interface ThreadListingRowUiBinder extends
            UiBinder<Widget, ThreadListingRow> {
    }

    public ThreadListingRow(RowInfo rowInfo) {
        initWidget(uiBinder.createAndBindUi(this));
        setWidth("100%");
        updateRowInfo(rowInfo);
    }

    public void updateRowInfo(RowInfo rowInfo) {
        setStyleName(ROW_CLASS_NAME);
        if (rowInfo.isLocked) {
            addStyleName("locked");
        }
        if (rowInfo.isSticky) {
            addStyleName("sticky");
        }
        if (rowInfo.isFollowed) {
            addStyleName("following");
        }
        if (!rowInfo.isRead) {
            addStyleName("unread");
        }

        topic.setHref(rowInfo.url);
        topic.setInnerText(rowInfo.topic);

        startedBy.setInnerText(rowInfo.author);

        String postCount = rowInfo.postCount != 0 ? String
                .valueOf(rowInfo.postCount) : "";
        this.postCount.setInnerText(postCount);

        latestTime.setInnerText(rowInfo.latestPostPretty);

        if (rowInfo.settings != null) {
            Widget settings = ((AbstractComponentConnector) rowInfo.settings)
                    .getWidget();
            this.settings.setWidget(settings);
        }
    }
}