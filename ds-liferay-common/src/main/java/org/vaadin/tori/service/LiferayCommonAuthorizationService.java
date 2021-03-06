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

package org.vaadin.tori.service;

import javax.portlet.PortletRequest;

import org.apache.log4j.Logger;
import org.vaadin.tori.PortletRequestAware;
import org.vaadin.tori.data.LiferayCommonDataSource;
import org.vaadin.tori.exception.DataSourceException;
import org.vaadin.tori.service.LiferayCommonAuthorizationConstants.CategoryAction;
import org.vaadin.tori.service.LiferayCommonAuthorizationConstants.MbAction;
import org.vaadin.tori.service.LiferayCommonAuthorizationConstants.MessageAction;

import com.liferay.portal.kernel.exception.NestableException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.model.MBThread;
import com.liferay.portlet.messageboards.service.MBBanLocalServiceUtil;
import com.liferay.portlet.messageboards.service.MBMessageLocalServiceUtil;
import com.liferay.portlet.messageboards.service.MBThreadLocalServiceUtil;

public class LiferayCommonAuthorizationService implements AuthorizationService,
        PortletRequestAware {

    private static final Logger log = Logger
            .getLogger(LiferayCommonAuthorizationService.class);
    private long scopeGroupId = -1;
    private String currentUser;
    private boolean banned;

    @Override
    public boolean mayEditCategories() {
        return hasPermission(MbAction.ADD_CATEGORY);
    }

    @Override
    public boolean mayRearrangeCategories() {
        // Liferay doesn't support reordering of categories.
        return false;
    }

    @Override
    public boolean mayReportPosts() {
        return isLoggedIn() && !isBanned();
    }

    @Override
    public boolean mayFollowCategory(Long categoryId) {
        return hasCategoryPermission(CategoryAction.SUBSCRIBE, categoryId);
    }

    @Override
    public boolean mayDeleteCategory(Long categoryId) {
        return hasCategoryPermission(CategoryAction.DELETE, categoryId);
    }

    @Override
    public boolean mayEditCategory(Long categoryId) {
        return hasCategoryPermission(CategoryAction.UPDATE, categoryId);
    }

    @Override
    public boolean mayEditPost(long postId) {
        return hasMessagePermission(MessageAction.UPDATE, postId);
    }

    @Override
    public boolean mayReplyInThread(long threadid) {
        MBThread mbThread = null;
        try {
            mbThread = MBThreadLocalServiceUtil.getThread(threadid);
        } catch (final NestableException e) {
            log.error(e);
        }
        return mbThread != null
                && !mbThread.isLocked()
                && hasCategoryPermission(CategoryAction.REPLY_TO_MESSAGE,
                        mbThread.getCategoryId());
    }

    @Override
    public boolean mayAddFilesInCategory(Long categoryId) {
        return hasCategoryPermission(CategoryAction.ADD_FILE, categoryId);
    }

    @Override
    public boolean mayBan() {
        return hasPermission(MbAction.BAN_USER);
    }

    @Override
    public boolean mayFollowThread(long threadId) {
        try {
            return hasMessagePermission(MessageAction.SUBSCRIBE,
                    LiferayCommonDataSource.getRootMessageId(threadId));
        } catch (final DataSourceException e) {
            log.error(e);
        }
        return false;
    }

    @Override
    public boolean mayDeletePost(long postId) {
        return hasMessagePermission(MessageAction.DELETE, postId);
    }

    @Override
    public boolean mayVote() {
        return isLoggedIn() && !isBanned();
    }

    @Override
    public boolean mayMoveThreadInCategory(Long categoryId) {
        return hasCategoryPermission(CategoryAction.MOVE_THREAD, categoryId);
    }

    @Override
    public boolean mayStickyThreadInCategory(Long categoryId) {
        return hasCategoryPermission(CategoryAction.UPDATE_THREAD_PRIORITY,
                categoryId);
    }

    @Override
    public boolean mayLockThreadInCategory(Long categoryId) {
        return hasCategoryPermission(CategoryAction.LOCK_THREAD, categoryId);
    }

    @Override
    public boolean mayDeleteThread(long threadId) {
        try {
            return hasMessagePermission(MessageAction.DELETE,
                    LiferayCommonDataSource.getRootMessageId(threadId));
        } catch (final DataSourceException e) {
            log.error(e);
        }
        return false;
    }

    @Override
    public boolean mayCreateThreadInCategory(Long categoryId) {
        return hasCategoryPermission(CategoryAction.ADD_MESSAGE, categoryId);
    }

    private PermissionChecker getPermissionChecker() {
        final PermissionChecker pc = PermissionThreadLocal
                .getPermissionChecker();
        if (pc == null) {
            throw new IllegalStateException(
                    "PermissionChecker is not initialized.");
        }
        return pc;
    }

    private boolean hasCategoryPermission(final CategoryAction action,
            final Long categoryId) {
        if (isBanned()) {
            return false;
        }
        return getPermissionChecker().hasPermission(scopeGroupId,
                CategoryAction.getScope(),
                LiferayCommonDataSource.normalizeCategoryId(categoryId),
                action.toString());
    }

    private boolean hasMessagePermission(final MessageAction action,
            final long messageId) {
        if (isBanned()) {
            return false;
        }

        try {
            final MBMessage message = MBMessageLocalServiceUtil
                    .getMBMessage(messageId);

            // check for owner permission
            if (getPermissionChecker().hasOwnerPermission(
                    message.getCompanyId(), MBMessage.class.getName(),
                    message.getRootMessageId(), message.getUserId(),
                    action.toString())) {
                return true;
            }

            // check for other permissions
            return getPermissionChecker().hasPermission(message.getGroupId(),
                    MBMessage.class.getName(), message.getRootMessageId(),
                    action.toString());
        } catch (final PortalException e) {
            log.error(e);
        } catch (final SystemException e) {
            log.error(e);
        }
        // default to false
        return false;
    }

    private boolean hasPermission(final MbAction action) {
        if (isBanned()) {
            return false;
        }
        return getPermissionChecker().hasPermission(scopeGroupId,
                MbAction.getScope(), scopeGroupId, action.toString());
    }

    private boolean isBanned() {
        return banned;
    }

    private boolean isLoggedIn() {
        return currentUser != null;
    }

    private void setCurrentUser(final String user) {
        if (currentUser == null && user != null || currentUser != null
                && !currentUser.equals(user)) {
            // user has changed
            currentUser = user;
            log.debug(String.format("Current user is now %s.", currentUser));
        }
    }

    private void setBannedStatus() {
        // check the ban status and store in the banned field
        if (currentUser != null) {
            try {
                banned = MBBanLocalServiceUtil.hasBan(scopeGroupId,
                        Long.valueOf(currentUser));
            } catch (final SystemException e) {
                log.error("Cannot check ban status for user " + currentUser, e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setRequest(final PortletRequest request) {
        if (scopeGroupId < 0) {
            // scope not defined yet -> get if from the request
            final ThemeDisplay themeDisplay = (ThemeDisplay) request
                    .getAttribute("THEME_DISPLAY");

            if (themeDisplay != null) {
                scopeGroupId = themeDisplay.getScopeGroupId();
                log.debug("Using groupId " + scopeGroupId + " as the scope.");
            }
        }

        setCurrentUser(request.getRemoteUser());
        setBannedStatus();
    }

    @Override
    public boolean mayViewCategory(Long categoryId) {
        return hasCategoryPermission(CategoryAction.VIEW, categoryId);
    }

}
