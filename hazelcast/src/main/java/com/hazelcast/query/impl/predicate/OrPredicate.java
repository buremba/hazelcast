/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.query.impl.predicate;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.query.ConnectorPredicate;
import com.hazelcast.query.IndexAwarePredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.resultset.OrResultSet;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryableEntry;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Or Predicate
 */
public class OrPredicate implements IndexAwarePredicate, DataSerializable, ConnectorPredicate {

    private Predicate[] predicates;

    public OrPredicate() {
    }


    public OrPredicate(Predicate... predicates) {
        this.predicates = predicates;
    }

    @Override
    public Set<QueryableEntry> filter(QueryContext queryContext) {
        List<Set<QueryableEntry>> indexedResults = new LinkedList<Set<QueryableEntry>>();

        QueryContext.QueryPlan queryPlan = queryContext.getQueryPlan(false);
        for (QueryContext.IndexPredicate entry : queryPlan.getPlan()) {
            IndexAwarePredicate key = entry.getPredicate();
            if (key != null) {
                indexedResults.add(key.filter(queryContext));
            } else {
                indexedResults.add(entry.getIndex().getRecords());
            }
        }

        return indexedResults.isEmpty() ? null : new OrResultSet(indexedResults);
    }

    @Override
    public boolean isIndexed(QueryContext queryContext) {
        QueryContext.QueryPlan queryPlan = queryContext.getQueryPlan(false);
        return queryPlan.getNotIndexedPredicates().size() == 0;
    }

    public boolean equals(Object predicate) {
        if (predicates.length == 1) {
            return predicates[0].equals(predicate);
        }
        if (predicate instanceof ConnectorPredicate) {
            ConnectorPredicate p = (ConnectorPredicate) predicate;
            return this.isSubset(p) && p.isSubset(this);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (predicates.length == 1) {
            return predicates[0].hashCode();
        }
        return Arrays.hashCode(predicates);
    }

    @Override
    public boolean apply(Map.Entry mapEntry) {
        for (Predicate predicate : predicates) {
            if (predicate.apply(mapEntry)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean in(Predicate predicate) {
        for (Predicate p : predicates) {
            if (p.in(predicate)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(predicates.length);
        for (Predicate predicate : predicates) {
            out.writeObject(predicate);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        predicates = new Predicate[size];
        for (int i = 0; i < size; i++) {
            predicates[i] = in.readObject();
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("(");
        int size = predicates.length;
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(" OR ");
            }
            sb.append(predicates[i]);
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public OrPredicate subtract(Predicate predicate) {
        if (!isSubset(predicate)) {
            return null;
        }

        List<Predicate> listPredicate = new LinkedList(Arrays.asList(predicates));

        if (predicate instanceof ConnectorPredicate) {
            for (Predicate p : ((ConnectorPredicate) predicate).getPredicates()) {
                listPredicate.remove(p);
            }
        } else {
            listPredicate.remove(predicate);
        }
        return new OrPredicate(listPredicate.toArray(new Predicate[listPredicate.size()]));
    }

    @Override
    public ConnectorPredicate copy() {
        return new OrPredicate(predicates);
    }

    @Override
    public void removeChild(int index) {
        Predicate[] newPredicates = new Predicate[predicates.length - 1];
        for (int i = 0; i < predicates.length; i++) {
            if (i < index) {
                newPredicates[i] = predicates[i];
            } else if (i > index) {
                newPredicates[i - 1] = predicates[i];
            }
        }
        predicates = newPredicates;
    }

    @Override
    public int getPredicateCount() {
        return predicates.length;
    }


    @Override
    public Predicate[] getPredicates() {
        return predicates;
    }

    @Override
    public boolean isSubset(Predicate predicate) {
        if (predicate instanceof ConnectorPredicate) {
            for (Predicate pInline : ((ConnectorPredicate) predicate).getPredicates()) {
                if (!this.isSubset(pInline)) {
                    return false;
                }
            }
            return true;
        } else {
            for (Predicate p : predicates) {
                if (p instanceof ConnectorPredicate) {
                    if (((ConnectorPredicate) p).isSubset(predicate)) {
                        return true;
                    }
                } else if (predicate.equals(p)) {
                    return true;
                }
            }
        }
        return false;
    }

}
