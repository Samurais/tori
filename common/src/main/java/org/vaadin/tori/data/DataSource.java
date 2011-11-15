package org.vaadin.tori.data;

import java.util.List;
import java.util.Set;

import org.vaadin.tori.data.entity.Category;
import org.vaadin.tori.data.entity.DiscussionThread;
import org.vaadin.tori.data.entity.Post;
import org.vaadin.tori.data.entity.User;

public interface DataSource {

    /**
     * Returns a list of all root {@link Category} instances.
     * 
     * @return all root {@link Category} instances.
     */
    List<Category> getRootCategories();

    /**
     * Get all {@link Category Categories} that have <code>category</code> as
     * their parent.
     * 
     * @param root
     *            The parent <code>Category</code> for the queried
     *            <code>Categories</code>.
     */
    List<Category> getSubCategories(Category category);

    /**
     * Get all threads in the given <code>category</code>, ordered by most
     * recent activity first.
     */
    List<DiscussionThread> getThreads(Category category);

    /**
     * Returns the Category corresponding to the id or {@code null} if no such
     * Category exist.
     */
    Category getCategory(long categoryId);

    /**
     * Returns the number {@link DiscussionThread}s in the given
     * {@link Category}.
     * 
     * @param category
     *            Category from which to count the threads.
     * @return number of {@link DiscussionThread}s
     */
    long getThreadCount(Category category);

    /**
     * Returns the {@link DiscussionThread} corresponding to the id or
     * <code>null</code> if no such <code>DiscussionThread</code> exists.
     */
    DiscussionThread getThread(long threadId);

    /**
     * Returns all {@link Post Posts} in a {@link Thread} in ascending time
     * order (oldest, i.e. first, post first).
     */
    List<Post> getPosts(Thread thread);

    /**
     * Returns {@code true} if the given {@link User} has administrator
     * privileges.
     * 
     * @param user
     *            User to check for administrator privileges.
     * @return {@code true} if the given user has administrator privileges.
     */
    boolean isAdministrator(User user);

    /**
     * Saves all changes made to the given {@link Category Categories}.
     * 
     * @param categoriesToSave
     *            {@link Category Categories} to save.
     */
    void saveCategories(Set<Category> categoriesToSave);

    /**
     * Saves all changes made to the given {@link Category Category} or adds it
     * if it's a new Category.
     * 
     * @param categoryToSave
     *            {@link Category Category} to save.
     */
    void saveCategory(Category categoryToSave);
}
