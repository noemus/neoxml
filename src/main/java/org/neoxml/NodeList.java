package org.neoxml;

import java.io.Serializable;
import java.util.List;
import java.util.function.Predicate;

/**
 * DOCUMENT ME!
 * Any change to this list will be reflected in attached Branch
 */
public interface NodeList<T extends Node> extends List<T>, Cloneable, Serializable {
    /**
     * @return true if node list is readonly
     */
    boolean isReadOnly();

    /**
     * Detach all nodes from parent branch
     *
     * @return this node list (for chaining calls)
     */
    NodeList<T> detach();

    /**
     * Attach all nodes to new branch
     *
     * @return this node list (for chaining calls)
     */
    NodeList<T> attach(Branch branch);

    /**
     * @return parent branch of this NodeList instance
     */
    Branch getParent();

    /**
     * Finds first node that satisfies provided condition
     *
     * @param cond provided condition
     * @return found node or null
     */
    T find(Predicate<? super T> cond);

    /**
     * Finds first node that satisfies provided condition and casts it to provided node type
     * Condition provider must ensure that only nodes of provided type are satisfied
     *
     * @param cond     provided condition
     * @param nodeType type of returned node
     * @return found node or null
     */
    <N extends T> N find(Predicate<? super T> cond, Class<N> nodeType);

    /**
     * Finds all nodes that satisfies provided condition
     * Any change to this filtered list will be reflected in attached branch and vice-versa.
     *
     * @param cond provided condition
     * @return nodelist of found nodes
     */
    NodeList<T> filter(Predicate<? super T> cond);

    /**
     * Finds all nodes that satisfies provided condition and casts them to provided node type
     * Any change to this filtered list will be reflected in attached branch and vice-versa.
     * Condition provider must ensure that only nodes of provided type are satisfied
     *
     * @param cond     provided condition
     * @param nodeType type of returned nodes
     * @return nodelist of found nodes
     */
    <N extends T> NodeList<N> filter(Predicate<? super T> cond, Class<N> nodeType);

    /**
     * Removes all nodes that satisfy provided condition
     *
     * @param cond provided condition
     * @return this node list (for chaining calls)
     */
    NodeList<T> remove(Predicate<? super T> cond);

    /**
     * Add one or more nodes
     *
     * @param n     DOCUMENT ME!
     * @param nodes DOCUMENT ME!
     */
    @SuppressWarnings("unchecked")
    void add(T n, T... nodes);

    /**
     * This method will create new instance of node list facade backed by same branch
     * Any change to this facade will be reflected in attached branch and vice-versa.
     *
     * @return node list attached to same branch
     */
    NodeList<T> facade();

    /**
     * This method will create new instance of node list with shallow copy of nodes
     * Any change to this copied list will be reflected in attached branch, however changes to the branch would not be reflected
     *
     * @return shallow copy of node list
     */
    NodeList<T> duplicate();

    /**
     * This method will create new instance of node list with all nodes cloned and detached from parent branch
     *
     * @return detached and cloned node list
     */
    NodeList<T> copy();

    /**
     * DOCUMENT ME!
     * <p>
     * This method is used in <code>setContent and setAttributes mehods and returns one of the following:<br>
     * <ul>
     * <li> <code>null</code> if it is instance of <code>EmptyNodeList</code><br>
     * <li> same instance if it is <code>DefaultNodeList</code> and has not parent branch<br>
     * <li> duplicate if it is <code>FacadeNodeList</code> or <code>FilteredNodeList</code> and has not parent branch<br>
     * <li> copy of it otherwise<br>
     * </ul>
     *
     * @return node list suitable for attaching to new parent branch
     */
    NodeList<T> content();
}
