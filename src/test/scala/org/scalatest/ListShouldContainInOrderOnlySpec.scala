/*
 * Copyright 2001-2013 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalatest

import org.scalautils.Equality
import org.scalautils.Normalization
import org.scalautils.StringNormalizations._
import SharedHelpers._
import FailureMessages.decorateToStringValue
import scala.collection.mutable.LinkedList

class ListShouldContainInOrderOnlySpec extends Spec with Matchers {

  private def upperCase(value: Any): Any = 
    value match {
      case l: List[_] => l.map(upperCase(_))
      case s: String => s.toUpperCase
      case c: Char => c.toString.toUpperCase.charAt(0)
      case (s1: String, s2: String) => (s1.toUpperCase, s2.toUpperCase)
      case _ => value
    }

  val upperCaseStringEquality =
    new Equality[String] {
      def areEqual(a: String, b: Any): Boolean = upperCase(a) == upperCase(b)
    }

  //ADDITIONAL//

  object `a List` {

    val fumList: List[String] = List("fum", "fum", "foe", "fie", "fie", "fie", "fee", "fee")
    val toList: List[String] = List("happy", "happy", "happy", "birthday", "to", "you")

    object `when used with contain inOrderOnly (..)` {

      def `should do nothing if valid, else throw a TFE with an appropriate error message` {
        fumList should newContain newInOrderOnly ("fum", "foe", "fie", "fee")
        val e1 = intercept[TestFailedException] {
          fumList should newContain newInOrderOnly ("fee", "fie", "foe", "fum")
        }
        e1.failedCodeFileName.get should be ("ListShouldContainInOrderOnlySpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message.get should be (Resources("didNotContainInOrderOnlyElements", decorateToStringValue(fumList), "\"fee\", \"fie\", \"foe\", \"fum\""))
      }
      def `should use the implicit Equality in scope` {
        implicit val ise = upperCaseStringEquality
        fumList should newContain newInOrderOnly ("FUM", "FOE", "FIE", "FEE")
        intercept[TestFailedException] {
          fumList should newContain newInOrderOnly ("fee", "fie", "foe", "fum")
        }
      }
      def `should use an explicitly provided Equality` {
        (fumList should newContain newInOrderOnly ("FUM", "FOE", "FIE", "FEE")) (decided by upperCaseStringEquality)
        intercept[TestFailedException] {
          (fumList should newContain newInOrderOnly ("fee", "fie", "foe", "fum")) (decided by upperCaseStringEquality)
        }
        intercept[TestFailedException] {
          fumList should newContain newInOrderOnly (" FUM ", " FOE ", " FIE ", " FEE ")
        }
        (fumList should newContain newInOrderOnly (" FUM ", " FOE ", " FIE ", " FEE ")) (after being lowerCased and trimmed)
      }
    }

    object `when used with (contain inOrderOnly (..))` {
      def `should do nothing if valid, else throw a TFE with an appropriate error message` {
        fumList should (newContain newInOrderOnly ("fum", "foe", "fie", "fee"))
        val e1 = intercept[TestFailedException] {
          fumList should (newContain newInOrderOnly ("fee", "fie", "foe", "fum"))
        }
        e1.failedCodeFileName.get should be ("ListShouldContainInOrderOnlySpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message.get should be (Resources("didNotContainInOrderOnlyElements", decorateToStringValue(fumList),  "\"fee\", \"fie\", \"foe\", \"fum\""))
      }
      def `should use the implicit Equality in scope` {
        implicit val ise = upperCaseStringEquality
        fumList should (newContain newInOrderOnly ("FUM", "FOE", "FIE", "FEE"))
        intercept[TestFailedException] {
          fumList should (newContain newInOrderOnly ("fee", "fie", "foe", "fum"))
        }
      }
      def `should use an explicitly provided Equality` {
        (fumList should (newContain newInOrderOnly ("FUM", "FOE", "FIE", "FEE"))) (decided by upperCaseStringEquality)
        intercept[TestFailedException] {
          (fumList should (newContain newInOrderOnly ("fee", "fie", "foe", "fum"))) (decided by upperCaseStringEquality)
        }
        intercept[TestFailedException] {
          fumList should (newContain newInOrderOnly (" FUM ", " FOE ", " FIE ", " FEE "))
        }
        (fumList should (newContain newInOrderOnly (" FUM ", " FOE ", " FIE ", " FEE "))) (after being lowerCased and trimmed)
      }
    }

    object `when used with not contain inOrderOnly (..)` {

      def `should do nothing if valid, else throw a TFE with an appropriate error message` {
        toList should not newContain newInOrderOnly ("you", "to", "birthday", "happy")
        val e1 = intercept[TestFailedException] {
          toList should not newContain newInOrderOnly ("happy", "birthday", "to", "you")
        }
        e1.failedCodeFileName.get should be ("ListShouldContainInOrderOnlySpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message.get should be (Resources("containedInOrderOnlyElements", decorateToStringValue(toList), "\"happy\", \"birthday\", \"to\", \"you\""))
      }
      def `should use the implicit Equality in scope` {
        implicit val ise = upperCaseStringEquality
        toList should not newContain newInOrderOnly ("YOU", "TO", "BIRTHDAY", "HAPPY")
        intercept[TestFailedException] {
          toList should not newContain newInOrderOnly ("HAPPY", "BIRTHDAY", "TO", "YOU")
        }
      }
      def `should use an explicitly provided Equality` {
        (toList should not newContain newInOrderOnly ("YOU", "TO", "BIRTHDAY", "HAPPY")) (decided by upperCaseStringEquality)
        intercept[TestFailedException] {
          (toList should not newContain newInOrderOnly ("HAPPY", "BIRTHDAY", "TO", "YOU")) (decided by upperCaseStringEquality)
        }
        toList should not newContain newInOrderOnly (" HAPPY ", " BIRTHDAY ", " TO ", " YOU ")
        intercept[TestFailedException] {
          (toList should not newContain newInOrderOnly (" HAPPY ", " BIRTHDAY ", " TO ", " YOU ")) (after being lowerCased and trimmed)
        }
      }
    }

    object `when used with (not contain inOrderOnly (..))` {

      def `should do nothing if valid, else throw a TFE with an appropriate error message` {
        toList should (not newContain newInOrderOnly ("you", "to", "birthday", "happy"))
        val e1 = intercept[TestFailedException] {
          toList should (not newContain newInOrderOnly ("happy", "birthday", "to", "you"))
        }
        e1.failedCodeFileName.get should be ("ListShouldContainInOrderOnlySpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message.get should be (Resources("containedInOrderOnlyElements", decorateToStringValue(toList), "\"happy\", \"birthday\", \"to\", \"you\""))
      }
      def `should use the implicit Equality in scope` {
        implicit val ise = upperCaseStringEquality
        toList should (not newContain newInOrderOnly ("YOU", "TO", "BIRTHDAY", "HAPPY"))
        intercept[TestFailedException] {
          toList should (not newContain newInOrderOnly ("HAPPY", "BIRTHDAY", "TO", "YOU"))
        }
      }
      def `should use an explicitly provided Equality` {
        (toList should (not newContain newInOrderOnly ("YOU", "TO", "BIRTHDAY", "HAPPY"))) (decided by upperCaseStringEquality)
        intercept[TestFailedException] {
          (toList should (not newContain newInOrderOnly ("HAPPY", "BIRTHDAY", "TO", "YOU"))) (decided by upperCaseStringEquality)
        }
        toList should (not newContain newInOrderOnly (" HAPPY ", " BIRTHDAY ", " TO ", " YOU "))
        intercept[TestFailedException] {
          (toList should (not newContain newInOrderOnly (" HAPPY ", " BIRTHDAY ", " TO ", " YOU "))) (after being lowerCased and trimmed)
        }
      }
    }
  }

  object `a col of Lists` {

    val list1s: Vector[List[Int]] = Vector( List(1, 2, 2, 3), List(1, 1, 2, 3, 3, 3), List(1, 2, 3))
    val lists: Vector[List[Int]] = Vector( List(1, 2, 2, 3, 3, 3), List(1, 1, 1, 2, 3), List(2, 2, 3, 4))
    val listsNil: Vector[List[Int]] = Vector( List(1, 1, 1, 2, 2, 2, 3, 3, 3), List(1, 2, 2, 3), Nil)
    val hiLists: Vector[List[String]] = Vector( List("hi", "hi", "he"), List("hi", "he", "he", "he"), List("hi", "he"))
    val toLists: Vector[List[String]] = Vector( List("to", "you"), List("to", "you"), List("to", "you"))

    object `when used with contain inOrderOnly (..)` {

      def `should do nothing if valid, else throw a TFE with an appropriate error message` {
        all (list1s) should newContain newInOrderOnly (1, 2, 3)
        atLeast (2, lists) should newContain newInOrderOnly (1, 2, 3)
        atMost (2, lists) should newContain newInOrderOnly (1, 2, 3)
        no (lists) should newContain newInOrderOnly (3, 4, 5)

        val e1 = intercept[TestFailedException] {
          all (lists) should newContain newInOrderOnly (1, 2, 3)
        }
        e1.failedCodeFileName.get should be ("ListShouldContainInOrderOnlySpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message should be (Some("'all' inspection failed, because: \n" +
                                   "  at index 2, " + decorateToStringValue(lists(2)) + " did not contain only " + "(1, 2, 3)" + " in order (ListShouldContainInOrderOnlySpec.scala:" + (thisLineNumber - 5) + ") \n" +
                                   "in " + decorateToStringValue(lists)))

        val e3 = intercept[TestFailedException] {
          all (listsNil) should newContain newInOrderOnly (1, 2, 3)
        }
        e3.failedCodeFileName.get should be ("ListShouldContainInOrderOnlySpec.scala")
        e3.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e3.message should be (Some("'all' inspection failed, because: \n" +
                                   "  at index 2, " + decorateToStringValue(Nil) + " did not contain only " + "(1, 2, 3)" + " in order (ListShouldContainInOrderOnlySpec.scala:" + (thisLineNumber - 5) + ") \n" +
                                   "in " + decorateToStringValue(listsNil)))
      }

      def `should use the implicit Equality in scope` {
        all (hiLists) should newContain newInOrderOnly ("hi", "he")
        intercept[TestFailedException] {
          all (hiLists) should newContain newInOrderOnly ("hi", "ho")
        }
        implicit val ise = upperCaseStringEquality
        all (hiLists) should newContain newInOrderOnly ("HI", "HE")
        intercept[TestFailedException] {
          all (hiLists) should newContain newInOrderOnly ("HI", "HO")
        }
      }
      def `should use an explicitly provided Equality` {
        (all (hiLists) should newContain newInOrderOnly ("HI", "HE")) (decided by upperCaseStringEquality)
        intercept[TestFailedException] {
          (all (hiLists) should newContain newInOrderOnly ("HI", "HO")) (decided by upperCaseStringEquality)
        }
        implicit val ise = upperCaseStringEquality
        (all (hiLists) should newContain newInOrderOnly ("hi", "he")) (decided by defaultEquality[String])
        intercept[TestFailedException] {
          (all (hiLists) should newContain newInOrderOnly ("hi", "ho")) (decided by defaultEquality[String])
        }
      }
    }

    object `when used with (contain inOrderOnly (..))` {

      def `should do nothing if valid, else throw a TFE with an appropriate error message` {
        all (list1s) should (newContain newInOrderOnly (1, 2, 3))
        atLeast (2, lists) should (newContain newInOrderOnly (1, 2, 3))
        atMost (2, lists) should (newContain newInOrderOnly (1, 2, 3))
        no (lists) should (newContain newInOrderOnly (3, 4, 5))
        no (listsNil) should (newContain newInOrderOnly (3, 4, 5))

        val e1 = intercept[TestFailedException] {
          all (lists) should (newContain newInOrderOnly (1, 2, 3))
        }
        e1.failedCodeFileName.get should be ("ListShouldContainInOrderOnlySpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message should be (Some("'all' inspection failed, because: \n" +
                                   "  at index 2, " + decorateToStringValue(lists(2)) + " did not contain only " + "(1, 2, 3)" + " in order (ListShouldContainInOrderOnlySpec.scala:" + (thisLineNumber - 5) + ") \n" +
                                   "in " + decorateToStringValue(lists)))

        val e4 = intercept[TestFailedException] {
          all (listsNil) should (newContain newInOrderOnly (1, 2, 3))
        }
        e4.failedCodeFileName.get should be ("ListShouldContainInOrderOnlySpec.scala")
        e4.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e4.message should be (Some("'all' inspection failed, because: \n" +
                                   "  at index 2, " + decorateToStringValue(Nil) + " did not contain only " + "(1, 2, 3)" + " in order (ListShouldContainInOrderOnlySpec.scala:" + (thisLineNumber - 5) + ") \n" +
                                   "in " + decorateToStringValue(listsNil)))
      }

      def `should use the implicit Equality in scope` {
        all (hiLists) should (newContain newInOrderOnly ("hi", "he"))
        intercept[TestFailedException] {
          all (hiLists) should (newContain newInOrderOnly ("he", "hi"))
        }
        implicit val ise = upperCaseStringEquality
        all (hiLists) should (newContain newInOrderOnly ("HI", "HE"))
        intercept[TestFailedException] {
          all (hiLists) should (newContain newInOrderOnly ("HI", "HO"))
        }
      }
      def `should use an explicitly provided Equality` {
        (all (hiLists) should (newContain newInOrderOnly ("HI", "HE"))) (decided by upperCaseStringEquality)
        intercept[TestFailedException] {
          (all (hiLists) should (newContain newInOrderOnly ("HI", "HO"))) (decided by upperCaseStringEquality)
        }
        implicit val ise = upperCaseStringEquality
        (all (hiLists) should (newContain newInOrderOnly ("hi", "he"))) (decided by defaultEquality[String])
        intercept[TestFailedException] {
          (all (hiLists) should (newContain newInOrderOnly ("he", "hi"))) (decided by defaultEquality[String])
        }
      }
    }

    object `when used with not contain inOrderOnly (..)` {

      def `should do nothing if valid, else throw a TFE with an appropriate error message` {
        all (toLists) should not newContain newInOrderOnly ("you", "to")
        val e1 = intercept[TestFailedException] {
          all (toLists) should not newContain newInOrderOnly ("to", "you")
        }
        e1.failedCodeFileName.get should be ("ListShouldContainInOrderOnlySpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message should be (Some("'all' inspection failed, because: \n" +
                                   "  at index 0, " + decorateToStringValue(List("to", "you")) + " contained only " + "(\"to\", \"you\")" +  " in order (ListShouldContainInOrderOnlySpec.scala:" + (thisLineNumber - 5) + ") \n" +
                                   "in " + decorateToStringValue(toLists)))
      }
      def `should use the implicit Equality in scope` {
        implicit val ise = upperCaseStringEquality
        all (toLists) should not newContain newInOrderOnly ("YOU", "TO")
        intercept[TestFailedException] {
          all (toLists) should not newContain newInOrderOnly ("TO", "YOU")
        }
      }
      def `should use an explicitly provided Equality` {
        (all (toLists) should not newContain newInOrderOnly ("YOU", "TO")) (decided by upperCaseStringEquality)
        intercept[TestFailedException] {
          (all (toLists) should not newContain newInOrderOnly ("TO", "YOU")) (decided by upperCaseStringEquality)
        }
        all (toLists) should not newContain newInOrderOnly (" TO ", " YOU ")
        intercept[TestFailedException] {
          (all (toLists) should not newContain newInOrderOnly (" TO ", " YOU ")) (after being lowerCased and trimmed)
        }
      }
    }

/*
    object `when used with (not contain theSameElementsInOrderAs (..))` {

      def `should do nothing if valid, else throw a TFE with an appropriate error message` {
        all (toLists) should (not newContain newTheSameElementsInOrderAs (LinkedList("you", "to")))
        val e1 = intercept[TestFailedException] {
          all (toLists) should (not newContain newTheSameElementsInOrderAs (LinkedList("to", "you")))
        }
        e1.failedCodeFileName.get should be ("ListShouldContainInOrderOnlySpec.scala")
        e1.failedCodeLineNumber.get should be (thisLineNumber - 3)
        e1.message should be (Some("'all' inspection failed, because: \n" +
                                   "  at index 0, " + decorateToStringValue(List("to", "you")) + " contained the same elements in the same (iterated) order as " + decorateToStringValue(LinkedList("to", "you")) + " (ListShouldContainInOrderOnlySpec.scala:" + (thisLineNumber - 5) + ") \n" +
                                   "in " + decorateToStringValue(toLists)))
      }
      def `should use the implicit Equality in scope` {
        implicit val ise = upperCaseStringEquality
        all (toLists) should (not newContain newTheSameElementsInOrderAs (LinkedList("YOU", "TO")))
        intercept[TestFailedException] {
          all (toLists) should (not newContain newTheSameElementsInOrderAs (LinkedList("TO", "YOU")))
        }
      }
      def `should use an explicitly provided Equality` {
        (all (toLists) should (not newContain newTheSameElementsInOrderAs (LinkedList("YOU", "TO")))) (decided by upperCaseStringEquality)
        intercept[TestFailedException] {
          (all (toLists) should (not newContain newTheSameElementsInOrderAs (LinkedList("TO", "YOU")))) (decided by upperCaseStringEquality)
        }
        all (toLists) should (not newContain newTheSameElementsInOrderAs (LinkedList(" TO ", " YOU ")))
        intercept[TestFailedException] {
          (all (toLists) should (not newContain newTheSameElementsInOrderAs (LinkedList(" TO ", " YOU ")))) (after being lowerCased and trimmed)
        }
      }
    }
*/
  }
}
