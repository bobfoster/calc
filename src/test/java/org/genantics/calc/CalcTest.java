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
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.genantics.peggen.Node;

/**
 * Unit tests for generated parser.
 * 
 * TODO
 * 
 * These three tests uncovered bugs. There should be more.
 * Functional tests like this should be migrated to peggen project.
 * 
 * @author Bob Foster
 */
public class CalcTest extends TestCase {
  
  public void testCalc() throws Exception {
    Calc calc = new Calc("3+4*5");
    Node[] tree = calc.parse();
    assertTrue(tree != null);
    double value = calc.eval(tree[0]);
    assertTrue(value == 23.0);
  }

  public void testBadExpr() throws Exception {
    Calc calc = new Calc("(3+4*5");
    Node[] tree = calc.parse();
    assertTrue(tree == null);
    List errors = calc.getErrors();
    assertTrue(errors != null && !errors.isEmpty());
  }
  
  public void testNoExpr() throws Exception {
    Calc calc = new Calc("");
    Node[] tree = calc.parse();
    assertTrue(tree == null);
    List errors = calc.getErrors();
    assertTrue(errors == null);
  }
}
