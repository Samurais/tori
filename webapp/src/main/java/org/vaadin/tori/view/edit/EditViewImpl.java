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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.vaadin.tori.Configuration;
import org.vaadin.tori.data.DataSource;
import org.vaadin.tori.mvp.AbstractView;
import org.vaadin.tori.service.AuthorizationService;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class EditViewImpl extends AbstractView<EditView, EditPresenter>
        implements EditView {

    private VerticalLayout layout;
    private Table replacementsTable;
    private Button removeButton;
    private Button newButton;

    private CheckBox convertMessageBoardsUrls;
    private CheckBox updatePageTitle;
    private CheckBox showThreadsOnDashboard;
    private TextField pageTitlePrefix;
    private TextField analyticsTrackerIdField;
    private TextField mayNotReplyNote;

    public EditViewImpl(final DataSource dataSource,
            final AuthorizationService authorizationService) {
    }

    @Override
    protected Component createCompositionRoot() {
        layout = new VerticalLayout();
        return layout;
    }

    @Override
    public void initView() {
        layout.setSpacing(true);
        setStyleName("editview");

        layout.addComponent(buildGAField());
        layout.addComponent(buildReplacements());
        layout.addComponent(buildReplaceLinks());
        layout.addComponent(buildUpdateTitle());
        layout.addComponent(buildShowThreadsOnDashboard());
        layout.addComponent(buildMayNotreplyNote());

        Component saveButton = buildSaveButton();
        layout.addComponent(saveButton);
        layout.setComponentAlignment(saveButton, Alignment.MIDDLE_RIGHT);

        getPresenter().init();
    }

    private Component buildShowThreadsOnDashboard() {
        showThreadsOnDashboard = new CheckBox("Show threads on dashboard");
        return getFieldWrapper(
                "Check the box beneath to let Tori show threads/topics added to the root category on the front page.",
                showThreadsOnDashboard);
    }

    private Component buildSaveButton() {
        Button saveButton = new Button("Save preferences",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(final ClickEvent event) {
                        final Map<String, String> replacements = new HashMap<String, String>();

                        for (final Object itemId : replacementsTable
                                .getItemIds()) {
                            final Item item = replacementsTable.getItem(itemId);
                            replacements.put(
                                    (String) item.getItemProperty("regex")
                                            .getValue(), (String) item
                                            .getItemProperty("replacement")
                                            .getValue());

                        }

                        final String trackerId = analyticsTrackerIdField
                                .getValue();
                        final String titlePrefix = pageTitlePrefix.getValue();
                        final String fixedTrackerId = "".equals(trackerId) ? null
                                : trackerId;

                        final Configuration config = new Configuration();
                        config.setReplaceMessageBoardsLinks(convertMessageBoardsUrls
                                .getValue());
                        config.setShowThreadsOnDashboard(showThreadsOnDashboard
                                .getValue());
                        config.setReplacements(replacements);
                        config.setGoogleAnalyticsTrackerId(fixedTrackerId);
                        config.setUpdatePageTitle(updatePageTitle.getValue());
                        config.setMayNotReplyNote(mayNotReplyNote.getValue());
                        config.setPageTitlePrefix(titlePrefix);

                        getPresenter().savePreferences(config);
                    }
                });
        return saveButton;
    }

    private Component buildMayNotreplyNote() {
        mayNotReplyNote = getTextField("Default");
        return getFieldWrapper(
                "Message displayed in the end of the thread when user is not allowed to reply",
                mayNotReplyNote);
    }

    private Component buildUpdateTitle() {
        VerticalLayout layout = new VerticalLayout();

        updatePageTitle = new CheckBox("Update the page title");
        updatePageTitle.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(final ValueChangeEvent event) {
                pageTitlePrefix.setEnabled(updatePageTitle.getValue());
            }
        });
        layout.addComponent(updatePageTitle);

        pageTitlePrefix = getTextField("Default");
        pageTitlePrefix.setEnabled(false);
        layout.addComponent(pageTitlePrefix);

        return getFieldWrapper(
                "Check the box beneath to let Tori update the title of the page.",
                layout);
    }

    private Component buildReplaceLinks() {
        convertMessageBoardsUrls = new CheckBox(
                "Replace message boards link data with tori format");
        return getFieldWrapper(
                "Check the box beneath to let Tori scan post content render-time for links "
                        + "intended for Liferay message boards portlet and convert them to tori-format for display.",
                convertMessageBoardsUrls);
    }

    private Component buildReplacements() {
        VerticalLayout result = new VerticalLayout();
        layout.setSpacing(true);
        result.addComponent(getSubTitle("Define post body regex-patterns/replacements to be "
                + "applied whenever posts are being displayed/previewed."));

        replacementsTable = new Table();
        replacementsTable.setWidth("100%");
        replacementsTable.setHeight("10em");
        replacementsTable.setSelectable(true);
        replacementsTable.setImmediate(true);
        replacementsTable.setEditable(true);
        replacementsTable.setTableFieldFactory(new TableFieldFactory() {

            @Override
            public Field<?> createField(final Container container,
                    final Object itemId, final Object propertyId,
                    final Component uiContext) {
                final TextField textField = new TextField();
                textField.setWidth(100.0f, Unit.PERCENTAGE);
                textField.addFocusListener(new FocusListener() {
                    @Override
                    public void focus(final FocusEvent event) {
                        replacementsTable.setValue(itemId);
                    }
                });
                return textField;
            }
        });

        replacementsTable
                .addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(final ValueChangeEvent event) {
                        removeButton
                                .setEnabled(event.getProperty().getValue() != null);
                    }
                });

        replacementsTable.addContainerProperty("regex", String.class, "",
                "Regex", null, null);
        replacementsTable.setSortContainerPropertyId("regex");
        replacementsTable.addContainerProperty("replacement", String.class, "",
                "Replacement", null, null);
        result.addComponent(replacementsTable);

        removeButton = new Button("Remove", new Button.ClickListener() {

            @Override
            public void buttonClick(final ClickEvent event) {
                replacementsTable.removeItem(replacementsTable.getValue());
            }
        });
        removeButton.setEnabled(false);

        newButton = new Button("New", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                final Object itemId = replacementsTable.addItem();
                replacementsTable.setValue(itemId);
            }
        });

        final HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.setSpacing(true);
        buttonsLayout.addComponent(removeButton);
        buttonsLayout.addComponent(newButton);
        result.addComponent(buttonsLayout);

        return result;
    }

    private Component buildGAField() {
        analyticsTrackerIdField = getTextField("Not enabled");
        return getFieldWrapper("Google Analytics Tracker id",
                analyticsTrackerIdField);
    }

    private Component getFieldWrapper(final String subTitle,
            final Component field) {
        VerticalLayout result = new VerticalLayout();
        result.setSpacing(true);
        result.addComponents(getSubTitle(subTitle), field);
        return result;
    }

    private TextField getTextField(final String inputPrompt) {
        TextField result = new TextField();
        result.setNullRepresentation("");
        result.setNullSettingAllowed(true);
        result.setInputPrompt(inputPrompt);
        return result;
    }

    private Component getSubTitle(final String string) {
        Label titleLabel = new Label(string);
        titleLabel.addStyleName("subtitle");
        return titleLabel;
    }

    @Override
    protected EditPresenter createPresenter() {
        return new EditPresenter(this);
    }

    @Override
    public void setReplacements(final Map<String, String> postReplacements) {
        replacementsTable.removeAllItems();
        for (final Entry<String, String> entry : postReplacements.entrySet()) {

            final Item item = replacementsTable.addItem(entry);
            item.getItemProperty("regex").setValue(entry.getKey());
            item.getItemProperty("replacement").setValue(entry.getValue());
        }
        replacementsTable.sort();
    }

    @Override
    public void setConvertMessageBoardsUrls(final boolean convert) {
        convertMessageBoardsUrls.setValue(convert);
    }

    @Override
    public void setShowThreadsOnDashboard(final boolean show) {
        showThreadsOnDashboard.setValue(show);
    }

    @Override
    public void setUpdatePageTitle(final boolean update) {
        updatePageTitle.setValue(update);
    }

    @Override
    public void showNotification(final String notification) {
        Notification.show(notification);
    }

    @Override
    public void setGoogleAnalyticsTrackerId(
            final String googleAnalyticsTrackerId) {
        final String value = (googleAnalyticsTrackerId == null) ? ""
                : googleAnalyticsTrackerId;
        analyticsTrackerIdField.setValue(value);
    }

    @Override
    public String getTitle() {
        return "Preferences View";
    }

    @Override
    public void setPageTitlePrefix(final String pageTitlePrefix) {
        this.pageTitlePrefix.setValue(pageTitlePrefix);
    }

    @Override
    public void setMayNotReplyNote(final String mayNotReplyNote) {
        this.mayNotReplyNote.setValue(mayNotReplyNote);
    }

}
