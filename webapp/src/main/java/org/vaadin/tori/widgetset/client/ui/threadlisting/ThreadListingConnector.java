package org.vaadin.tori.widgetset.client.ui.threadlisting;

import java.util.List;

import org.vaadin.tori.widgetset.client.ui.threadlisting.ThreadData.ThreadAdditionalData;
import org.vaadin.tori.widgetset.client.ui.threadlisting.ThreadData.ThreadPrimaryData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.ui.AbstractComponentContainerConnector;
import com.vaadin.shared.ui.Connect;

@SuppressWarnings("serial")
@Connect(org.vaadin.tori.view.listing.thread.ThreadListing.class)
public class ThreadListingConnector extends AbstractComponentContainerConnector {

    private final ThreadListingServerRpc rpc = RpcProxy.create(
            ThreadListingServerRpc.class, this);

    @Override
    protected void init() {
        super.init();
        getWidget().init(rpc, rpc);
        getWidget().attachScrollHandlersIfNeeded(
                getConnection().getUIConnector().getWidget());

        registerRpc(ThreadListingClientRpc.class, new ThreadListingClientRpc() {

            @Override
            public void removeThreadRow(final long threadId) {
                getWidget().removeThreadRow(threadId);
            }

            @Override
            public void sendRows(final List<ThreadPrimaryData> rows,
                    final int placeholders) {
                getWidget().addRows(rows, placeholders);
            }

            @Override
            public void refreshThreadRows(final List<ThreadAdditionalData> rows) {
                getWidget().refreshRows(rows);
            }
        });
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(ThreadListingWidget.class);
    }

    @Override
    public ThreadListingWidget getWidget() {
        return (ThreadListingWidget) super.getWidget();
    }

    @Override
    public void updateCaption(final ComponentConnector connector) {
        // Not supported
    }

    @Override
    public void onConnectorHierarchyChange(
            final ConnectorHierarchyChangeEvent connectorHierarchyChangeEvent) {
        // Ignore
    }

}
