/*
 * Copyright (c) 2013 TDP Ltd. All Rights Reserved.
 * 
 * THIS WORK IS  SUBJECT  TO  U.S.  AND  INTERNATIONAL  COPYRIGHT  LAWS  AND
 * TREATIES.   NO  PART  OF  THIS  WORK MAY BE  USED,  PRACTICED,  PERFORMED
 * COPIED, DISTRIBUTED, REVISED, MODIFIED, TRANSLATED,  ABRIDGED, CONDENSED,
 * EXPANDED,  COLLECTED,  COMPILED,  LINKED,  RECAST, TRANSFORMED OR ADAPTED
 * WITHOUT THE PRIOR WRITTEN CONSENT OF TDP LTD. ANY USE OR EXPLOITATION
 * OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO
 * CRIMINAL AND CIVIL LIABILITY.
 */
package java.util.function;

/**
 * Determines if the input object matches some criteria.
 * <p>
 * All predicate implementations are expected to:
 * <ul>
 * <li>Provide stable results such that for any {@code t} the result of two {@code eval} operations
 * are always equivalent. ie.
 *
 * <pre>
 * boolean one = predicate.test(a);
 * boolean two = predicate.test(a);
 *
 * assert one == two;
 * </pre>
 *
 * </li>
 * <li>Equivalent input objects should map to equivalent output objects. ie.
 *
 * <pre>
 * assert a.equals(b);  // a and b are equivalent
 *
 * boolean x = predicate.test(a);
 * boolean y = predicate.test(ab;
 *
 * assert x == y; // their test results should be the same.
 * </pre>
 *
 * </li>
 * <li>The predicate should not modify the input object in any way that would change the evaluation.
 * </li>
 * <li>When used for aggregate operations upon many elements predicates should not assume that the
 * {@code test} operation will be called upon elements in any specific order.</li>
 * </ul>
 *
 * @param <T> the type of input objects provided to {@code test}.
 */
public interface Predicate<T>
{
  /**
   * Return {@code true} if the input object matches some criteria.
   *
   * @param t the input object.
   * @return {@code true} if the input object matched some criteria.
   */
  boolean test(T t);
}
