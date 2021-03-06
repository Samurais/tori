/*
 * Copyright 2012 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.vaadin.tori.view.edit;

import org.vaadin.tori.Configuration;
import org.vaadin.tori.exception.DataSourceException;
import org.vaadin.tori.mvp.Presenter;

public class EditPresenter extends Presenter<EditView> {

    public EditPresenter(final EditView view) {
        super(view);
    }

    public final void init() {
        refreshView();
    }

    @SuppressWarnings("deprecation")
    private void refreshView() {
        view.setReplacements(dataSource.getPostReplacements());
        view.setConvertMessageBoardsUrls(dataSource
                .getReplaceMessageBoardsLinks());
        view.setUpdatePageTitle(dataSource.getUpdatePageTitle());
        view.setPageTitlePrefix(dataSource.getPageTitlePrefix());
        view.setGoogleAnalyticsTrackerId(dataSource
                .getGoogleAnalyticsTrackerId());
        view.setMayNotReplyNote(dataSource.getMayNotReplyNote());
        view.setShowThreadsOnDashboard(dataSource.getShowThreadsOnDashboard());
    }

    public final void savePreferences(final Configuration config) {
        try {
            dataSource.save(config);
            view.showNotification("Preferences saved");
        } catch (final DataSourceException e) {
            view.showNotification("There was an error while saving preferences. Please see the log.");
        }
        refreshView();
    }

}
