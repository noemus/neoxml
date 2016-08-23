/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 * This software is open source.
 * See the bottom of this file for the licence.
 */
package org.neoxml.rule;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.neoxml.Attribute;
import org.neoxml.Document;
import org.neoxml.Element;
import org.neoxml.Node;
import org.neoxml.NodeType;

/**
 * <p>
 * <code>Mode</code> manages a number of RuleSet instances for the mode in a stylesheet. It is responsible for finding
 * the correct rule for a given neoxml node using the XSLT processing model uses the smallest possible RuleSet to reduce
 * the number of Rule evaluations.
 * </p>
 *
 * @author <a href="mailto:james.strachan@metastuff.com">James Strachan </a>
 * @version $Revision: 1.9 $
 */
public class Mode
{

  private final EnumMap<NodeType,RuleSet> ruleSets = new EnumMap<>(NodeType.class);
  /**
   * Map of exact (local) element names to RuleSet instances
   */
  private Map<String,RuleSet> elementNameRuleSets;
  /**
   * Map of exact (local) attribute names to RuleSet instances
   */
  private Map<String,RuleSet> attributeNameRuleSets;

  public Mode() {}

  /**
   * Runs the actions associated with the given node
   *
   * @param node DOCUMENT ME!
   * @throws Exception DOCUMENT ME!
   */
  public void fireRule(Node node) throws Exception {
    if (node != null) {
      Rule rule = getMatchingRule(node);

      if (rule != null) {
        Action action = rule.getAction();

        if (action != null) {
          action.run(node);
        }
      }
    }
  }

  public void applyTemplates(Element element) throws Exception {
    for (int i = 0, size = element.attributeCount(); i < size; i++) {
      Attribute attribute = element.attribute(i);
      fireRule(attribute);
    }

    for (int i = 0, size = element.nodeCount(); i < size; i++) {
      Node node = element.node(i);
      fireRule(node);
    }
  }

  public void applyTemplates(Document document) throws Exception {
    for (int i = 0, size = document.nodeCount(); i < size; i++) {
      Node node = document.node(i);
      fireRule(node);
    }
  }

  public void addRule(Rule rule) {
    final NodeType matchType = rule.getMatchType();
    final String name = rule.getMatchesNodeName();

    if (name != null) {
      switch (matchType) {
        case ELEMENT_NODE:
          elementNameRuleSets = addToNameMap(elementNameRuleSets, name, rule);
          break;
        case ATTRIBUTE_NODE:
          attributeNameRuleSets = addToNameMap(attributeNameRuleSets, name, rule);
          break;

        default:
          break;
      }
    }

    if (matchType == NodeType.ANY_NODE) {
      // add rule to all other RuleSets if they exist
      for (RuleSet ruleSet : this.ruleSets.values()) {
        ruleSet.addRule(rule);
      }
    }

    getRuleSet(matchType).addRule(rule);
  }

  public void removeRule(Rule rule) {
    NodeType matchType = rule.getMatchType();
    String name = rule.getMatchesNodeName();

    if (name != null) {
      switch (matchType) {
        case ELEMENT_NODE:
          removeFromNameMap(elementNameRuleSets, name, rule);
          break;
        case ATTRIBUTE_NODE:
          removeFromNameMap(attributeNameRuleSets, name, rule);
          break;

        default:
          break;
      }
    }

    getRuleSet(matchType).removeRule(rule);

    if (matchType != NodeType.ANY_NODE) {
      getRuleSet(NodeType.ANY_NODE).removeRule(rule);
    }
  }

  /**
   * Performs an XSLT processing model match for the rule which matches the
   * given Node the best.
   *
   * @param node is the neoxml node to match against
   * @return the matching Rule or no rule if none matched
   */
  public Rule getMatchingRule(Node node) {
    NodeType matchType = node.getNodeTypeEnum();

    switch (matchType) {
      case ELEMENT_NODE:
        if (elementNameRuleSets != null) {
          String name = node.getName();
          RuleSet ruleSet = elementNameRuleSets.get(name);

          if (ruleSet != null) {
            Rule answer = ruleSet.getMatchingRule(node);

            if (answer != null) {
              return answer;
            }
          }
        }
        break;
      case ATTRIBUTE_NODE:
        if (attributeNameRuleSets != null) {
          String name = node.getName();
          RuleSet ruleSet = attributeNameRuleSets.get(name);

          if (ruleSet != null) {
            Rule answer = ruleSet.getMatchingRule(node);

            if (answer != null) {
              return answer;
            }
          }
        }
        break;

      default:
        break;
    }

    Rule answer = null;
    RuleSet ruleSet = ruleSets.get(matchType);

    if (ruleSet != null) {
      // try rules that match this kind of node first
      answer = ruleSet.getMatchingRule(node);
    }

    if ((answer == null) && (matchType != NodeType.ANY_NODE)) {
      // try general rules that match any kind of node
      ruleSet = ruleSets.get(NodeType.ANY_NODE);

      if (ruleSet != null) {
        answer = ruleSet.getMatchingRule(node);
      }
    }

    return answer;
  }

  /**
   * DOCUMENT ME!
   *
   * @param matchType DOCUMENT ME!
   * @return the RuleSet for the given matching type. This method will never
   *         return null, a new instance will be created.
   */
  protected RuleSet getRuleSet(NodeType matchType) {
    RuleSet ruleSet = ruleSets.get(matchType);

    if (ruleSet == null) {
      ruleSet = new RuleSet();
      ruleSets.put(matchType, ruleSet);

      // add the patterns that match any node
      if (matchType != NodeType.ANY_NODE) {
        RuleSet allRules = ruleSets.get(NodeType.ANY_NODE);

        if (allRules != null) {
          ruleSet.addAll(allRules);
        }
      }
    }

    return ruleSet;
  }

  /**
   * Adds the Rule to a RuleSet for the given name.
   *
   * @param map DOCUMENT ME!
   * @param name DOCUMENT ME!
   * @param rule DOCUMENT ME!
   * @return the Map (which will be created if the given map was null
   */
  protected Map<String,RuleSet> addToNameMap(Map<String,RuleSet> map, String name, Rule rule) {
    if (map == null) {
      map = new HashMap<>();
    }

    RuleSet ruleSet = map.computeIfAbsent(name, n -> new RuleSet());

    ruleSet.addRule(rule);

    return map;
  }

  protected void removeFromNameMap(Map<String,RuleSet> map, String name, Rule rule) {
    if (map != null) {
      RuleSet ruleSet = map.get(name);

      if (ruleSet != null) {
        ruleSet.removeRule(rule);
      }
    }
  }
}

/*
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 3. The name "DOM4J" must not be used to endorse or promote products derived
 * from this Software without prior written permission of MetaStuff, Ltd. For
 * written permission, please contact dom4j-info@metastuff.com.
 * 4. Products derived from this Software may not be called "DOM4J" nor may
 * "DOM4J" appear in their names without prior written permission of MetaStuff,
 * Ltd. DOM4J is a registered trademark of MetaStuff, Ltd.
 * 5. Due credit should be given to the DOM4J Project - http://dom4j.sourceforge.net
 * THIS SOFTWARE IS PROVIDED BY METASTUFF, LTD. AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL METASTUFF, LTD. OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 */
