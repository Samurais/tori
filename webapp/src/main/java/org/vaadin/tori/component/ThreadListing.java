package org.vaadin.tori.component;

import java.util.List;

import org.vaadin.tori.ToriNavigator;
import org.vaadin.tori.data.entity.Thread;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ThreadListing extends CustomComponent {

    private final VerticalLayout layout = new VerticalLayout();

    public ThreadListing() {
        setCompositionRoot(layout);
    }

    public void setThreads(final List<Thread> threadsInCategory) {
        layout.removeAllComponents();
        for (final Thread thread : threadsInCategory) {
            final String threadUrl = ToriNavigator.ApplicationView.THREADS
                    .getUrl();
            final long id = thread.getId();
            final String topic = thread.getTopic();
            layout.addComponent(new Label(String.format(
                    "<a href=\"#%s/%s\">%s</a>", threadUrl, id, topic),
                    Label.CONTENT_XHTML));
        }
    }
}