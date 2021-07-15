package com.alibaba.jvm.sandbox.repeater.aide.compare;

import java.util.List;

/**
 * {@link Comparable}
 * <p>
 *
 * @author zhaoyb1990
 */
public interface Comparable {

    /**
     * compare to object
     *
     * @param left  left object to be compare
     * @param right right object to be compare
     * @param right excludeField exclude field
     * @return compare result
     */
    CompareResult compare(Object left, Object right, List<String> excludeField);
}
