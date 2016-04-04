package org.nrg.framework.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class TreeNode<T> {
    public TreeNode<T> getParent() {
        return _parent;
    }

    public void setParent(final TreeNode<T> parent) {
        _parent = parent;
    }

    public T getData() {
        return _data;
    }

    public void setData(final T data) {
        _data = data;
    }

    public Collection<TreeNode<T>> getChildren() {
        return _children;
    }

    public void setChildren(final Collection<TreeNode<T>> children) {
        for (final TreeNode<T> child : children) {
            child.setParent(this);
        }
        _children = children;
    }

    public void setChildren(final TreeNode<T>... children) {
        setChildren(Arrays.asList(children));
    }

    public List<TreeNode<T>> getAncestry() {
        final List<TreeNode<T>> ancestry = new ArrayList<>();
        ancestry.add(this);
        TreeNode<T> current = this;
        while (current.getParent() != null) {
            current = current.getParent();
            ancestry.add(current);
        }
        return ancestry;
    }

    private TreeNode<T>             _parent;
    private T                       _data;
    private Collection<TreeNode<T>> _children;
}

