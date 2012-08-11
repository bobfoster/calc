/*******************************************************************************
 * Copyright (C) 2003-2012 Bob Foster. All rights reserved.
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
 * 
 * Contributors:
 * 
 *    Bob Foster, initial API and implementation.
 *******************************************************************************/
 
package org.genantics.calc;

import java.util.List;
import org.genantics.peggen.Node;

/**
 * Accept an expression from the command line and evaluate it.
 * 
 * @author bobfoster
 */
public class Calc {
  
  public static void main(String[] args) {
    
    // Catenate all the args together with spaces in between.
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < args.length; i++) {
      if (i > 0)
        sb.append(" ");
      sb.append(args[i]);
    }
    
    String arg = sb.toString().trim();
    Calc calc = new Calc(arg);
    Node[] tree = calc.parse();
    
    if (tree == null) {
      List errors = calc.getErrors();
      for (Object err : errors)
        System.err.println(err.toString());
      System.exit(1);
    }
    
    try {
      prettyPrint(calc.eval(tree[0]));
    } catch (Exception e) {
      System.err.println("Evaluation error: "+e.getMessage());
    }
  }
  
  ExprBnf parser;
  private String expr;
  
  /** Package private for testing **/
  Calc(String expr) {
    this.expr = expr;
  }
  
  /** Package private for testing **/
  Node[] parse() {
    if (expr.length() > 0) {
      parser = new ExprBnf();
      return parser.parseLanguage(expr);
    }
    return null;
  }
  
  /** Package private testing **/
  List getErrors() {
    if (parser == null)
      return null;
    return parser.getErrors();
  }
  
  private static void prettyPrint(double d) {
    // Just want integers to look like integers.
    long ld = (long) d;
    if ((double)ld == d)
      System.out.println(ld);
    else
      System.out.println(d);
  }
  
  //========================== eval ==================================
  
  /**
   * Evaluate a Node of unknown type.
   * 
   * Since Nodes are generic, evaluating them is not object-oriented
   * or as fast as it might be, but it's probably as fast as it needs
   * to be for most applications. Note the use of == to compare node
   * names. This is valid in Java as long as all values have been
   * set to and compared with String constants, as they are interned.
   * Package private for testing.
   */
  double eval(Node node) throws Exception {
    String name = node.name;
    if (name == "NUMBER")
      return evalNUMBER(node);
    if (name == "Sum")
      return evalSum(node);
    if (name == "Prod")
      return evalProd(node);
    if (name == "Power")
      return evalPower(node);
    if (name == "Unary")
      return evalUnary(node);
    throw new Exception("Unexpected Node type "+name);
  }


  private double evalUnary(Node node) throws Exception {
    // Unary only appears if MINUS was specified
    return eval(node.child.next);
  }
  
  private double evalPower(Node node) throws Exception {
    // Power only appears if there are exactly two operands.
    return Math.pow(eval(node.child), eval(node.child.next));
  }
  
  private double evalProd(Node node) throws Exception {
    double result = eval(node.child);
    // Start with second child and process two at a time.
    for (Node child = node.child.next; child != null; child = child.next.next) {
      if (child.name == "MUL")
        result *= eval(child.next);
      else
        result /= eval(child.next);
    }
    return result;
  }

  private double evalSum(Node node) throws Exception {
    // Just like evalProd with different operations.
    double result = eval(node.child);
    for (Node child = node.child.next; child != null; child = child.next.next) {
      if (child.name == "ADD")
        result += eval(child.next);
      else
        result -= eval(child.next);
    }
    return result;
  }

  private double evalNUMBER(Node node) throws Exception {
    // Whitespace in this grammar can be easily trimmed.
    // Whitespace that includes comments would require more thought.
    String num = expr.substring(node.offset, node.offset+node.length).trim();
    return Double.parseDouble(num);
  }
}
