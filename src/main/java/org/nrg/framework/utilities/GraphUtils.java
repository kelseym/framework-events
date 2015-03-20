/**
 * GraphUtils
 * (C) 2015 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 */
package org.nrg.framework.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Kevin A. Archie <karchie@wustl.edu>
 *
 */
public final class GraphUtils {
    private GraphUtils() {}	// prevent instantiation

    /**
     * Indicates that an algorithm that requires a directed acyclic graph (DAG)
     * was instead provided a cyclic graph.
     * @author Kevin A. Archie <karchie@wustl.edu>
     *
     */
    public static class CyclicGraphException extends IllegalArgumentException {
        private static final long serialVersionUID = 1L;
        private final Object partial;

        CyclicGraphException(final String msg, final Object partial) {
            super(msg);
            this.partial = partial;
        }

        CyclicGraphException(final Object partial) {
            super();
            this.partial = partial;
        }

        /**
         * Retrieves partial result from algorithm, if one is available.
         * @return partial result (type determined by throwing algorithm)
         */
        public Object getPartialResult() { return partial; }
    }


    /**
     * Use Kahn's algorithm for topological sort. Removes successfully sorted nodes from the input graph.
     * @param graph Map X -> Collection<X> ; each entry maps from a node to its incoming edges
     * @return topologically sorted X
     * @throws CyclicGraphException if graph is cyclic
     */
    public static <X> List<X> topologicalSort(final Map<X,Collection<X>> graph) throws CyclicGraphException {
        // Nodes with no incoming edges are trivially resolved.
        final Set<X> resolved = new LinkedHashSet<X>();
        for (final Iterator<Map.Entry<X,Collection<X>>> mei = graph.entrySet().iterator(); mei.hasNext(); ) {
            final Map.Entry<X,Collection<X>> me = mei.next();
            final X node = me.getKey();
            final Collection<X> edges = me.getValue();
            edges.remove(node); // ignore edges from a node to itself
            if (edges.isEmpty()) {
                resolved.add(node);
                mei.remove();
            }
        }
        
        final List<X> sorted = new ArrayList<X>(graph.size());
        while (!resolved.isEmpty()) {
            // Move one element (x) from the resolved bin to the final sorted list.
            final Iterator<X> i = resolved.iterator();
            final X x = i.next();
            i.remove();
            sorted.add(x);

            // All of the elements pointed to by x now have that edge resolved.
            for (final Iterator<Map.Entry<X,Collection<X>>> mei = graph.entrySet().iterator(); mei.hasNext(); ) {
                final Map.Entry<X,Collection<X>> me = mei.next();
                final Collection<X> incoming = me.getValue();
                incoming.remove(x);
                if (incoming.isEmpty()) {
                    resolved.add(me.getKey());
                    mei.remove();
                }
            }
        }

        if (graph.isEmpty()) {
            return sorted;
        } else {
            throw new CyclicGraphException("some nodes are in cyclic graph: " + graph.keySet(), sorted);
        }
    }
}